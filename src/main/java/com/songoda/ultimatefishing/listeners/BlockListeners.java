package com.songoda.ultimatefishing.listeners;

import com.songoda.core.compatibility.CompatibleHand;
import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.rarity.Rarity;
import com.songoda.ultimatefishing.utils.FishUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListeners implements Listener {

    private final UltimateFishing plugin;

    public BlockListeners(UltimateFishing instance) {
        this.plugin = instance;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        ItemStack item = CompatibleHand.getHand(event).getItem(event.getPlayer());
        if (item != null && plugin.getBaitManager().getBait(item) != null)
            event.setCancelled(true);
    }
}
