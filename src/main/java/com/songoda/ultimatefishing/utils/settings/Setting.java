package com.songoda.ultimatefishing.utils.settings;

import com.songoda.ultimatefishing.UltimateFishing;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Setting {

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