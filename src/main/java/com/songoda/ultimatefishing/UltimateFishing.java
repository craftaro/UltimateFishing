package com.songoda.ultimatefishing;

import com.songoda.core.SongodaCore;
import com.songoda.core.library.commands.CommandManager;
import com.songoda.core.library.economy.EconomyManager;
import com.songoda.ultimatefishing.command.commands.*;
import com.songoda.ultimatefishing.listeners.EntityListeners;
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

    public static UltimateFishing getInstance() {
        return INSTANCE;
    }

    @Override
    public void onDisable() {
        console.sendMessage(Methods.formatText("&a============================="));
        console.sendMessage(Methods.formatText("&7UltimateFishing " + this.getDescription().getVersion() + " by &5Songoda <3!"));
        console.sendMessage(Methods.formatText("&7Action: &cDisabling&7..."));
        console.sendMessage(Methods.formatText("&a============================="));
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        console.sendMessage(Methods.formatText("&a============================="));
        console.sendMessage(Methods.formatText("&7UltimateFishing " + this.getDescription().getVersion() + " by &5Songoda <3&7!"));
        console.sendMessage(Methods.formatText("&7Action: &aEnabling&7..."));
        console.sendMessage(Methods.formatText("&a============================="));

        this.settingsManager = new SettingsManager(this);
        this.settingsManager.setupConfig();

        this.commandManager = new CommandManager(this);
        this.commandManager.addCommand(new CommandUltimateFishing(this))
                .addSubCommands(
                        new CommandSell(this),
                        new CommandSellAll(this),
                        new CommandSettings(this),
                        new CommandReload(this)
                );

        new Locale(this, "en_US");
        this.locale = Locale.getLocale(getConfig().getString("System.Language Mode"));

        // Setup Lootables
        this.lootablesManager = new LootablesManager(this);
        this.lootablesManager.createDefaultLootables();
        this.getLootablesManager().getLootManager().loadLootables();

        //Running Songoda Updater
        SongodaCore.registerPlugin(this, 59);

        PluginManager pluginManager = Bukkit.getPluginManager();

        // Load Economy
        EconomyManager.load();

        // Setup Listeners
        pluginManager.registerEvents(new FishingListeners(this), this);
        pluginManager.registerEvents(new FurnaceListeners(this), this);
        pluginManager.registerEvents(new EntityListeners(this), this);

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
        this.runRarityDefaults();
    }

    /*
     * Insert default fish sizes into config.
     */
    private void runRarityDefaults() {
        if (!rarityFile.getConfig().contains("Rarity")) {
            rarityFile.getConfig().set("Rarity.Tiny.Chance", 15);
            rarityFile.getConfig().set("Rarity.Tiny.Color", "9");
            rarityFile.getConfig().set("Rarity.Tiny.Extra Health", -2);
            rarityFile.getConfig().set("Rarity.Tiny.Sell Price", 4.99);
            rarityFile.getConfig().set("Rarity.Tiny.Lure Chance Change", -5);
            rarityFile.getConfig().set("Rarity.Normal.Chance", 50);
            rarityFile.getConfig().set("Rarity.Normal.Color", "7");
            rarityFile.getConfig().set("Rarity.Normal.Extra Health", 0);
            rarityFile.getConfig().set("Rarity.Normal.Sell Price", 19.99);
            rarityFile.getConfig().set("Rarity.Normal.Lure Chance Change", -8);
            rarityFile.getConfig().set("Rarity.Large.Chance", 25);
            rarityFile.getConfig().set("Rarity.Large.Color", "c");
            rarityFile.getConfig().set("Rarity.Large.Extra Health", 2);
            rarityFile.getConfig().set("Rarity.Large.Sell Price", 49.99);
            rarityFile.getConfig().set("Rarity.Large.Lure Chance Change", 5);
            rarityFile.getConfig().set("Rarity.Huge.Chance", 10);
            rarityFile.getConfig().set("Rarity.Huge.Color", "5");
            rarityFile.getConfig().set("Rarity.Huge.Extra Health", 4);
            rarityFile.getConfig().set("Rarity.Huge.Sell Price", 99.99);
            rarityFile.getConfig().set("Rarity.Huge.Broadcast", true);
            rarityFile.getConfig().set("Rarity.Huge.Lure Chance Change", 8);
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
                        raritySection.getInt("Extra Health"),
                        raritySection.getDouble("Sell Price"),
                        raritySection.getBoolean("Broadcast"),
                        raritySection.getDouble("Lure Chance Change")));
            }
        }
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
