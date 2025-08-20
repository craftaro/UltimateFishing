package com.songoda.ultimatefishing.listeners;

import com.songoda.core.compatibility.CompatibleHand;
import com.songoda.ultimatefishing.UltimateFishing;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListeners implements Listener {

    private final UltimateFishing plugin;

    public BlockListeners(UltimateFishing plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        ItemStack item = CompatibleHand.getHand(event).getItem(event.getPlayer());
        if (item != null && plugin.getBaitManager().getBait(item) != null)
            event.setCancelled(true);
    }
}
