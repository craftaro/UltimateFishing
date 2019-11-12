package com.songoda.ultimatefishing.listeners;

import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.bait.Bait;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by songoda on 3/14/2017.
 */
public class InventoryListeners implements Listener {

    private final UltimateFishing plugin;

    public InventoryListeners(UltimateFishing instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getCurrentItem() == null) return;

        if (event.getCursor() != null && event.getCurrentItem() != null) {
            ItemStack cursor = event.getCursor();
            ItemStack item = event.getCurrentItem();
            if (cursor.hasItemMeta()
                    && cursor.getType() != Material.FISHING_ROD
                    && item.getType() == Material.FISHING_ROD
                    && plugin.getBaitManager().getBait(cursor) != null) {
                Bait bait = plugin.getBaitManager().getBait(cursor);
                if (!bait.applyBait(item)) return;
                event.setCancelled(true);

                int result = cursor.getAmount() - 1;
                cursor.setAmount(result);
                player.setItemOnCursor(result > 0 ? cursor : null);
            }
        }
    }
}
