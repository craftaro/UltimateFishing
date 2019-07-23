package com.songoda.ultimatefishing.listeners;

import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.utils.Methods;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;

public class FurnaceListeners implements Listener {

    private final UltimateFishing plugin;

    public FurnaceListeners(UltimateFishing instance) {
        this.plugin = instance;
    }


    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBurn(FurnaceSmeltEvent event) {
        ItemStack source = event.getSource();
        ItemStack result = event.getResult();
        if (!Methods.isFish(source)
                || !source.hasItemMeta()
                || !source.getItemMeta().hasLore()) return;

        result.setItemMeta(source.getItemMeta());
    }
}
