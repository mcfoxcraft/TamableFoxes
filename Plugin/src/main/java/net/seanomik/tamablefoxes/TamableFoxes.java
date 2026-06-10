package net.seanomik.tamablefoxes;

import net.seanomik.tamablefoxes.util.NMSInterface;
import net.seanomik.tamablefoxes.util.Utils;
import net.seanomik.tamablefoxes.util.io.Config;
import net.seanomik.tamablefoxes.util.io.sqlite.SQLiteHandler;
import net.seanomik.tamablefoxes.util.io.sqlite.SQLiteHelper;
import net.seanomik.tamablefoxes.versions.version_1_14_R1.NMSInterface_1_14_R1;
import net.seanomik.tamablefoxes.versions.version_1_15_R1.NMSInterface_1_15_R1;
import net.seanomik.tamablefoxes.versions.version_1_16_R1.NMSInterface_1_16_R1;
import net.seanomik.tamablefoxes.versions.version_1_16_R2.NMSInterface_1_16_R2;
import net.seanomik.tamablefoxes.versions.version_1_16_R3.NMSInterface_1_16_R3;
import net.seanomik.tamablefoxes.versions.version_1_17_R1.NMSInterface_1_17_R1;
import net.seanomik.tamablefoxes.versions.version_1_17_1_R1.NMSInterface_1_17_1_R1;
import net.seanomik.tamablefoxes.util.io.LanguageConfig;

import net.seanomik.tamablefoxes.versions.version_1_18_1_R1.NMSInterface_1_18_1_R1;
import net.seanomik.tamablefoxes.versions.version_1_18_R1.NMSInterface_1_18_R1;
import net.seanomik.tamablefoxes.versions.version_1_18_R2.NMSInterface_1_18_R2;
import net.seanomik.tamablefoxes.versions.version_1_19_R1.NMSInterface_1_19_R1;
import net.seanomik.tamablefoxes.versions.version_1_19_1_R1.NMSInterface_1_19_1_R1;
import net.seanomik.tamablefoxes.versions.version_1_19_2_R1.NMSInterface_1_19_2_R1;
import net.seanomik.tamablefoxes.versions.version_1_19_3_R1.NMSInterface_1_19_3_R1;
import net.seanomik.tamablefoxes.versions.version_1_19_R3.NMSInterface_1_19_4_R1;
import net.seanomik.tamablefoxes.versions.version_1_20_R1.NMSInterface_1_20_R1;
import net.seanomik.tamablefoxes.versions.version_1_20_R3.NMSInterface_1_20_R3;
import net.seanomik.tamablefoxes.versions.version_1_21_10_R1.NMSInterface_1_21_10_R1;
import net.seanomik.tamablefoxes.versions.version_1_21_11_R1.NMSInterface_1_21_11_R1;
import net.seanomik.tamablefoxes.versions.version_1_21_R1.NMSInterface_1_21_R1;
import org.bukkit.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.logging.Level;
import net.seanomik.tamablefoxes.versions.version_1_21_4_R1.NMSInterface_1_21_4_R1;
import net.seanomik.tamablefoxes.versions.version_1_21_5_R1.NMSInterface_1_21_5_R1;
import net.seanomik.tamablefoxes.versions.version_1_21_8_R1.NMSInterface_1_21_8_R1;

public final class TamableFoxes extends JavaPlugin implements Listener {
    private static TamableFoxes plugin;
    public static final int BSTATS_PLUGIN_ID = 11944;

    private boolean versionSupported = true;

    public NMSInterface nmsInterface;
    private PlayerInteractEntityEventListener playerInteractEntityEventListener;

    @Override
    public void onLoad() {
        plugin = this;
        Utils.setTamableFoxesPlugin(this);

        Config.setConfig(this.getConfig());
        LanguageConfig.getConfig(this).saveDefault();

        // Verify server version
        // FOX
        switch (Bukkit.getMinecraftVersion()) {
            case "1.14", "1.14.1", "1.14.2", "1.14.3", "1.14.4" -> nmsInterface = new NMSInterface_1_14_R1();
            case "1.15", "1.15.1", "1.15.2" -> nmsInterface = new NMSInterface_1_15_R1();
            case "1.16" -> nmsInterface = new NMSInterface_1_16_R1();
            case "1.16.2", "1.16.3" -> nmsInterface = new NMSInterface_1_16_R2();
            case "1.16.4", "1.16.5" -> nmsInterface = new NMSInterface_1_16_R3();
            case "1.17" -> nmsInterface = new NMSInterface_1_17_R1();
            case "1.17.1" -> nmsInterface = new NMSInterface_1_17_1_R1();
            case "1.18" -> nmsInterface = new NMSInterface_1_18_R1();
            case "1.18.1" -> nmsInterface = new NMSInterface_1_18_1_R1();
            case "1.18.2" -> nmsInterface = new NMSInterface_1_18_R2();
            case "1.19" -> nmsInterface = new NMSInterface_1_19_R1();
            case "1.19.1" -> nmsInterface = new NMSInterface_1_19_1_R1();
            case "1.19.2" -> nmsInterface = new NMSInterface_1_19_2_R1();
            case "1.19.3" -> nmsInterface = new NMSInterface_1_19_3_R1();
            case "1.19.4" -> nmsInterface = new NMSInterface_1_19_4_R1();
            case "1.20", "1.20.1" -> nmsInterface = new NMSInterface_1_20_R1();
            case "1.20.3", "1.20.4" -> nmsInterface = new NMSInterface_1_20_R3();
            case "1.21", "1.21.1" -> nmsInterface = new NMSInterface_1_21_R1(); // FOX
            case "1.21.4" -> nmsInterface = new NMSInterface_1_21_4_R1();
            case "1.21.5" -> nmsInterface = new NMSInterface_1_21_5_R1();
            case "1.21.8" -> nmsInterface = new NMSInterface_1_21_8_R1();
            case "1.21.9", "1.21.10" -> nmsInterface = new NMSInterface_1_21_10_R1();
            case "1.21.11" -> nmsInterface = new NMSInterface_1_21_11_R1();
            // FOX: Paper/Spigot 26.x are Mojang-mapped with an unversioned craftbukkit
            // package, so 26.1.2 needs its own (non-remapped) module. It is loaded via
            // reflection so older servers never resolve the module's classes and a
            // load failure (including LinkageError) disables the plugin cleanly.
            case "26.1.2" -> nmsInterface = loadNMSInterfaceByName("net.seanomik.tamablefoxes.versions.version_26_1_R1.NMSInterface_26_1_R1");

            default -> abortLoad(LanguageConfig.getUnsupportedMCVersionRegister(),
                    "You're trying to run MC version " + Bukkit.getMinecraftVersion() + " which is not supported!");
        }

        // FOX: a reflection-loaded interface can fail to load even on a supported
        // version; the cause was already printed by loadNMSInterfaceByName, so don't
        // claim the MC version is unsupported.
        if (versionSupported && nmsInterface == null) {
            abortLoad("Failed to load NMS support for MC " + Bukkit.getMinecraftVersion() + ".");
        }

        if (versionSupported) {
            // Display starting message then register entity.
            Bukkit.getServer().getConsoleSender().sendMessage(Config.getPrefix() + ChatColor.YELLOW + LanguageConfig.getMCVersionLoading(Bukkit.getMinecraftVersion()));
            try {
                nmsInterface.registerCustomFoxEntity();
            } catch (Exception | LinkageError e) {
                // FOX: newer interfaces rethrow on registration failure so the plugin
                // disables cleanly instead of running half-enabled with vanilla foxes.
                // LinkageError is included because the module's entity classes are first
                // loaded inside this call (e.g. the EntityTamableFox::new factory ref);
                // an uncaught Error here would leave versionSupported=true and onEnable
                // would still run half-enabled.
                e.printStackTrace();
                abortLoad("Failed to register the custom fox entity for MC " + Bukkit.getMinecraftVersion() + ".");
                return;
            }

            if (Config.getMaxPlayerFoxTames() != 0) {
                SQLiteHelper.getInstance(this).createTablesIfNotExist();
            }
        }
    }

    // FOX: shared failure path for onLoad. Only flips versionSupported — the actual
    // disable happens in onEnable, since a plugin cannot be disabled before it has
    // been enabled (PluginManager#disablePlugin is a no-op during onLoad).
    private void abortLoad(String... reasonLines) {
        for (String line : reasonLines) {
            Bukkit.getServer().getConsoleSender().sendMessage(Config.getPrefix() + ChatColor.RED + line);
        }
        Bukkit.getServer().getConsoleSender().sendMessage(Config.getPrefix() + "Disabling plugin...");
        versionSupported = false;
    }

    // FOX: loads an NMS module by name so its classes are only resolved on the matching
    // server version, and so any load failure (including LinkageError, e.g. a missing
    // or incompatible server class) is caught here instead of escaping onLoad.
    // Returns null when loading fails.
    private NMSInterface loadNMSInterfaceByName(String className) {
        try {
            return (NMSInterface) Class.forName(className).getDeclaredConstructor().newInstance();
        } catch (Exception | LinkageError e) {
            Bukkit.getServer().getConsoleSender().sendMessage(Config.getPrefix() + ChatColor.RED + "Failed to load NMS support class " + className + ":");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onEnable() {
        if (!versionSupported) {
            Bukkit.getServer().getConsoleSender().sendMessage(Config.getPrefix() + ChatColor.RED + LanguageConfig.getUnsupportedMCVersionDisable());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        playerInteractEntityEventListener = new PlayerInteractEntityEventListener(this);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(playerInteractEntityEventListener, this);
        this.getCommand("spawntamablefox").setExecutor(new CommandSpawnTamableFox(this));
        this.getCommand("tamablefoxes").setExecutor(new CommandTamableFoxes(this));
        this.getCommand("givefox").setExecutor(new CommandGiveFox(this, playerInteractEntityEventListener));

        this.saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public void saveResource(String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + getFile());
        }

        File outFile = new File(getDataFolder(), resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(getDataFolder(), resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                try (InputStream inputStream = in; OutputStream out = new FileOutputStream(outFile)) {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                }
            }
            // Ignore could not save because it already exists.
            /* else {
                getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }*/
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(Config.getPrefix() + ChatColor.YELLOW + LanguageConfig.getSavingFoxMessage());
        SQLiteHandler.getInstance().closeConnection();
    }

    public static TamableFoxes getPlugin() {
        return plugin;
    }
}
