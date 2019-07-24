package com.songoda.ultimatefishing.rarity;

public class Rarity {

    private final String rarity;
    private final String color;

    private final double chance;

    private final int extrahealth;
    private final double sellPrice;
    private final boolean broadcast;

    public Rarity(String rarity, String color, double chance, int extrahealth, double sellPrice, boolean broadcast) {
        this.rarity = rarity;
        this.color = color;
        this.chance = chance;
        this.extrahealth = extrahealth;
        this.sellPrice = sellPrice;
        this.broadcast = broadcast;
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

    public double getSellPrice() {
        return sellPrice;
    }

    public boolean isBroadcast() {
        return broadcast;
    }
}
