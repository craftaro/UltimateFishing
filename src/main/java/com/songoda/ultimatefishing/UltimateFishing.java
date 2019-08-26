package com.songoda.ultimatefishing;

import com.songoda.core.SongodaCore;
import com.songoda.core.commands.CommandManager;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.core.settings.Config;
import com.songoda.core.settings.Section;
import com.songoda.ultimatefishing.command.commands.*;
import com.songoda.ultimatefishing.listeners.EntityListeners;
import com.songoda.ultimatefishing.listeners.FishingListeners;
import com.songoda.ultimatefishing.listeners.FurnaceListeners;
import com.songoda.ultimatefishing.lootables.LootablesManager;
import com.songoda.ultimatefishing.rarity.Rarity;
import com.songoda.ultimatefishing.rarity.RarityManager;
import com.songoda.ultimatefishing.utils.Methods;
import com.songoda.ultimatefishing.utils.Metrics;
import com.songoda.ultimatefishing.utils.locale.Locale;
import com.songoda.ultimatefishing.utils.settings.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class UltimateFishing extends JavaPlugin {

    private static UltimateFishing INSTANCE;

    private Config rarityConfig = new Config(this, "rarity.yml");

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
        setupRarity();
    }

    public void reload() {
        this.locale = Locale.getLocale(getConfig().getString("System.Language Mode"));
        this.locale.reloadMessages();
        this.settingsManager.reloadConfig();
        this.getLootablesManager().getLootManager().loadLootables();
        this.setupRarity();
    }

    /*
     * Insert default fish sizes into config.
     */
    private void setupRarity() {
        this.rarityConfig.reload();

        rarityConfig.addCategory("Rarity", "The different levels of fish rarity",
                "You can rename, replace and add new fish as you wish.")
                .addDefaultSetting("Tiny.Chance", 15,
                        "The chance that a caught fish will be tiny.")
                .addDefaultSetting("Tiny.Color", "9",
                        "The color used for the name tag.")
                .addDefaultSetting("Tiny.Extra Health", -2,
                        "The amount of health on top of the initial health that the caught",
                        "fish grants.")
                .addDefaultSetting("Tiny.Sell Price", 4.99,
                        "The price tiny fish will sell for.")
                .addDefaultSetting("Tiny.Lure Chance Change", -5,
                        "The effect the lure fishing enchantment would have on the chance.",
                        "This is multiplied per enchantment level.")
                .addDefaultSetting("Normal.Chance", 50)
                .addDefaultSetting("Normal.Color", "7")
                .addDefaultSetting("Normal.Extra Health", 0)
                .addDefaultSetting("Normal.Sell Price", 19.99)
                .addDefaultSetting("Normal.Lure Chance Change", -8)
                .addDefaultSetting("Large.Chance", 25)
                .addDefaultSetting("Large.Color", "c")
                .addDefaultSetting("Large.Extra Health", 2)
                .addDefaultSetting("Large.Sell Price", 49.99)
                .addDefaultSetting("Large.Lure Chance Change", 5)
                .addDefaultSetting("Huge.Chance", 10)
                .addDefaultSetting("Huge.Color", "5")
                .addDefaultSetting("Huge.Extra Health", 4)
                .addDefaultSetting("Huge.Sell Price", 99.99)
                .addDefaultSetting("Huge.Broadcast", true,
                        "Should we broadcast a message to all players when a huge fish",
                        "is caught?")
                .addDefaultSetting("Huge.Lure Chance Change", 8);

        this.rarityConfig.categorySpacing(false).commentSpacing(false).setup();

        this.rarityManager = new RarityManager();

        /*
         * Register rarities into RarityManager from Configuration.
         */
        if (rarityConfig.hasCategory("Rarity")) {
            for (Section section : rarityConfig.getCategory("Rarity").getSection()) {
                this.rarityManager.addRarity(new Rarity(section.getKey(),
                        section.narrow("Color").getString(),
                        section.narrow("Chance").getDouble(),
                        section.narrow("Extra Health").getInt(),
                        section.narrow("Sell Price").getDouble(),
                        section.narrow("Broadcast").getBoolean(),
                        section.narrow("Lure Chance Change").getDouble()));
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
