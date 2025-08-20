package com.songoda.ultimatefishing.rarity;

public class Rarity {

    private final String rarity;
    private final String color;

    private final double chance;
    private final int weight;

    private final int extraHealth;
    private final double sellPrice;
    private final boolean broadcast;
    private final double lureChance;
    private final int tournamentValue;

    public Rarity(String rarity, String color, double chance, int weight, int extraHealth, double sellPrice, boolean broadcast, double lureChance) {
        this(rarity, color, chance, weight, extraHealth, sellPrice, broadcast, lureChance, 1);
    }

    public Rarity(String rarity, String color, double chance, int weight, int extraHealth, double sellPrice, boolean broadcast, double lureChance, int tournamentValue) {
        this.rarity = rarity;
        this.color = color;
        this.chance = chance;
        this.weight = weight;
        this.extraHealth = extraHealth;
        this.sellPrice = sellPrice;
        this.broadcast = broadcast;
        this.lureChance = lureChance;
        this.tournamentValue = tournamentValue;
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

    public int getWeight() {
        return weight;
    }

    public int getExtraHealth() {
        return extraHealth;
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

    public int getTournamentValue() {
        return tournamentValue;
    }
}
