package com.songoda.ultimatefishing.player;

import com.songoda.ultimatefishing.rarity.Rarity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FishingPlayer {

    private final UUID uniqueId;

    private final Map<Rarity, Integer> caught = new HashMap<>();

    public FishingPlayer(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public void addCatch(Rarity rarity) {
        addCatch(rarity, 1);
    }

    public void addCatch(Rarity rarity, int amount) {
        amount = caught.containsKey(rarity) ? caught.get(rarity) + amount : amount;
        caught.put(rarity, amount);
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public int getScore() {
        int score = 0;
        for (Map.Entry<Rarity, Integer> entry : caught.entrySet())
            score += entry.getKey().getWeight() * entry.getValue();
        return score;
    }

    public int getCaught(Rarity rarity) {
        if (!caught.containsKey(rarity))
            return 0;
        return caught.get(rarity);
    }
}
