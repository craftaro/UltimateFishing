package com.songoda.ultimatefishing.rarity;

public class Rarity {

    private final String rarity;
    private final String color;

    private final double chance;

    private final int extrahealth;

    public Rarity(String rarity, String color, double chance, int extrahealth) {
        this.rarity = rarity;
        this.color = color;
        this.chance = chance;
        this.extrahealth = extrahealth;
    }

    public String getRarity() {
        return rarity;
    }

    public String getColor() {
        return color;
    }

    public double getChance() {
        return chance;
    }

    public int getExtrahealth() {
        return extrahealth;
    }
}
