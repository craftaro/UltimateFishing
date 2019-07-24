package com.songoda.ultimatefishing.rarity;

public class Rarity {

    private final String rarity;
    private final String color;

    private final double chance;

    private final int extrahealth;
    private final double sellPrice;
    private final boolean broadcast;
    private final double lureChance;

    public Rarity(String rarity, String color, double chance, int extrahealth, double sellPrice, boolean broadcast, double lureChance) {
        this.rarity = rarity;
        this.color = color;
        this.chance = chance;
        this.extrahealth = extrahealth;
        this.sellPrice = sellPrice;
        this.broadcast = broadcast;
        this.lureChance = lureChance;
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

    public double getLureChance() {
        return lureChance;
    }
}
