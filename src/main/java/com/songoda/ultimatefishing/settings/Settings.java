package com.songoda.ultimatefishing.settings;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.configuration.Config;
import com.songoda.core.configuration.ConfigSetting;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.ultimatefishing.UltimateFishing;

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

    public static final ConfigSetting STACKABLE_BAITS = new ConfigSetting(config, "Main.Stackable Baits", true,
            "Should bait stack?");

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

    public static final ConfigSetting GLASS_TYPE_1 = new ConfigSetting(config, "Interfaces.Glass Type 1", "GRAY_STAINED_GLASS_PANE");
    public static final ConfigSetting GLASS_TYPE_2 = new ConfigSetting(config, "Interfaces.Glass Type 2", "BLUE_STAINED_GLASS_PANE");
    public static final ConfigSetting GLASS_TYPE_3 = new ConfigSetting(config, "Interfaces.Glass Type 3", "LIGHT_BLUE_STAINED_GLASS_PANE");

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
            config.set(GLASS_TYPE_1.getKey(), CompatibleMaterial.getGlassPaneColor(color).name());
        }
        if ((color = GLASS_TYPE_2.getInt(-1)) != -1) {
            config.set(GLASS_TYPE_2.getKey(), CompatibleMaterial.getGlassPaneColor(color).name());
        }
        if ((color = GLASS_TYPE_3.getInt(-1)) != -1) {
            config.set(GLASS_TYPE_3.getKey(), CompatibleMaterial.getGlassPaneColor(color).name());
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
