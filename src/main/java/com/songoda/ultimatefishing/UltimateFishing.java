package com.songoda.ultimatefishing;

import com.songoda.core.SongodaCore;
import com.songoda.core.library.commands.CommandManager;
import com.songoda.core.library.economy.EconomyManager;
import com.songoda.core.library.economy.economies.Economy;
import com.songoda.core.library.locale.Locale;
import com.songoda.core.library.settings.Config;
import com.songoda.core.library.settings.Section;
import com.songoda.core.library.settings.SettingsManager;
import com.songoda.ultimatefishing.command.commands.*;
import com.songoda.ultimatefishing.listeners.EntityListeners;
import com.songoda.ultimatefishing.listeners.FishingListeners;
import com.songoda.ultimatefishing.listeners.FurnaceListeners;
import com.songoda.ultimatefishing.lootables.LootablesManager;
import com.songoda.ultimatefishing.rarity.Rarity;
import com.songoda.ultimatefishing.rarity.RarityManager;
import com.songoda.ultimatefishing.utils.Methods;
import com.songoda.ultimatefishing.utils.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.stream.Collectors;

public class UltimateFishing extends JavaPlugin {

    private static UltimateFishing INSTANCE;

    private Config rarityConfig = new Config(this, "rarity.yml");
    private Config config = new Config(this, "config.yml");

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

        // Load Economy
        EconomyManager.load();

        // Setup Config
        setupConfig();

        EconomyManager.setPreferredEconomy(config.getSetting("Main.Economy").getString());

        this.settingsManager = new SettingsManager(config);

        this.commandManager = new CommandManager(this);
        this.commandManager.addCommand(new CommandUltimateFishing(this))
                .addSubCommands(
                        new CommandSell(this),
                        new CommandSellAll(this),
                        new CommandSettings(this),
                        new CommandReload(this)
                );


        new Locale(this, "en_US");
        this.locale = Locale.getLocale(this.config.getSetting("System.Language Mode").getString());

        // Setup Lootables
        this.lootablesManager = new LootablesManager(this);
        this.lootablesManager.createDefaultLootables();
        this.getLootablesManager().getLootManager().loadLootables();

        //Running Songoda Updater
        SongodaCore.registerPlugin(this, 59);

        PluginManager pluginManager = Bukkit.getPluginManager();

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
        this.locale = Locale.getLocale(this.config.getSetting("System.Language Mode").getString());
        this.locale.reloadMessages();
        this.config.reload();
        this.getLootablesManager().getLootManager().loadLootables();
        this.setupRarity();
    }

    private void setupConfig() {
        config.addCategory("Main")
                .addDefaultSetting("Critical Cast Chance", "10%",
                        "What should the chance be for a cast to become critical?")
                .addDefaultSetting("Critical Cast Cooldown", 30,
                        "The amount of time in seconds between critical casts.")
                .addDefaultSetting("Critical Cast Expire", false,
                        "Should the critical cast expire after a failed catch attempt?")
                .addDefaultSetting("Critical Drop Multiplier", 3,
                        "How many times look should a critical cast get you?")
                .addDefaultSetting("Play Bell Sound On Bite", true,
                        "Should a bell sound play on bite?")
                .addDefaultSetting("Fish Rarity", true,
                        "Should fish have rarity?")
                .addDefaultSetting("Economy", EconomyManager.getEconomy() == null ? "Vault" : EconomyManager.getEconomy().getName(),
                        "Which economy plugin should be used?",
                        "You can choose from \"" + EconomyManager.getRegisteredEconomies().stream().map(Economy::getName)
                                .collect(Collectors.joining(", ")) + "\".")
                .getConfig().addCategory("AFK")
                .addDefaultSetting("Challenges", true,
                        "Should AFK challenges be enabled?")
                .addDefaultSetting("Trigger Amount", 6,
                        "How many casts does a player have to make without moving",
                        "To trigger an AFK event. During which a random mob listed below",
                        "will be thrown at the player.")
                .addDefaultSetting("Mob List", Arrays.asList("SKELETON", "ZOMBIE"),
                        "What mobs should be thrown the the AFK challenge is",
                        "Triggered.")
                .getConfig().addCategory("Interfaces")
                .addDefaultSetting("Glass Type 1", 7)
                .addDefaultSetting("Glass Type 2", 11)
                .addDefaultSetting("Glass Type 3", 3)
                .getConfig().addCategory("System")
                .addDefaultSetting("Language Mode", "en_US",
                        "The enabled language file.",
                        "More language files (if available) can be found in the plugins data folder.")
                .getConfig().allowUserExpansion(false).setup();
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
                .addDefaultSetting("Huge.Lure Chance Change", 8)
                .getConfig().showNullCategoryComments(false).categorySpacing(false).commentSpacing(false).setup();

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

    public Config getMainConfig() {
        return config;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public RarityManager getRarityManager() {
        return rarityManager;
    }
}
