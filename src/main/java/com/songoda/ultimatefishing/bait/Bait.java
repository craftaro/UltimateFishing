package com.songoda.ultimatefishing.bait;

import com.songoda.core.utils.TextUtils;
import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.rarity.Rarity;
import org.bukkit.Bukkit;
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

    public Bait(String bait, String color, Material material, double sellPrice, int uses, List<Rarity> target, double chanceBonus) {
        this.bait = bait;
        this.color = color;
        this.material = material;
        this.sellPrice = sellPrice;
        this.uses = uses;
        this.target = target;
        this.chanceBonus = chanceBonus;
    }

    public ItemStack asItemStack(int amount) {
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(TextUtils.convertToInvisibleString(bait + ":")
                + TextUtils.formatText("&" + color)
                + UltimateFishing.getInstance().getLocale().getMessage("object.bait.name")
                .processPlaceholder("bait", bait).getMessage());
        meta.setLore(Collections.singletonList(UltimateFishing.getInstance().getLocale()
                .getMessage("object.bait.lore").getMessage()));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public ItemStack asItemStack() {
        return asItemStack(1);
    }

    public boolean applyBait(ItemStack item) {
        return applyBait(item, 0, uses);
    }

    public boolean applyBait(ItemStack item, int uses, int max) {
        if (item.getItemMeta().hasLore()) {
            Bait bait = UltimateFishing.getInstance().getBaitManager().getBait(item);
            max = Integer.parseInt(TextUtils.convertFromInvisibleString(item.getItemMeta().getLore().get(0)).split(":")[2]);
            if (bait == null || !bait.getBait().equals(this.getBait()))
                return false;
            else
                max += bait.uses;
        }
        String baited = TextUtils.convertToInvisibleString(bait + ":" + uses + ":" + max + ":")
                + UltimateFishing.getInstance().getLocale().getMessage("object.bait.baited")
                .processPlaceholder("bait",
                        TextUtils.formatText("&" + color) + bait)
                .processPlaceholder("uses", max - uses)
                .processPlaceholder("max", max)
                .getMessage();

        ItemMeta meta = item.getItemMeta();
        meta.setLore(Collections.singletonList(baited));
        item.setItemMeta(meta);
        return true;
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

    public void use(ItemStack item) {
        String[] split = TextUtils.convertFromInvisibleString(item.getItemMeta().getLore().get(0)).split(":");
        int uses = Integer.parseInt(split[1]) + 1;
        int max = Integer.parseInt(split[2]);

        ItemMeta meta = item.getItemMeta();
        meta.setLore(new ArrayList<>());
        item.setItemMeta(meta);

        if (uses < max)
            applyBait(item, uses, max);
    }
}
