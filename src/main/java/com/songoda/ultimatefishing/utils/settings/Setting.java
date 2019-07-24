package com.songoda.ultimatefishing.utils.settings;

import com.songoda.ultimatefishing.UltimateFishing;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Setting {

    CRITICAL_CHANCE("Main.Critical Cast Chance", "10%",
            "What should the chance be for a cast to become critical?"),

    CRITICAL_COOLDOWN("Main.Critical Cast Cooldown", 30,
            "The amount of time in seconds between critical casts."),

    CRITICAL_CAST_EXPIRE("Main.Critical Cast Expire", false,
            "Should the critical cast expire after a failed catch attempt?"),

    CRITICAL_DROP_MULTI("Main.Critical Drop Multiplier", 3,
            "How many times look should a critical cast get you?"),

    BELL_ON_BITE("Main.Play Bell Sound On Bite", true,
            "Should a bell sound play on bite?"),

    FISH_SIZE("Main.Fish Size", true,
            "Should fish have custom sizes?"),

    VAULT_ECONOMY("Economy.Use Vault Economy", true,
            "Should Vault be used?"),

    RESERVE_ECONOMY("Economy.Use Reserve Economy", true,
            "Should Reserve be used?"),

    PLAYER_POINTS_ECONOMY("Economy.Use Player Points Economy", false,
            "Should PlayerPoints be used?"),

    GLASS_TYPE_1("Interfaces.Glass Type 1", 7),
    GLASS_TYPE_2("Interfaces.Glass Type 2", 11),
    GLASS_TYPE_3("Interfaces.Glass Type 3", 3),

    LANGUGE_MODE("System.Language Mode", "en_US",
            "The enabled language file.",
            "More language files (if available) can be found in the plugins data folder.");

    private String setting;
    private Object option;
    private String[] comments;

    Setting(String setting, Object option, String... comments) {
        this.setting = setting;
        this.option = option;
        this.comments = comments;
    }

    Setting(String setting, Object option) {
        this.setting = setting;
        this.option = option;
        this.comments = null;
    }

    public static Setting getSetting(String setting) {
        List<Setting> settings = Arrays.stream(values()).filter(setting1 -> setting1.setting.equals(setting)).collect(Collectors.toList());
        if (settings.isEmpty()) return null;
        return settings.get(0);
    }

    public String getSetting() {
        return setting;
    }

    public Object getOption() {
        return option;
    }

    public String[] getComments() {
        return comments;
    }

    public List<Integer> getIntegerList() {
        return UltimateFishing.getInstance().getConfig().getIntegerList(setting);
    }

    public List<String> getStringList() {
        return UltimateFishing.getInstance().getConfig().getStringList(setting);
    }

    public boolean getBoolean() {
        return UltimateFishing.getInstance().getConfig().getBoolean(setting);
    }

    public int getInt() {
        return UltimateFishing.getInstance().getConfig().getInt(setting);
    }

    public long getLong() {
        return UltimateFishing.getInstance().getConfig().getLong(setting);
    }

    public String getString() {
        return UltimateFishing.getInstance().getConfig().getString(setting);
    }

    public char getChar() {
        return UltimateFishing.getInstance().getConfig().getString(setting).charAt(0);
    }

    public double getDouble() {
        return UltimateFishing.getInstance().getConfig().getDouble(setting);
    }

}