package com.songoda.ultimatefishing.listeners;

import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.rarity.Rarity;
import com.songoda.ultimatefishing.utils.FishUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class BlockListeners implements Listener {

    private final UltimateFishing plugin;

    public BlockListeners(UltimateFishing instance) {
        this.plugin = instance;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if (plugin.getBaitManager().getBait(event.getItemInHand()) != null)
            event.setCancelled(true);
    }
}
