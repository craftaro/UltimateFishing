package com.songoda.ultimatefishing.lootables;

import com.songoda.lootables.Lootables;
import com.songoda.lootables.loot.*;
import com.songoda.lootables.loot.objects.EnchantChance;
import com.songoda.lootables.utils.Methods;
import com.songoda.ultimatefishing.UltimateFishing;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LootablesManager {

    private final Lootables instance;

    private final LootManager lootManager;

    private final String lootablesDir = UltimateFishing.getInstance().getDataFolder() + File.separator + "lootables";

    public LootablesManager() {
        this.instance = new Lootables(lootablesDir);
        this.lootManager = new LootManager(instance);
    }

    public List<Drop> getDrops(Player player) {
        List<Drop> toDrop = new ArrayList<>();

        Lootable lootable = lootManager.getRegisteredLootables().get("NORMAL");
        int looting = player.getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_MOBS)
                ? player.getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS)
                : 0;

        int rerollChance = looting / (looting + 1);

        for (Loot loot : lootable.getRegisteredLoot())
            toDrop.addAll(runLoot(player, loot, rerollChance, looting));

        return toDrop;
    }

    private List<Drop> runLoot(LivingEntity entity, Loot loot, int rerollChance, int looting) {
        return lootManager.runLoot(null,
                false,
                entity.getKiller() != null ? entity.getKiller().getItemInHand() : null,
                entity.getKiller() == null ? null : entity.getKiller().getType(),
                loot,
                rerollChance,
                looting);
    }

    public void createDefaultLootables() {

        // Add Normal.
        lootManager.addLootable(new Lootable("NORMAL",
                new LootBuilder()
                        .setChildDropCounMin(1)
                        .setChildDropCountMax(1)
                        .addChildLoot(
                                new LootBuilder()
                                        .setMaterial(Material.COD)
                                        .setBurnedMaterial(Material.COOKED_COD)
                                        .setChance(51)
                                        .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 50.9),
                                                new EnchantChance(Enchantment.LUCK, 2, 50.8),
                                                new EnchantChance(Enchantment.LUCK, 3, 50.7))
                                        .build(),
                                new LootBuilder()
                                        .setMaterial(Material.SALMON)
                                        .setBurnedMaterial(Material.COOKED_SALMON)
                                        .setChance(51)
                                        .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 21.2),
                                                new EnchantChance(Enchantment.LUCK, 2, 21.2),
                                                new EnchantChance(Enchantment.LUCK, 3, 21.1))
                                        .build(),
                                new LootBuilder()
                                        .setMaterial(Material.TROPICAL_FISH)
                                        .setChance(1.7)
                                        .build(),
                                new LootBuilder()
                                        .setMaterial(Material.PUFFERFISH)
                                        .setChance(11.1)
                                        .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 11.0),
                                                new EnchantChance(Enchantment.LUCK, 2, 11.0),
                                                new EnchantChance(Enchantment.LUCK, 3, 11.0))
                                        .build(),
                                new LootBuilder()
                                        .setMaterial(Material.BOW)
                                        .setChance(0.7)
                                        .addEnchants(new Methods.Tuple("RANDOM", 26))
                                        .setDamageMin(10)
                                        .setDamageMax(100)
                                        .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 1),
                                                new EnchantChance(Enchantment.LUCK, 2, 1.3),
                                                new EnchantChance(Enchantment.LUCK, 3, 1.6))
                                        .build(),
                                new LootBuilder()
                                        .setMaterial(Material.ENCHANTED_BOOK)
                                        .setChance(0.7)
                                        .addEnchants(new Methods.Tuple("RANDOM", 26))
                                        .setDamageMin(10)
                                        .setDamageMax(100)
                                        .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 1),
                                                new EnchantChance(Enchantment.LUCK, 2, 1.3),
                                                new EnchantChance(Enchantment.LUCK, 3, 1.6))
                                        .build(),
                                new LootBuilder()
                                        .setMaterial(Material.FISHING_ROD)
                                        .setChance(0.7)
                                        .addEnchants(new Methods.Tuple("RANDOM", 26))
                                        .setDamageMin(10)
                                        .setDamageMax(100)
                                        .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 1),
                                                new EnchantChance(Enchantment.LUCK, 2, 1.3),
                                                new EnchantChance(Enchantment.LUCK, 3, 1.6))
                                        .build(),
                                new LootBuilder()
                                        .setMaterial(Material.NAME_TAG)
                                        .setChance(0.7)
                                        .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 1),
                                                new EnchantChance(Enchantment.LUCK, 2, 1.3),
                                                new EnchantChance(Enchantment.LUCK, 3, 1.6))
                                        .build(),
                                new LootBuilder()
                                        .setMaterial(Material.NAUTILUS_SHELL)
                                        .setChance(0.7)
                                        .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 1),
                                                new EnchantChance(Enchantment.LUCK, 2, 1.3),
                                                new EnchantChance(Enchantment.LUCK, 3, 1.6))
                                        .build(),
                                new LootBuilder()
                                        .setMaterial(Material.SADDLE)
                                        .setChance(0.7)
                                        .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 1),
                                                new EnchantChance(Enchantment.LUCK, 2, 1.3),
                                                new EnchantChance(Enchantment.LUCK, 3, 1.6))
                                        .build(),
                                new LootBuilder()
                                        .setMaterial(Material.LILY_PAD)
                                        .setChance(0.7)
                                        .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 1),
                                                new EnchantChance(Enchantment.LUCK, 2, 1.3),
                                                new EnchantChance(Enchantment.LUCK, 3, 1.6))
                                        .build(),
                                new LootBuilder()
                                        .setMaterial(Material.BOWL)
                                        .setChance(1.2)
                                        .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 1),
                                                new EnchantChance(Enchantment.LUCK, 2, .7),
                                                new EnchantChance(Enchantment.LUCK, 3, .5))
                                        .build(),
                                new LootBuilder()
                                        .setMaterial(Material.FISHING_ROD)
                                        .setChance(0.2)
                                        .setDamageMin(10)
                                        .setDamageMax(100)
                                        .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, .2),
                                                new EnchantChance(Enchantment.LUCK, 2, .1),
                                                new EnchantChance(Enchantment.LUCK, 3, .1))
                                        .build(),
                                new LootBuilder()
                                        .setMaterial(Material.LEATHER)
                                        .setChance(1.2)
                                        .setDamageMin(10)
                                        .setDamageMax(100)
                                        .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 1.0),
                                                new EnchantChance(Enchantment.LUCK, 2, .7),
                                                new EnchantChance(Enchantment.LUCK, 3, .5))
                                        .build(),
                                new LootBuilder()
                                        .setMaterial(Material.LEATHER_BOOTS)
                                        .setChance(1.2)
                                        .setDamageMin(10)
                                        .setDamageMax(100)
                                        .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 1.0),
                                                new EnchantChance(Enchantment.LUCK, 2, .7),
                                                new EnchantChance(Enchantment.LUCK, 3, .5))
                                        .build(),
                                new LootBuilder()
                                        .setMaterial(Material.ROTTEN_FLESH)
                                        .setChance(1.2)
                                        .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 1.0),
                                                new EnchantChance(Enchantment.LUCK, 2, .7),
                                                new EnchantChance(Enchantment.LUCK, 3, .5))
                                        .build(),
                                new LootBuilder()
                                        .setMaterial(Material.STICK)
                                        .setChance(.6)
                                        .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, .5),
                                                new EnchantChance(Enchantment.LUCK, 2, .4),
                                                new EnchantChance(Enchantment.LUCK, 3, .3))
                                        .build(),
                                new LootBuilder()
                                        .setMaterial(Material.STRING)
                                        .setChance(.6)
                                        .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, .5),
                                                new EnchantChance(Enchantment.LUCK, 2, .4),
                                                new EnchantChance(Enchantment.LUCK, 3, .3))
                                        .build(),
                                new LootBuilder()
                                        .setMaterial(Material.POTION)
                                        .setChance(1.2)
                                        .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 1.0),
                                                new EnchantChance(Enchantment.LUCK, 2, .7),
                                                new EnchantChance(Enchantment.LUCK, 3, .5))
                                        .build(),
                                new LootBuilder()
                                        .setMaterial(Material.BONE)
                                        .setChance(1.2)
                                        .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 1.0),
                                                new EnchantChance(Enchantment.LUCK, 2, .7),
                                                new EnchantChance(Enchantment.LUCK, 3, .5))
                                        .build(),
                                new LootBuilder()
                                        .setMaterial(Material.INK_SAC)
                                        .setChance(.1)
                                        .build(),
                                new LootBuilder()
                                        .setMaterial(Material.TRIPWIRE_HOOK)
                                        .setChance(1.2)
                                        .addEnchantChances(new EnchantChance(Enchantment.LUCK, 1, 1.0),
                                                new EnchantChance(Enchantment.LUCK, 2, .7),
                                                new EnchantChance(Enchantment.LUCK, 3, .5))
                                        .build()
                        ).build()));

        lootManager.saveLootables();
    }

    public LootManager getLootManager() {
        return lootManager;
    }
}
