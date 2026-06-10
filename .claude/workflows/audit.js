export const meta = {
  name: 'audit',
  description: 'Multi-agent code review of the current diff: scope → review across dimensions → adversarially verify each finding → synthesize',
  whenToUse: 'Reviewing a branch/PR/diff for bugs, security, and quality with verified findings. Pass args as a base ref ("master"), a path, or a description like "PR #123" to scope the review; defaults to the diff vs the default branch.',
  phases: [
    { title: 'Scope', detail: 'identify changed files and the review target' },
    { title: 'Review', detail: 'one reviewer per dimension' },
    { title: 'Verify', detail: 'adversarially confirm each finding is real' },
    { title: 'Synthesize', detail: 'merge into a single report' },
  ],
}

// ---- Schemas ----------------------------------------------------------------

const SCOPE_SCHEMA = {
  type: 'object',
  additionalProperties: false,
  required: ['baseRef', 'changedFiles', 'summary'],
  properties: {
    baseRef: { type: 'string', description: 'The git ref the diff is compared against (e.g. merge-base with master), or "working-tree" for uncommitted changes.' },
    changedFiles: {
      type: 'array',
      items: { type: 'string' },
      description: 'Repo-relative paths of files changed in this review scope.',
    },
    summary: { type: 'string', description: '2-4 sentence plain-language summary of what the diff does.' },
    diffCommand: { type: 'string', description: 'The exact git command a reviewer can run to see the full diff for this scope.' },
  },
}

const FINDINGS_SCHEMA = {
  type: 'object',
  additionalProperties: false,
  required: ['findings'],
  properties: {
    findings: {
      type: 'array',
      items: {
        type: 'object',
        additionalProperties: false,
        required: ['title', 'file', 'line', 'severity', 'detail'],
        properties: {
          title: { type: 'string', description: 'One-line description of the issue.' },
          file: { type: 'string', description: 'Repo-relative path.' },
          line: { type: 'string', description: 'Line number or range (e.g. "42" or "42-50"). "unknown" if not pinpointable.' },
          severity: { type: 'string', enum: ['critical', 'high', 'medium', 'low'] },
          detail: { type: 'string', description: 'What is wrong, why it matters, and a concrete fix.' },
        },
      },
    },
  },
}

const VERDICT_SCHEMA = {
  type: 'object',
  additionalProperties: false,
  required: ['isReal', 'confidence', 'reasoning'],
  properties: {
    isReal: { type: 'boolean', description: 'True only if the finding is a genuine issue introduced or exposed by this diff.' },
    confidence: { type: 'string', enum: ['high', 'medium', 'low'] },
    reasoning: { type: 'string', description: 'Why the finding holds or is refuted, citing the actual code.' },
  },
}

// ---- Dimensions -------------------------------------------------------------

const DIMENSIONS = [
  {
    key: 'correctness',
    prompt: 'logic errors, off-by-one, null/None handling, incorrect conditionals, broken control flow, wrong API usage, race conditions, resource leaks (unclosed streams/connections), and regressions vs the pre-diff behavior.',
  },
  {
    key: 'security',
    prompt: 'injection (SQL/command/path), unsafe deserialization, missing authz/permission checks, secrets in code, unsafe reflection, SSRF, and untrusted input reaching sensitive sinks.',
  },
  {
    key: 'api-contract',
    prompt: 'breaking changes to public/plugin APIs, event-handling correctness, thread/scheduler-safety (main-thread vs async), backwards-incompatible config or data-format changes, and version-compat assumptions.',
  },
  {
    key: 'quality',
    prompt: 'duplicated logic that should reuse existing helpers, dead code, needless complexity, misleading names, and missing error handling. Only report items material to maintainability — skip pure style nits.',
  },
]

// ---- Script -----------------------------------------------------------------

const target = typeof args === 'string' && args.trim() ? args.trim() : null

phase('Scope')
const scope = await agent(
  `You are scoping a code review. Determine the set of changes to review.
${target ? `The user specified this review target: "${target}". Honor it (it may be a base ref like "master", a path, or a PR reference).` : 'No target was given. Review the current branch diff against the default branch (find it via the merge-base with origin/HEAD, origin/main, or origin/master), and if there is no diff there, review uncommitted working-tree changes.'}

Use Bash (git) to inspect the repo. Identify the base ref, the list of changed files, and a one-paragraph summary of what the diff does. Also give the exact git command that reproduces the full diff. Do NOT review for issues yet — just scope.`,
  { phase: 'Scope', label: 'scope', schema: SCOPE_SCHEMA },
)

if (!scope.changedFiles.length) {
  log('No changed files found in scope — nothing to review.')
  return { scope, findings: [], report: 'No changes were in scope, so there was nothing to review.' }
}

log(`Reviewing ${scope.changedFiles.length} changed file(s) against ${scope.baseRef}.`)

// Pipeline: each dimension reviews, then its findings verify as soon as they land.
const reviewContext = `Review target base ref: ${scope.baseRef}
Reproduce the full diff with: ${scope.diffCommand || 'git diff ' + scope.baseRef}
Changed files:
${scope.changedFiles.map(f => '  - ' + f).join('\n')}

What the diff does: ${scope.summary}`

const verified = await pipeline(
  DIMENSIONS,
  d =>
    agent(
      `You are a senior reviewer focused on ONE dimension: ${d.key}.
${reviewContext}

Run the diff command and Read the changed files as needed. Report only issues in your dimension that are introduced or exposed by THIS diff: ${d.prompt}
Pin each finding to a file and line. Be precise; do not pad the list. Return an empty list if you find nothing real.`,
      { phase: 'Review', label: `review:${d.key}`, schema: FINDINGS_SCHEMA },
    ),
  (review, d) =>
    parallel(
      review.findings.map(f => () =>
        agent(
          `Adversarially verify this ${d.key} finding from a code review. Try to REFUTE it. Read the actual code at ${f.file}:${f.line} (and surrounding context) before deciding.

Finding: ${f.title}
Detail: ${f.detail}

Confirm isReal=true ONLY if it is a genuine issue caused or exposed by the diff under review. If it is pre-existing-but-unchanged, a false positive, or you cannot confirm it from the code, lean toward isReal=false and explain.`,
          { phase: 'Verify', label: `verify:${f.file}`, schema: VERDICT_SCHEMA },
        ).then(v => ({ ...f, dimension: d.key, verdict: v })),
      ),
    ),
)

const confirmed = verified
  .flat()
  .filter(Boolean)
  .filter(f => f.verdict && f.verdict.isReal)
  .sort((a, b) => {
    const order = { critical: 0, high: 1, medium: 2, low: 3 }
    return order[a.severity] - order[b.severity]
  })

phase('Synthesize')
const report = await agent(
  `Write a concise code-review report in Markdown from these verified findings. Group by severity (Critical, High, Medium, Low). For each: a bold one-line title, the file:line, and a short why + fix. If a severity group is empty, omit it. End with a one-sentence overall verdict (e.g. safe to merge / needs changes). Do not invent findings beyond those provided.

Scope summary: ${scope.summary}

Verified findings (JSON):
${JSON.stringify(confirmed, null, 2)}`,
  { phase: 'Synthesize', label: 'synthesize' },
)

return {
  baseRef: scope.baseRef,
  changedFiles: scope.changedFiles,
  confirmedCount: confirmed.length,
  findings: confirmed,
  report,
}
