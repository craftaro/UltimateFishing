package com.songoda.ultimatefishing.rarity;

public class Rarity {

    private final String rarity;
    private final String color;

    private final double chance;

    private final double sizeMin;
    private final double sizeMax;

    public Rarity(String rarity, String color, double chance, double sizeMin, double sizeMax) {
        this.rarity = rarity;
        this.color = color;
        this.chance = chance;
        this.sizeMin = sizeMin;
        this.sizeMax = sizeMax;
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

    public double getSizeMin() {
        return sizeMin;
    }

    public double getSizeMax() {
        return sizeMax;
    }
}
