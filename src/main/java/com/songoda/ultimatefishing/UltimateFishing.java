package com.songoda.ultimatefishing;

import com.songoda.ultimatefishing.command.CommandManager;
import com.songoda.ultimatefishing.listeners.FishingListeners;
import com.songoda.ultimatefishing.listeners.FurnaceListeners;
import com.songoda.ultimatefishing.lootables.LootablesManager;
import com.songoda.ultimatefishing.rarity.Rarity;
import com.songoda.ultimatefishing.rarity.RarityManager;
import com.songoda.ultimatefishing.utils.ConfigWrapper;
import com.songoda.ultimatefishing.utils.Methods;
import com.songoda.ultimatefishing.utils.Metrics;
import com.songoda.ultimatefishing.utils.locale.Locale;
import com.songoda.ultimatefishing.utils.settings.SettingsManager;
import com.songoda.ultimatefishing.utils.updateModules.LocaleModule;
import com.songoda.update.Plugin;
import com.songoda.update.SongodaUpdate;
import com.songoda.update.utils.ServerVersion;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class UltimateFishing extends JavaPlugin {

    private static UltimateFishing INSTANCE;

    private ConfigWrapper rarityFile = new ConfigWrapper(this, "", "rarity.yml");

    private Locale locale;
    private SettingsManager settingsManager;
    private LootablesManager lootablesManager;
    private CommandManager commandManager;
    private RarityManager rarityManager;

    private ConsoleCommandSender console = Bukkit.getConsoleSender();

    private ServerVersion serverVersion = ServerVersion.fromPackageName(Bukkit.getServer().getClass().getPackage().getName());

    public static UltimateFishing getInstance() {
        return INSTANCE;
    }

    public void onDisable() {
        console.sendMessage(Methods.formatText("&a============================="));
        console.sendMessage(Methods.formatText("&7UltimateFishing " + this.getDescription().getVersion() + " by &5Songoda <3!"));
        console.sendMessage(Methods.formatText("&7Action: &cDisabling&7..."));
        console.sendMessage(Methods.formatText("&a============================="));
    }

    public void onEnable() {
        INSTANCE = this;
        console.sendMessage(Methods.formatText("&a============================="));
        console.sendMessage(Methods.formatText("&7UltimateFishing " + this.getDescription().getVersion() + " by &5Songoda <3&7!"));
        console.sendMessage(Methods.formatText("&7Action: &aEnabling&7..."));
        console.sendMessage(Methods.formatText("&a============================="));

        this.settingsManager = new SettingsManager(this);
        this.settingsManager.setupConfig();

        this.commandManager = new CommandManager(this);

        new Locale(this, "en_US");
        this.locale = Locale.getLocale(getConfig().getString("System.Language Mode"));

        // Setup Lootables
        this.lootablesManager = new LootablesManager(this);
        this.lootablesManager.createDefaultLootables();
        this.getLootablesManager().getLootManager().loadLootables();

        //Running Songoda Updater
        Plugin plugin = new Plugin(this, 59);
        plugin.addModule(new LocaleModule());
        SongodaUpdate.load(plugin);

        PluginManager pluginManager = Bukkit.getPluginManager();

        // Setup Listeners
        pluginManager.registerEvents(new FishingListeners(this), this);
        pluginManager.registerEvents(new FurnaceListeners(this), this);


        // Starting Metrics
        new Metrics(this);

        //Apply default fish rarity.
        runRarityDefaults();
    }

    public void reload() {
        this.locale = Locale.getLocale(getConfig().getString("System.Language Mode"));
        this.locale.reloadMessages();
        this.settingsManager.reloadConfig();
        this.getLootablesManager().getLootManager().loadLootables();
    }

    /*
     * Insert default fish sizes into config.
     */
    private void runRarityDefaults() {
        if (!rarityFile.getConfig().contains("Rarity")) {
            rarityFile.getConfig().set("Rarity.Common.Chance", 60);
            rarityFile.getConfig().set("Rarity.Common.Color", "7");
            rarityFile.getConfig().set("Rarity.Common.SizeMin", 50);
            rarityFile.getConfig().set("Rarity.Common.SizeMax", 75);
            rarityFile.getConfig().set("Rarity.Rare.Chance", 30);
            rarityFile.getConfig().set("Rarity.Rare.Color", "c");
            rarityFile.getConfig().set("Rarity.Rare.SizeMin", 75);
            rarityFile.getConfig().set("Rarity.Rare.SizeMax", 100);
            rarityFile.getConfig().set("Rarity.Epic.Chance", 10);
            rarityFile.getConfig().set("Rarity.Epic.Color", "5");
            rarityFile.getConfig().set("Rarity.Epic.SizeMin", 100);
            rarityFile.getConfig().set("Rarity.Epic.SizeMax", 150);
            rarityFile.saveConfig();
        }

        this.rarityManager = new RarityManager();

        /*
         * Register rarities into RarityManager from Configuration.
         */
        if (rarityFile.getConfig().contains("Rarity")) {
            for (String keyName : rarityFile.getConfig().getConfigurationSection("Rarity").getKeys(false)) {
                ConfigurationSection raritySection = rarityFile.getConfig().getConfigurationSection("Rarity." + keyName);

                this.rarityManager.addRarity(new Rarity(keyName,
                        raritySection.getString("Color"),
                        raritySection.getDouble("Chance"),
                        raritySection.getDouble("SizeMin"),
                        raritySection.getDouble("SizeMax")));
            }
        }
    }

    public ServerVersion getServerVersion() {
        return serverVersion;
    }

    public boolean isServerVersion(ServerVersion version) {
        return serverVersion == version;
    }

    public boolean isServerVersion(ServerVersion... versions) {
        return ArrayUtils.contains(versions, serverVersion);
    }

    public boolean isServerVersionAtLeast(ServerVersion version) {
        return serverVersion.ordinal() >= version.ordinal();
    }

    public LootablesManager getLootablesManager() {
        return lootablesManager;
    }

    public Locale getLocale() {
        return locale;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public RarityManager getRarityManager() {
        return rarityManager;
    }
}
