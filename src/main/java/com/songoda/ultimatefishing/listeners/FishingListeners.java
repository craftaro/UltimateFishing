package com.songoda.ultimatefishing.listeners;

import com.songoda.lootables.loot.Drop;
import com.songoda.ultimatefishing.UltimateFishing;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class FishingListeners implements Listener {

    private final UltimateFishing plugin;

    public FishingListeners(UltimateFishing instance) {
        this.plugin = instance;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDeath(PlayerFishEvent event) {
        //Bukkit.broadcastMessage("fish " + event.getState().name());
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            Entity oldEntity = event.getCaught();
            oldEntity.remove();

            List<Drop> drops = plugin.getLootablesManager().getDrops(event.getPlayer());

            for (Drop drop : drops) {

                if (drop.getItemStack() != null) {

                    Item item = oldEntity.getWorld().dropItem(oldEntity.getLocation(), drop.getItemStack());

                    Location owner = event.getPlayer().getLocation();

                    double d0 = owner.getX() - item.getLocation().getX();
                    double d1 = owner.getY() - item.getLocation().getY();
                    double d2 = owner.getZ() - item.getLocation().getZ();
                    Vector vector = new Vector(d0 * 0.1D, d1 * 0.1D + Math.sqrt(Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2)) * 0.08D, d2 * 0.1D);

                    item.setVelocity(vector);
                } else if (drop.getCommand() != null) {
                    String command = drop.getCommand();
                    command = command.replace("%player%", event.getPlayer().getName());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            }
            } else if (event.getState() == PlayerFishEvent.State.FISHING) {

            }
    }

}
