package com.songoda.ultimatefishing.rarity;

import com.songoda.ultimatefishing.utils.Methods;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RarityManager {

    private final List<Rarity> registeredRarities = new ArrayList<>();

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

    public List<Rarity> getRarities() {
        return new ArrayList<>(registeredRarities);
    }
}
