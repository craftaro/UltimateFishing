package com.songoda.ultimatefishing.rarity;

public class Rarity {

    private final String rarity;
    private final String color;

    private final double chance;

    private final int extrahealth;
    private final double sellPrice;

    public Rarity(String rarity, String color, double chance, int extrahealth, double sellPrice) {
        this.rarity = rarity;
        this.color = color;
        this.chance = chance;
        this.extrahealth = extrahealth;
        this.sellPrice = sellPrice;
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
}
