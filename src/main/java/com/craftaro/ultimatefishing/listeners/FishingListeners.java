package com.craftaro.ultimatefishing.listeners;

import com.craftaro.core.compatibility.CompatibleHand;
import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.core.lootables.loot.Drop;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.ultimatefishing.UltimateFishing;
import com.craftaro.ultimatefishing.bait.Bait;
import com.craftaro.ultimatefishing.rarity.Rarity;
import com.craftaro.ultimatefishing.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FishingListeners implements Listener {

    private final UltimateFishing plugin;

    public FishingListeners(UltimateFishing plugin) {
        this.plugin = plugin;
    }

    private final Map<UUID, Long> criticalCooldown = new HashMap<>();

    private final List<UUID> inCritical = new ArrayList<>();

    private final Map<UUID, AFKObject> afk = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        Player player = event.getPlayer();

        CompatibleHand hand = CompatibleHand.MAIN_HAND;
        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9)
                && XMaterial.matchXMaterial(player.getInventory().getItemInOffHand()) == XMaterial.FISHING_ROD)
                hand = CompatibleHand.OFF_HAND;

        ItemStack rod = hand.getItem(player);
        boolean isUsingBait = plugin.getBaitManager().getBait(rod) != null;
        boolean isCaught = event.getState() == PlayerFishEvent.State.CAUGHT_FISH;
        if (!isUsingBait && !isCaught
                || !player.hasPermission("ultimatefishing.use")) return;

        if (isCaught) {
            Entity oldEntity = event.getCaught();
            if (!Settings.NO_BAIT_NO_RARITY.getBoolean() || isUsingBait)
                oldEntity.remove();

            if (Settings.AFK_CHALLENGES.getBoolean()) {
                if (afk.containsKey(player.getUniqueId())) {
                    AFKObject afkObject = afk.get(player.getUniqueId());

                    if (afkObject.isSameLocation(player.getLocation())) {
                        afkObject.advanceAmount();
                        if (afkObject.getAmount() >= Settings.AFK_TRIGGER.getInt()) {
                            List<String> types = Settings.AFK_MOB.getStringList();
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
            if (!isUsingBait && Settings.NO_BAIT_NO_RARITY.getBoolean()) return;

            Bait bait = plugin.getBaitManager().getBait(rod);
            CompatibleHand finalHand = hand;
            if (bait != null)
                Bukkit.getScheduler().runTaskLater(plugin, () -> finalHand.setItem(player, bait.use(rod)), 1L);


            List<Drop> drops = plugin.getLootablesManager().getDrops(player,  rod, bait);

            if (inCritical.contains(player.getUniqueId())) {
                for (int i = 0; i < (Settings.CRITICAL_DROP_MULTI.getInt() - 1); i++)
                    drops.addAll(plugin.getLootablesManager().getDrops(player, rod, bait));
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
            Bait bait = plugin.getBaitManager().getBait(rod);
            if (rod.hasItemMeta() && rod.getItemMeta().hasLore()) {
                if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13))
                    plugin.getBaitParticleTask().addBait(bait, event.getHook());
            }

            double ch = bait == null ? Settings.CRITICAL_CHANCE.getDouble() : bait.getCriticalChance();
            double rand = Math.random() * 100;
            if (rand - ch < 0 || ch == 100) {
                if (criticalCooldown.containsKey(player.getUniqueId()) && System.currentTimeMillis() < criticalCooldown.get(player.getUniqueId()))
                    return;
                criticalCooldown.put(player.getUniqueId(), System.currentTimeMillis() + (Settings.CRITICAL_COOLDOWN.getLong() * 1000));
                plugin.getLocale().getMessage("event.general.critical").sendPrefixedMessage(player);

                player.playSound(player.getLocation(), XSound.ITEM_ARMOR_EQUIP_CHAIN.parseSound(), 1f, .1f);

                inCritical.add(player.getUniqueId());
            }
        } else if (event.getState() == PlayerFishEvent.State.FAILED_ATTEMPT
                || (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13) && event.getState() == PlayerFishEvent.State.REEL_IN)) {
            if (Settings.CRITICAL_CAST_EXPIRE.getBoolean() && inCritical.contains(player.getUniqueId()))
                inCritical.remove(player.getUniqueId());
        } else if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9) && event.getState() == PlayerFishEvent.State.BITE) {
            if (Settings.BELL_ON_BITE.getBoolean()) {
                player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_BELL.parseSound(), 1f, .1f);
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
