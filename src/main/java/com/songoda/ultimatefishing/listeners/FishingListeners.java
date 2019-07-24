package com.songoda.ultimatefishing.listeners;

import com.songoda.lootables.loot.Drop;
import com.songoda.lootables.utils.ServerVersion;
import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.utils.settings.Setting;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FishingListeners implements Listener {

    private final UltimateFishing plugin;

    public FishingListeners(UltimateFishing instance) {
        this.plugin = instance;
    }

    private Map<UUID, Long> criticalCooldown = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        //Bukkit.broadcastMessage("fish " + event.getState().name());
        Player player = event.getPlayer();
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            Entity oldEntity = event.getCaught();
            oldEntity.remove();

            List<Drop> drops = plugin.getLootablesManager().getDrops(event.getPlayer());

            if (event.getHook().hasMetadata("CRITICAL")) {
                for (int i = 0; i < (Setting.CRITICAL_DROP_MULTI.getInt() - 1); i++)
                    drops.addAll(plugin.getLootablesManager().getDrops(event.getPlayer()));
            }

            for (Drop drop : drops) {

                if (drop.getItemStack() != null) {

                    Item item = oldEntity.getWorld().dropItem(oldEntity.getLocation(), drop.getItemStack());

                    Location owner = player.getLocation();

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
            double ch = Double.parseDouble(Setting.CRITICAL_CHANCE.getString().replace("%", ""));
            double rand = Math.random() * 100;
            if (rand - ch < 0 || ch == 100) {
                if (criticalCooldown.containsKey(player.getUniqueId()) && System.currentTimeMillis() < criticalCooldown.get(player.getUniqueId()))
                    return;
                criticalCooldown.put(player.getUniqueId(), System.currentTimeMillis() + (Setting.CRITICAL_COOLDOWN.getLong() * 1000));
                plugin.getLocale().getMessage("event.general.critical").sendPrefixedMessage(player);

                if (plugin.isServerVersionAtLeast(ServerVersion.V1_9))
                    player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_CHAIN, 1f, .1f);

                event.getHook().setMetadata("CRITICAL", new FixedMetadataValue(plugin, true));
            }
        } else if (event.getState() == PlayerFishEvent.State.FAILED_ATTEMPT) {
            if (Setting.CRITICAL_CAST_EXPIRE.getBoolean() && event.getHook().hasMetadata("CRITICAL"))
                event.getHook().removeMetadata("CRTICAL", plugin);
        } else if (event.getState() == PlayerFishEvent.State.BITE) {
            if (Setting.BELL_ON_BITE.getBoolean() && plugin.isServerVersionAtLeast(ServerVersion.V1_9))
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1f, .1f);
        }
    }

}
