package com.songoda.ultimatefishing;

import com.songoda.lootables.utils.ServerVersion;
import com.songoda.ultimatefishing.command.CommandManager;
import com.songoda.ultimatefishing.economy.Economy;
import com.songoda.ultimatefishing.economy.PlayerPointsEconomy;
import com.songoda.ultimatefishing.economy.ReserveEconomy;
import com.songoda.ultimatefishing.economy.VaultEconomy;
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
import com.songoda.ultimatefishing.utils.settings.Setting;
import com.songoda.ultimatefishing.utils.settings.SettingsManager;
import com.songoda.ultimatefishing.utils.updateModules.LocaleModule;
import com.songoda.update.Plugin;
import com.songoda.update.SongodaUpdate;
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
    private Economy economy;

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

        // Setup Economy
        if (Setting.VAULT_ECONOMY.getBoolean() && pluginManager.isPluginEnabled("Vault"))
            this.economy = new VaultEconomy();
        else if (Setting.RESERVE_ECONOMY.getBoolean() && pluginManager.isPluginEnabled("Reserve"))
            this.economy = new ReserveEconomy();
        else if (Setting.PLAYER_POINTS_ECONOMY.getBoolean() && pluginManager.isPluginEnabled("PlayerPoints"))
            this.economy = new PlayerPointsEconomy();

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
            rarityFile.getConfig().set("Rarity.Normal.Chance", 50);
            rarityFile.getConfig().set("Rarity.Normal.Color", "7");
            rarityFile.getConfig().set("Rarity.Normal.Extra Health", 0);
            rarityFile.getConfig().set("Rarity.Normal.Sell Price", 19.99);
            rarityFile.getConfig().set("Rarity.Large.Chance", 25);
            rarityFile.getConfig().set("Rarity.Large.Color", "c");
            rarityFile.getConfig().set("Rarity.Large.Extra Health", 2);
            rarityFile.getConfig().set("Rarity.Large.Sell Price", 49.99);
            rarityFile.getConfig().set("Rarity.Huge.Chance", 10);
            rarityFile.getConfig().set("Rarity.Huge.Color", "5");
            rarityFile.getConfig().set("Rarity.Huge.Extra Health", 4);
            rarityFile.getConfig().set("Rarity.Huge.Sell Price", 99.99);
            rarityFile.getConfig().set("Rarity.Huge.Broadcast", true);
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
                        raritySection.getBoolean("Broadcast")));
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

    public Economy getEconomy() {
        return economy;
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
