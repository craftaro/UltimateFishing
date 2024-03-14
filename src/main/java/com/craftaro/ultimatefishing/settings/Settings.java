package com.craftaro.ultimatefishing.settings;

import com.craftaro.ultimatefishing.UltimateFishing;
import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.configuration.Config;
import com.craftaro.core.configuration.ConfigSetting;
import com.craftaro.core.hooks.EconomyManager;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Static config node accessors
 */
public class Settings {

    static final Config config = UltimateFishing.getInstance().getCoreConfig();

    public static final ConfigSetting CRITICAL_CHANCE = new ConfigSetting(config, "Main.Default Critical Cast Chance", 5,
            "What should the percent chance be for a cast to become critical?",
            "This can be overridden by bait.");

    public static final ConfigSetting CRITICAL_COOLDOWN = new ConfigSetting(config, "Main.Critical Cast Cooldown", 30,
            "The amount of time in seconds between critical casts.");

    public static final ConfigSetting CRITICAL_CAST_EXPIRE = new ConfigSetting(config, "Main.Critical Cast Expire", false,
            "Should the critical cast expire after a failed catch attempt?");

    public static final ConfigSetting CRITICAL_DROP_MULTI = new ConfigSetting(config, "Main.Critical Drop Multiplier", 3,
            "How many times look should a critical cast get you?");

    public static final ConfigSetting BELL_ON_BITE = new ConfigSetting(config, "Main.Play Bell Sound On Bite", true,
            "Should a bell sound play on bite?");

    public static final ConfigSetting FISH_RARITY = new ConfigSetting(config, "Main.Fish Rarity", true,
            "Should fish have rarity?");

    public static final ConfigSetting NO_BAIT_NO_RARITY = new ConfigSetting(config, "Main.No Bait No Rarity", false,
            "Should fish caught without bait have no rarity attached to them?");

    public static final ConfigSetting BAIT_ON_ROD_WITH_DISPLAYNAME = new ConfigSetting(config, "Main.Bait On Rod.Displayname", true,
            "Should bait be appliable to rods with displayname?");

    public static final ConfigSetting BAIT_ON_ROD_WITH_LORE = new ConfigSetting(config, "Main.Bait On Rod.Lore", true,
            "Should bait be appliable to rods with lore?");

    public static final ConfigSetting BAIT_ON_ROD_WITH_ENCHANTS = new ConfigSetting(config, "Main.Bait On Rod.Enchants", true,
            "Should bait be appliable to rods with enchantments?");

    public static final ConfigSetting ECONOMY_PLUGIN = new ConfigSetting(config, "Main.Economy", EconomyManager.getEconomy() == null ? "Vault" : EconomyManager.getEconomy().getName(),
            "Which economy plugin should be used?",
            "Supported plugins you have installed: \"" + EconomyManager.getManager().getRegisteredPlugins().stream().collect(Collectors.joining("\", \"")) + "\".");

    public static final ConfigSetting AFK_CHALLENGES = new ConfigSetting(config, "AFK.Challenges", true,
            "Should AFK challenges be enabled?");

    public static final ConfigSetting AFK_TRIGGER = new ConfigSetting(config, "AFK.Trigger Amount", 6,
            "How many casts does a player have to make without moving",
            "To trigger an AFK event. During which a random mob listed below",
            "will be thrown at the player.");

    public static final ConfigSetting AFK_MOB = new ConfigSetting(config, "AFK.Mob List", Arrays.asList("SKELETON", "ZOMBIE"),
            "What mobs should be thrown when the AFK challenge is Triggered.");

    public static final ConfigSetting LANGUGE_MODE = new ConfigSetting(config, "System.Language Mode", "en_US",
            "The enabled language file.",
            "More language files (if available) can be found in the plugins data folder.");

    public static final ConfigSetting BACK_ITEM = new ConfigSetting(config, "Interfaces.Back Item", "ARROW");
    public static final ConfigSetting NEXT_ITEM = new ConfigSetting(config, "Interfaces.Next Item", "ARROW");
    public static final ConfigSetting GLASS_TYPE_1 = new ConfigSetting(config, "Interfaces.Glass Type 1", "GRAY_STAINED_GLASS_PANE");
    public static final ConfigSetting GLASS_TYPE_2 = new ConfigSetting(config, "Interfaces.Glass Type 2", "BLUE_STAINED_GLASS_PANE");
    public static final ConfigSetting GLASS_TYPE_3 = new ConfigSetting(config, "Interfaces.Glass Type 3", "LIGHT_BLUE_STAINED_GLASS_PANE");

    public static final ConfigSetting MYSQL_ENABLED = new ConfigSetting(config, "MySQL.Enabled", false, "Set to 'true' to use MySQL instead of SQLite for data storage.");
    public static final ConfigSetting MYSQL_HOSTNAME = new ConfigSetting(config, "MySQL.Hostname", "localhost");
    public static final ConfigSetting MYSQL_PORT = new ConfigSetting(config, "MySQL.Port", 3306);
    public static final ConfigSetting MYSQL_DATABASE = new ConfigSetting(config, "MySQL.Database", "your-database");
    public static final ConfigSetting MYSQL_USERNAME = new ConfigSetting(config, "MySQL.Username", "user");
    public static final ConfigSetting MYSQL_PASSWORD = new ConfigSetting(config, "MySQL.Password", "pass");
    public static final ConfigSetting MYSQL_USE_SSL = new ConfigSetting(config, "MySQL.Use SSL", false);
    public static final ConfigSetting MYSQL_POOL_SIZE = new ConfigSetting(config, "MySQL.Pool Size", 3, "Determines the number of connections the pool is using. Increase this value if you are getting timeout errors when more players online.");

    /**
     * In order to set dynamic economy comment correctly, this needs to be
     * called after EconomyManager load
     */
    public static void setupConfig() {
        config.load();
        config.setAutoremove(true).setAutosave(true);

        // convert glass pane settings
        int color;
        if ((color = GLASS_TYPE_1.getInt(-1)) != -1) {
            config.set(GLASS_TYPE_1.getKey(), CompatibleMaterial.getGlassPaneForColor(color).name());
        }
        if ((color = GLASS_TYPE_2.getInt(-1)) != -1) {
            config.set(GLASS_TYPE_2.getKey(), CompatibleMaterial.getGlassPaneForColor(color).name());
        }
        if ((color = GLASS_TYPE_3.getInt(-1)) != -1) {
            config.set(GLASS_TYPE_3.getKey(), CompatibleMaterial.getGlassPaneForColor(color).name());
        }

        // convert economy settings
        if (config.getBoolean("Economy.Use Vault Economy") && EconomyManager.getManager().isEnabled("Vault")) {
            config.set("Main.Economy", "Vault");
        } else if (config.getBoolean("Economy.Use Reserve Economy") && EconomyManager.getManager().isEnabled("Reserve")) {
            config.set("Main.Economy", "Reserve");
        } else if (config.getBoolean("Economy.Use Player Points Economy") && EconomyManager.getManager().isEnabled("PlayerPoints")) {
            config.set("Main.Economy", "PlayerPoints");
        }

        config.saveChanges();
    }
}
