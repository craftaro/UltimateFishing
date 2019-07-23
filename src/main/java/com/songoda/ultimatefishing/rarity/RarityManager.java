package com.songoda.ultimatefishing.rarity;

import com.songoda.ultimatefishing.utils.Methods;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class RarityManager {

    private final Set<Rarity> registeredRarities = new HashSet<>();

    public boolean addRarity(Rarity rarity) {
        return this.registeredRarities.add(rarity);
    }

    public Rarity getRarity(ItemStack item) {
        if (item == null
                || !Methods.isFish(item)
                || !item.hasItemMeta()
                || !item.getItemMeta().hasLore()) return null;

        String line = ChatColor.stripColor(item.getItemMeta().getLore().get(0));

        Optional<Rarity> optionalRarity = getRarities().stream()
                .filter(rarity -> rarity.getRarity().equalsIgnoreCase(line)).findFirst();

        return optionalRarity.orElse(null);
    }

    public Set<Rarity> getRarities() {
        return Collections.unmodifiableSet(registeredRarities);
    }
}
