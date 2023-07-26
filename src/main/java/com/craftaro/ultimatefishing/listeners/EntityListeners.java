package com.craftaro.ultimatefishing.listeners;

import com.craftaro.ultimatefishing.UltimateFishing;
import com.craftaro.ultimatefishing.rarity.Rarity;
import com.craftaro.ultimatefishing.utils.FishUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class EntityListeners implements Listener {

    private final UltimateFishing plugin;

    public EntityListeners(UltimateFishing plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBurn(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)
                || ((Player) event.getEntity()).getFoodLevel() > event.getFoodLevel()) return;

        Rarity rarity = plugin.getRarityManager().getRarity(event.getEntity().getItemInHand());

        if (rarity == null || FishUtils.isRaw(event.getEntity().getItemInHand())) return;

        event.setFoodLevel(event.getFoodLevel() + rarity.getExtraHealth());
    }
}
