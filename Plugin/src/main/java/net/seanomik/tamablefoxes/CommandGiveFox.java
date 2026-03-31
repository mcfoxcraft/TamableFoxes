package net.seanomik.tamablefoxes;

import net.seanomik.tamablefoxes.util.io.Config;
import net.seanomik.tamablefoxes.util.io.LanguageConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Fox;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class CommandGiveFox implements TabExecutor {

    private final TamableFoxes plugin;
    private final PlayerInteractEntityEventListener playerInteractListener;

    public CommandGiveFox(TamableFoxes plugin, PlayerInteractEntityEventListener playerInteractListener) {
        this.plugin = plugin;
        this.playerInteractListener = playerInteractListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Config.getPrefix() + ChatColor.RED + LanguageConfig.getOnlyRunPlayer());
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Config.getPrefix() + "You didn't supply a player name!");
            sender.sendMessage(Config.getPrefix() + "Usage: /givefox [player name]");
            return true;
        }

        if (!sender.hasPermission("tamablefoxes.givefox.give")) {
            sender.sendMessage(Config.getPrefix() + ChatColor.RED + LanguageConfig.getNoPermMessage());
            return true;
        }

        Player player = (Player) sender;
        Player givingToPlayer = plugin.getServer().getPlayer(args[0]);

        if (givingToPlayer == null) {
            sender.sendMessage(Config.getPrefix() + ChatColor.RED + LanguageConfig.getPlayerDoesNotExist());
            return true;
        }

        if (!givingToPlayer.hasPermission("tamablefoxes.givefox.receive") &&
                !player.hasPermission("tamablefoxes.givefox.give.others")) {
            sender.sendMessage(Config.getPrefix() + ChatColor.RED + LanguageConfig.getGiveFoxOtherNoPermMessage());
            return true;
        }

        sender.sendMessage(Config.getPrefix() + ChatColor.WHITE + LanguageConfig.getInteractWithTransferringFox(givingToPlayer));

        Bukkit.getScheduler().runTaskAsynchronously(plugin, r -> {
            PlayerInteractEntityEventListener.SynchronizeFoxObject syncObject = new PlayerInteractEntityEventListener.SynchronizeFoxObject();
            playerInteractListener.players.put(player.getUniqueId(), syncObject);

            synchronized(syncObject) {
                try {
                    // Use while loop to avoid spurious wakeups and race conditions
                    long startTime = System.currentTimeMillis();
                    long timeout = 30000; // 30 seconds
                    while (!syncObject.foxSet) {
                        long elapsed = System.currentTimeMillis() - startTime;
                        long remaining = timeout - elapsed;
                        if (remaining <= 0) {
                            // Timeout occurred
                            playerInteractListener.players.remove(player.getUniqueId());
                            sender.sendMessage(Config.getPrefix() + ChatColor.RED + LanguageConfig.getTooLongInteraction());
                            return;
                        }
                        syncObject.wait(remaining);
                    }

                    playerInteractListener.players.remove(player.getUniqueId());

                    Fox fox = syncObject.interactedFox;
                    if (fox == null) {
                        sender.sendMessage("§cNo fox selected.");
                        return;
                    }
                    if (player.getUniqueId().equals(plugin.nmsInterface.getFoxOwner(fox)) ||
                            player.hasPermission("tamablefoxes.givefox.give.others")) {
                        plugin.nmsInterface.changeFoxOwner(fox, givingToPlayer);

                        Bukkit.getScheduler().runTask(plugin, r2 -> {
                            // If the player that is receiving the fox is online, prompt them to rename their new fox!
                            if (givingToPlayer.isOnline()) {
                                plugin.nmsInterface.renameFox(fox, givingToPlayer);
                            }
                        });

                        sender.sendMessage(Config.getPrefix() + ChatColor.GREEN + LanguageConfig.getGaveFox(givingToPlayer));
                    } else {
                        sender.sendMessage(Config.getPrefix() + ChatColor.RED + LanguageConfig.getNotYourFox());
                    }
                } catch (InterruptedException e) {
                    playerInteractListener.players.remove(player.getUniqueId());
                    sender.sendMessage(Config.getPrefix() + ChatColor.RED + LanguageConfig.getTooLongInteraction());
                }
            }
        });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> names = plugin.getServer().getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
        return names;
    }
}
