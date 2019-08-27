package com.songoda.ultimatefishing.rarity;

import com.songoda.ultimatefishing.utils.FishUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RarityManager {

    private final List<Rarity> registeredRarities = new ArrayList<>();

    public boolean addRarity(Rarity rarity) {
        return this.registeredRarities.add(rarity);
    }

    public Rarity getRarity(ItemStack item) {
        if (item == null
                || !FishUtils.isFish(item)
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

    public List<Rarity> getRarities(Player player) {
        List<Rarity> registeredRarities = new ArrayList<>();
        for (Rarity rarity : this.registeredRarities) {
            if (player == null || player.hasPermission("ultimatefishing.fish." + rarity.getRarity()))
                registeredRarities.add(rarity);
        }
        return registeredRarities;
    }
}
