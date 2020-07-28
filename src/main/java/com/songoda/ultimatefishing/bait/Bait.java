package com.songoda.ultimatefishing.bait;

import com.songoda.core.nms.NmsManager;
import com.songoda.core.nms.nbt.NBTItem;
import com.songoda.core.utils.ItemUtils;
import com.songoda.core.utils.TextUtils;
import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.rarity.Rarity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bait {

    private final String bait;
    private final String color;

    private Material material;
    private double sellPrice;

    // The amount of uses for this bait.
    private int uses;

    // The rarities this bait will improve the chances of catching.
    private List<Rarity> target;

    // The bonus percent chance.
    private double chanceBonus;

    private double criticalChance;

    private boolean enchanted;

    public Bait(String bait, String color, Material material, double sellPrice, int uses, List<Rarity> target, double chanceBonus, boolean enchanted, double criticalChance) {
        this.bait = bait;
        this.color = color;
        this.material = material;
        this.sellPrice = sellPrice;
        this.uses = uses;
        this.target = target;
        this.chanceBonus = chanceBonus;
        this.enchanted = enchanted;
        this.criticalChance = criticalChance;
    }

    public ItemStack asItemStack(int amount) {
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(TextUtils.formatText("&" + color)
                + UltimateFishing.getInstance().getLocale().getMessage("object.bait.name")
                .processPlaceholder("bait", bait).getMessage());
        meta.setLore(Collections.singletonList(UltimateFishing.getInstance().getLocale()
                .getMessage("object.bait.lore").getMessage()));
        itemStack.setItemMeta(meta);

        if (enchanted)
            ItemUtils.addGlow(itemStack);

        NBTItem nbtItem = NmsManager.getNbt().of(itemStack);
        nbtItem.set("bait", bait);

        return nbtItem.finish();
    }

    public ItemStack asItemStack() {
        return asItemStack(1);
    }

    public ItemStack applyBait(ItemStack item) {
        int uses = 0;
        NBTItem nbtItem = NmsManager.getNbt().of(item);
        if (nbtItem.has("uses"))
            uses = nbtItem.getNBTObject("uses").asInt();
        return applyBait(item, uses + this.uses, this.uses);
    }

    public ItemStack applyBait(ItemStack item, int uses, int max) {
        if (item.getItemMeta().hasLore()) {
            Bait bait = UltimateFishing.getInstance().getBaitManager().getBait(item);
            NBTItem nbtItem = NmsManager.getNbt().of(item);
            if (nbtItem.has("max")) {
                max = nbtItem.getNBTObject("max").asInt();
            } else {
                Integer.parseInt(TextUtils.convertFromInvisibleString(item.getItemMeta().getLore().get(0)).split(":")[2]);
            }

            if (bait == null || !bait.getBait().equals(this.getBait()))
                return null;
            else
                max += bait.uses;
        }
        String baited = UltimateFishing.getInstance().getLocale().getMessage("object.bait.baited")
                .processPlaceholder("bait",
                        TextUtils.formatText("&" + color) + bait)
                .processPlaceholder("uses", max - uses)
                .processPlaceholder("max", max)
                .getMessage();

        ItemMeta meta = item.getItemMeta();
        meta.setLore(Collections.singletonList(baited));
        item.setItemMeta(meta);

        NBTItem nbtItem = NmsManager.getNbt().of(item);
        nbtItem.set("bait", bait);
        nbtItem.set("uses", uses);
        nbtItem.set("max", max);

        return nbtItem.finish();
    }

    public String getBait() {
        return bait;
    }

    public String getColor() {
        return color;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public int getUses() {
        return uses;
    }

    public void setUses(int uses) {
        this.uses = uses;
    }

    public List<Rarity> getTarget() {
        return target;
    }

    public double getChanceBonus() {
        return chanceBonus;
    }

    public void setChanceBonus(double chanceBonus) {
        this.chanceBonus = chanceBonus;
    }

    public double getCriticalChance() {
        return criticalChance;
    }

    public void setCriticalChance(double criticalChance) {
        this.criticalChance = criticalChance;
    }

    public ItemStack use(ItemStack item) {
        if (!item.getItemMeta().hasLore()) return item;
        String[] split = TextUtils.convertFromInvisibleString(item.getItemMeta().getLore().get(0)).split(":");
        int uses;
        int max;

        NBTItem nbtItem = NmsManager.getNbt().of(item);
        if (nbtItem.has("uses")) {
            uses = nbtItem.getNBTObject("uses").asInt() + 1;
            max = nbtItem.getNBTObject("max").asInt();
        } else {
            uses = Integer.parseInt(split[1]) + 1;
            max = Integer.parseInt(split[2]);
        }

        ItemMeta meta = item.getItemMeta();
        meta.setLore(new ArrayList<>());
        item.setItemMeta(meta);

        if (uses < max)
            return applyBait(item, uses, max);
        else {
            nbtItem.set("bait", "UNSET"); // Not sure why I had to do this.
            nbtItem.set("uses", 0);
            nbtItem.set("max", 0);
            return nbtItem.finish();
        }
    }
}
