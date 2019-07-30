package com.songoda.ultimatefishing.listeners;

import com.songoda.lootables.loot.Drop;
import com.songoda.lootables.utils.ServerVersion;
import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.rarity.Rarity;
import com.songoda.ultimatefishing.utils.settings.Setting;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.util.Vector;

import java.util.*;

public class FishingListeners implements Listener {

    private final UltimateFishing plugin;

    public FishingListeners(UltimateFishing instance) {
        this.plugin = instance;
    }

    private Map<UUID, Long> criticalCooldown = new HashMap<>();

    private List<UUID> inCritical = new ArrayList<>();

    private Map<UUID, AFKObject> afk = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("ultimatefishing.use")) return;

        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            Entity oldEntity = event.getCaught();
            oldEntity.remove();

            if (Setting.AFK_CHALLENGES.getBoolean()) {
                if (afk.containsKey(player.getUniqueId())) {
                    AFKObject afkObject = afk.get(player.getUniqueId());

                    if (afkObject.isSameLocation(player.getLocation())) {
                        afkObject.advanceAmount();
                        if (afkObject.getAmount() >= Setting.AFK_TRIGGER.getInt()) {
                            List<String> types = Setting.AFK_MOB.getStringList();
                            Collections.shuffle(types);

                            Entity entity = oldEntity.getWorld().spawnEntity(oldEntity.getLocation(), EntityType.valueOf(types.get(0)));
                            Vector vector = getVector(player.getLocation(), entity);
                            entity.setVelocity(vector);
                            return;
                        }
                    } else {
                        afkObject.setLastLocation(player.getLocation());
                        afkObject.setAmount(1);
                    }
                } else {
                    afk.put(player.getUniqueId(), new AFKObject(player.getLocation()));
                }
            }

            List<Drop> drops = plugin.getLootablesManager().getDrops(event.getPlayer());

            if (inCritical.contains(player.getUniqueId())) {
                for (int i = 0; i < (Setting.CRITICAL_DROP_MULTI.getInt() - 1); i++)
                    drops.addAll(plugin.getLootablesManager().getDrops(event.getPlayer()));
            }
            inCritical.remove(player.getUniqueId());

            for (Drop drop : drops) {
                if (drop.getItemStack() != null) {
                    Item item = oldEntity.getWorld().dropItem(oldEntity.getLocation(), drop.getItemStack());
                    Location owner = player.getLocation();

                    Rarity rarity = plugin.getRarityManager().getRarity(drop.getItemStack());
                    if (rarity != null && rarity.isBroadcast()) {
                        Bukkit.broadcastMessage(plugin.getLocale().getMessage("event.catch.broadcast")
                                .processPlaceholder("username", player.getName())
                                .processPlaceholder("rarity", "&" + rarity.getColor() + rarity.getRarity())
                                .getPrefixedMessage());
                    }

                    item.setVelocity(getVector(owner, item));
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

                inCritical.add(player.getUniqueId());
            }
        } else if (event.getState() == PlayerFishEvent.State.FAILED_ATTEMPT
                || (plugin.isServerVersionAtLeast(ServerVersion.V1_13) && event.getState() == PlayerFishEvent.State.REEL_IN)) {
            if (Setting.CRITICAL_CAST_EXPIRE.getBoolean() && inCritical.contains(player.getUniqueId()))
                inCritical.remove(player.getUniqueId());
        } else if (plugin.isServerVersionAtLeast(ServerVersion.V1_9) && event.getState() == PlayerFishEvent.State.BITE) {
            if (Setting.BELL_ON_BITE.getBoolean() && plugin.isServerVersionAtLeast(ServerVersion.V1_12)) {
                Sound sound = plugin.isServerVersionAtLeast(ServerVersion.V1_13) ? Sound.BLOCK_NOTE_BLOCK_BELL : Sound.valueOf("BLOCK_NOTE_BELL");
                player.playSound(player.getLocation(), sound, 1f, .1f);
            }
        }
    }

    private Vector getVector(Location owner, Entity entity) {
        double d0 = owner.getX() - entity.getLocation().getX();
        double d1 = owner.getY() - entity.getLocation().getY();
        double d2 = owner.getZ() - entity.getLocation().getZ();
        return new Vector(d0 * 0.1D, d1 * 0.1D + Math.sqrt(Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2)) * 0.08D, d2 * 0.1D);

    }

    public class AFKObject {

        private Location lastLocation;
        private int amount = 1;

        public AFKObject(Location lastLocation) {
            this.lastLocation = lastLocation;
        }

        public boolean isSameLocation(Location location) {
            return location.getX() == lastLocation.getX()
                    && location.getY() == lastLocation.getY()
                    && location.getZ() == lastLocation.getZ();
        }

        public Location getLastLocation() {
            return lastLocation;
        }

        public void setLastLocation(Location lastLocation) {
            this.lastLocation = lastLocation;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public void advanceAmount() {
            this.amount++;
        }
    }

}
