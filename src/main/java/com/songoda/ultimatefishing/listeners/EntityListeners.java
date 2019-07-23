package com.songoda.ultimatefishing.listeners;

import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.rarity.Rarity;
import com.songoda.ultimatefishing.utils.Methods;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import java.util.Optional;

public class EntityListeners implements Listener {

    private final UltimateFishing plugin;

    public EntityListeners(UltimateFishing instance) {
        this.plugin = instance;
    }


    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBurn(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)
                || event.getEntity().getHealth() > event.getFoodLevel()
                || !Methods.isFish(event.getEntity().getItemInHand())
                || !event.getEntity().getItemInHand().hasItemMeta()
                || !event.getEntity().getItemInHand().getItemMeta().hasLore()) return;

        String line = ChatColor.stripColor(event.getEntity().getItemInHand().getItemMeta().getLore().get(0));

        Optional<Rarity> optionalRarity = plugin.getRarityManager().getRarities().stream()
                .filter(rarity -> rarity.getRarity().equalsIgnoreCase(line)).findFirst();

        if (!optionalRarity.isPresent()) return;

        event.setFoodLevel(event.getFoodLevel() + optionalRarity.get().getExtrahealth());
    }
}
