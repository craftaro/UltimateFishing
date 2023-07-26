package com.craftaro.ultimatefishing.listeners;

import com.craftaro.ultimatefishing.utils.FishUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;

public class FurnaceListeners implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBurn(FurnaceSmeltEvent event) {
        ItemStack source = event.getSource();
        ItemStack result = event.getResult();
        if (!FishUtils.isFish(source)
                || !source.hasItemMeta()
                || !source.getItemMeta().hasLore()) return;

        result.setItemMeta(source.getItemMeta());
    }
}
