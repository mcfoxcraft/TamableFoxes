package net.seanomik.tamablefoxes;

import net.seanomik.tamablefoxes.util.NMSInterface;
import net.seanomik.tamablefoxes.util.Utils;
import net.seanomik.tamablefoxes.util.io.Config;
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

    private boolean equalOrBetween(double num, double min, double max) {
        return num >= min && num <= max;
    }

    @Override
    public void onLoad() {
        plugin = this;
        Utils.tamableFoxesPlugin = this;

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

            default -> {
                Bukkit.getServer().getConsoleSender().sendMessage(Config.getPrefix() + ChatColor.RED + LanguageConfig.getUnsupportedMCVersionRegister());
                Bukkit.getServer().getConsoleSender().sendMessage(Config.getPrefix() + ChatColor.RED + "You're trying to run MC version " + Bukkit.getMinecraftVersion() + " which is not supported!");
                Bukkit.getServer().getConsoleSender().sendMessage(Config.getPrefix() + "Disabling plugin...");
                versionSupported = false;

                Bukkit.getPluginManager().disablePlugin(this);
            }
        }

        if (versionSupported) {
            // Display starting message then register entity.
            Bukkit.getServer().getConsoleSender().sendMessage(Config.getPrefix() + ChatColor.YELLOW + LanguageConfig.getMCVersionLoading(Bukkit.getMinecraftVersion()));
            nmsInterface.registerCustomFoxEntity();

            if (Config.getMaxPlayerFoxTames() != 0) {
                SQLiteHelper.getInstance(this).createTablesIfNotExist();
            }
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
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
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
    }

    public static TamableFoxes getPlugin() {
        return plugin;
    }
}
