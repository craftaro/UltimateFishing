package com.songoda.ultimatefishing.tournament;

import com.songoda.ultimatefishing.rarity.Rarity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TournamentParticipant {
    private final UUID playerUUID;
    private final Map<String, Integer> catches = new HashMap<>();
    private int totalPoints = 0;
    private int totalCatches = 0;
    
    public TournamentParticipant(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }
    
    public void addCatch(Rarity rarity, int points) {
        String rarityName = rarity.getRarity();
        catches.put(rarityName, catches.getOrDefault(rarityName, 0) + 1);
        totalPoints += points;
        totalCatches++;
    }
    
    public UUID getPlayerUUID() {
        return playerUUID;
    }
    
    public int getPoints() {
        return totalPoints;
    }
    
    public int getTotalCatches() {
        return totalCatches;
    }
    
    public Map<String, Integer> getCatches() {
        return new HashMap<>(catches);
    }
    
    public int getCatchCount(String rarity) {
        return catches.getOrDefault(rarity, 0);
    }
}