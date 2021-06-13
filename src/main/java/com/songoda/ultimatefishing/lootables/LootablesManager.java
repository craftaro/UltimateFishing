package com.songoda.ultimatefishing.lootables;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.core.lootables.Lootables;
import com.songoda.core.lootables.loot.Drop;
import com.songoda.core.lootables.loot.Loot;
import com.songoda.core.lootables.loot.LootBuilder;
import com.songoda.core.lootables.loot.LootManager;
import com.songoda.core.lootables.loot.Lootable;
import com.songoda.core.lootables.loot.objects.EnchantChance;
import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.bait.Bait;
import com.songoda.ultimatefishing.player.FishingPlayer;
import com.songoda.ultimatefishing.rarity.Rarity;
import com.songoda.ultimatefishing.settings.Settings;
import com.songoda.ultimatefishing.utils.FishUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LootablesManager {

    private final Lootables instance;

    private final UltimateFishing plugin;

    private final LootManager lootManager;

    private final String lootablesDir = UltimateFishing.getInstance().getDataFolder() + File.separator + "lootables";

    public LootablesManager(UltimateFishing plugin) {
        this.plugin = plugin;
        this.instance = new Lootables(lootablesDir);
        this.lootManager = new LootManager(instance);
    }

    public List<Drop> getDrops(Player player, ItemStack rod, Bait bait) {
        List<Drop> toDrop = new ArrayList<>();

        Lootable lootable = lootManager.getRegisteredLootables().get("NORMAL");
        int looting = rod.containsEnchantment(Enchantment.LOOT_BONUS_MOBS)
                ? rod.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS)
                : 0;

        int lure = rod.containsEnchantment(Enchantment.LURE)
                ? rod.getEnchantmentLevel(Enchantment.LURE)
                : 0;

        int rerollChance = looting / (looting + 1);

        for (Loot loot : lootable.getRegisteredLoot())
            toDrop.addAll(runLoot(rod, loot, rerollChance, looting));

        if (Settings.FISH_RARITY.getBoolean()) {
            for (Drop drop : toDrop) {
                if (drop.getItemStack() == null || !FishUtils.isFish(drop.getItemStack())) continue;
                ItemStack itemStack = drop.getItemStack();
                ItemMeta meta = itemStack.getItemMeta();
                List<String> lore = new ArrayList<>();

                Map<Double, Rarity> rarities = new HashMap<>();

                for (Rarity rarity : plugin.getRarityManager().getRarities(player)) {
                    double weight = rarity.getChance() + (rarity.getLureChance() * lure);
                    if (bait != null) {
                        if (bait.getTarget().contains(rarity))
                            weight += bait.getChanceBonus();
                    }
                    rarities.put(weight, rarity);
                }

                if (rarities.isEmpty()) continue;

                double totalWeight = rarities.keySet().stream().mapToDouble(p -> p).sum();

                Rarity rarity = rarities.entrySet().iterator().next().getValue();
                if (rarities.size() != 1) {
                    double random = Math.random() * totalWeight;
                    for (Map.Entry<Double, Rarity> entry : rarities.entrySet()) {
                        random -= entry.getKey();
                        if (random <= 0d) {
                            rarity = entry.getValue();
                            break;
                        }
                    }
                }
                lore.add(ChatColor.translateAlternateColorCodes('&', "&" + rarity.getColor() + rarity.getRarity()));
                if (meta.hasLore())
                    lore.addAll(meta.getLore());
                meta.setLore(lore);
                itemStack.setItemMeta(meta);
                FishingPlayer fishingPlayer = plugin.getPlayerManager().getPlayer(player);

                if (fishingPlayer.getCaught(rarity) == 0)
                    plugin.getDataManager().createCaught(player, rarity, 1);
                else
                    plugin.getDataManager().updateCaught(player, rarity, fishingPlayer.getCaught(rarity) + 1);
                plugin.getPlayerManager().getPlayer(player).addCatch(rarity);
            }
        }

        return toDrop;
    }

    private List<Drop> runLoot(ItemStack rod, Loot loot, int rerollChance, int looting) {
        return lootManager.runLoot(null,
                false,
                false,
                rod,
                EntityType.PLAYER,
                loot,
                rerollChance,
                looting);
    }

    public void createDefaultLootables() {

        // Add Normal.

        LootBuilder loot = new LootBuilder();

        loot.addChildLoot(new LootBuilder()
                .setMaterial(CompatibleMaterial.COD)
                .setBurnedMaterial(CompatibleMaterial.COOKED_COD)
                .setChance(51)
                .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 50.9),
                        new EnchantChance(Enchantment.LUCK, 2, 50.8),
                        new EnchantChance(Enchantment.LUCK, 3, 50.7))
                .build());

        loot.addChildLoot(new LootBuilder()
                .setMaterial(CompatibleMaterial.SALMON)
                .setBurnedMaterial(CompatibleMaterial.COOKED_SALMON)
                .setChance(51)
                .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 21.2),
                        new EnchantChance(Enchantment.LUCK, 2, 21.2),
                        new EnchantChance(Enchantment.LUCK, 3, 21.1))
                .build());

        loot.addChildLoot(new LootBuilder()
                .setMaterial(CompatibleMaterial.TROPICAL_FISH)
                .setChance(1.7)
                .build());

        loot.addChildLoot(new LootBuilder()
                .setMaterial(CompatibleMaterial.PUFFERFISH)
                .setChance(11.1)
                .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 11.0),
                        new EnchantChance(Enchantment.LUCK, 2, 11.0),
                        new EnchantChance(Enchantment.LUCK, 3, 11.0))
                .build());

        loot.addChildLoot(new LootBuilder()
                .setMaterial(CompatibleMaterial.BOW)
                .setChance(0.7)
                .addEnchants(new LootBuilder.Tuple("RANDOM", 26))
                .setDamageMin(10)
                .setDamageMax(100)
                .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 1),
                        new EnchantChance(Enchantment.LUCK, 2, 1.3),
                        new EnchantChance(Enchantment.LUCK, 3, 1.6))
                .build());

        loot.addChildLoot(new LootBuilder()
                .setMaterial(CompatibleMaterial.BOOK)
                .setChance(0.7)
                .addEnchants(new LootBuilder.Tuple("RANDOM", 26))
                .setDamageMin(10)
                .setDamageMax(100)
                .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 1),
                        new EnchantChance(Enchantment.LUCK, 2, 1.3),
                        new EnchantChance(Enchantment.LUCK, 3, 1.6))
                .build());

        loot.addChildLoot(new LootBuilder()
                .setMaterial(CompatibleMaterial.FISHING_ROD)
                .setChance(0.7)
                .addEnchants(new LootBuilder.Tuple("RANDOM", 26))
                .setDamageMin(10)
                .setDamageMax(100)
                .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 1),
                        new EnchantChance(Enchantment.LUCK, 2, 1.3),
                        new EnchantChance(Enchantment.LUCK, 3, 1.6))
                .build());

        loot.addChildLoot(new LootBuilder()
                .setMaterial(CompatibleMaterial.NAME_TAG)
                .setChance(0.7)
                .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 1),
                        new EnchantChance(Enchantment.LUCK, 2, 1.3),
                        new EnchantChance(Enchantment.LUCK, 3, 1.6))
                .build());

        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)) {
            loot.addChildLoot(new LootBuilder()
                    .setMaterial(CompatibleMaterial.NAUTILUS_SHELL)
                    .setChance(0.7)
                    .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 1),
                            new EnchantChance(Enchantment.LUCK, 2, 1.3),
                            new EnchantChance(Enchantment.LUCK, 3, 1.6))
                    .build());
        }

        loot.addChildLoot(new LootBuilder()
                .setMaterial(CompatibleMaterial.SADDLE)
                .setChance(0.7)
                .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 1),
                        new EnchantChance(Enchantment.LUCK, 2, 1.3),
                        new EnchantChance(Enchantment.LUCK, 3, 1.6))
                .build());

        loot.addChildLoot(new LootBuilder()
                .setMaterial(CompatibleMaterial.LILY_PAD)
                .setChance(0.7)
                .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 1),
                        new EnchantChance(Enchantment.LUCK, 2, 1.3),
                        new EnchantChance(Enchantment.LUCK, 3, 1.6))
                .build());

        loot.addChildLoot(new LootBuilder()
                .setMaterial(CompatibleMaterial.BOWL)
                .setChance(1.2)
                .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 1),
                        new EnchantChance(Enchantment.LUCK, 2, .7),
                        new EnchantChance(Enchantment.LUCK, 3, .5))
                .build());

        loot.addChildLoot(new LootBuilder()
                .setMaterial(CompatibleMaterial.FISHING_ROD)
                .setChance(0.2)
                .setDamageMin(10)
                .setDamageMax(100)
                .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, .2),
                        new EnchantChance(Enchantment.LUCK, 2, .1),
                        new EnchantChance(Enchantment.LUCK, 3, .1))
                .build());

        loot.addChildLoot(new LootBuilder()
                .setMaterial(CompatibleMaterial.LEATHER)
                .setChance(1.2)
                .setDamageMin(10)
                .setDamageMax(100)
                .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 1.0),
                        new EnchantChance(Enchantment.LUCK, 2, .7),
                        new EnchantChance(Enchantment.LUCK, 3, .5))
                .build());

        loot.addChildLoot(new LootBuilder()
                .setMaterial(CompatibleMaterial.LEATHER_BOOTS)
                .setChance(1.2)
                .setDamageMin(10)
                .setDamageMax(100)
                .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 1.0),
                        new EnchantChance(Enchantment.LUCK, 2, .7),
                        new EnchantChance(Enchantment.LUCK, 3, .5))
                .build());

        loot.addChildLoot(new LootBuilder()
                .setMaterial(CompatibleMaterial.ROTTEN_FLESH)
                .setChance(1.2)
                .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 1.0),
                        new EnchantChance(Enchantment.LUCK, 2, .7),
                        new EnchantChance(Enchantment.LUCK, 3, .5))
                .build());

        loot.addChildLoot(new LootBuilder()
                .setMaterial(CompatibleMaterial.STICK)
                .setChance(.6)
                .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, .5),
                        new EnchantChance(Enchantment.LUCK, 2, .4),
                        new EnchantChance(Enchantment.LUCK, 3, .3))
                .build());

        loot.addChildLoot(new LootBuilder()
                .setMaterial(CompatibleMaterial.STRING)
                .setChance(.6)
                .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, .5),
                        new EnchantChance(Enchantment.LUCK, 2, .4),
                        new EnchantChance(Enchantment.LUCK, 3, .3))
                .build());

        loot.addChildLoot(new LootBuilder()
                .setMaterial(CompatibleMaterial.BONE)
                .setChance(1.2)
                .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 1.0),
                        new EnchantChance(Enchantment.LUCK, 2, .7),
                        new EnchantChance(Enchantment.LUCK, 3, .5))
                .build());

        loot.addChildLoot(new LootBuilder()
                .setMaterial(CompatibleMaterial.INK_SAC)
                .setChance(.1)
                .build());

        loot.addChildLoot(new LootBuilder()
                .setMaterial(CompatibleMaterial.TRIPWIRE_HOOK)
                .setChance(1.2)
                .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 1.0),
                        new EnchantChance(Enchantment.LUCK, 2, .7),
                        new EnchantChance(Enchantment.LUCK, 3, .5))
                .build());

        lootManager.addLootable(new Lootable("NORMAL", loot.build()));

        lootManager.saveLootables(true);
    }

    public LootManager getLootManager() {
        return lootManager;
    }
}
