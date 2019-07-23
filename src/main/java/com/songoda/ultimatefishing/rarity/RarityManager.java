package com.songoda.ultimatefishing.rarity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RarityManager {

    private final Set<Rarity> registeredRarities = new HashSet<>();

    public boolean addRarity(Rarity rarity) {
        return this.registeredRarities.add(rarity);
    }

    public Set<Rarity> getRarities() {
        return Collections.unmodifiableSet(registeredRarities);
    }
}
