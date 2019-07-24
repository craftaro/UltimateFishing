package com.songoda.ultimatefishing.utils;

import com.songoda.lootables.utils.ServerVersion;
import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.rarity.Rarity;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;

public class Methods {

    public static double calculateTotal(Inventory inventory) {
        double total = 0;
        for (ItemStack itemStack : inventory.getContents()) {
            Rarity rarity = UltimateFishing.getInstance().getRarityManager().getRarity(itemStack);

            if (rarity == null) continue;
            total += rarity.getSellPrice() * itemStack.getAmount();
        }
        return total;
    }

    public static ItemStack getGlass() {
        
        UltimateFishing instance = UltimateFishing.getInstance();
        return Methods.getGlass(instance.getConfig().getBoolean("Interfaces.Replace Glass Type 1 With Rainbow Glass"), instance.getConfig().getInt("Interfaces.Glass Type 1"));
    }

    public static ItemStack getBackgroundGlass(boolean type) {
        UltimateFishing instance = UltimateFishing.getInstance();
        if (type)
            return getGlass(false, instance.getConfig().getInt("Interfaces.Glass Type 2"));
        else
            return getGlass(false, instance.getConfig().getInt("Interfaces.Glass Type 3"));
    }

    private static ItemStack getGlass(Boolean rainbow, int type) {
        int randomNum = 1 + (int) (Math.random() * 6);
        ItemStack glass;
        if (rainbow) {
            glass = new ItemStack(UltimateFishing.getInstance().isServerVersionAtLeast(ServerVersion.V1_13) ?
                    Material.LEGACY_STAINED_GLASS_PANE : Material.valueOf("STAINED_GLASS_PANE"), 1, (short) randomNum);
        } else {
            glass = new ItemStack(UltimateFishing.getInstance().isServerVersionAtLeast(ServerVersion.V1_13) ?
                    Material.LEGACY_STAINED_GLASS_PANE : Material.valueOf("STAINED_GLASS_PANE"), 1, (short) type);
        }
        ItemMeta glassmeta = glass.getItemMeta();
        glassmeta.setDisplayName("§l");
        glass.setItemMeta(glassmeta);
        return glass;
    }
    
    public static String formatText(String text) {
    if (text == null || text.equals(""))
        return "";
    return formatText(text, false);
}

    public static String formatText(String text, boolean cap) {
        if (text == null || text.equals(""))
            return "";
        if (cap)
            text = text.substring(0, 1).toUpperCase() + text.substring(1);
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static boolean isFish(ItemStack itemStack) {
        switch (itemStack.getType().name()) {
            case "RAW_FISH":
            case "COOKED_FISH":
            case "SALMON":
            case "COOKED_SALMON":
            case "COD":
            case "COOKED_COD":
            case "PUFFERFISH":
            case "TROPICAL_FISH":
                return true;
        }
        return false;
    }

    public static boolean isRaw(ItemStack itemStack) {
        switch (itemStack.getType().name()) {
            case "RAW_FISH":
            case "SALMON":
            case "COD":
            case "TROPICAL_FISH":
                return true;
        }
        return false;
    }

    public static String formatTitle(String text) {
        if (text == null || text.equals(""))
            return "";
        if (!UltimateFishing.getInstance().isServerVersionAtLeast(ServerVersion.V1_9)) {
            if (text.length() > 31)
                text = text.substring(0, 29) + "...";
        }
        text = formatText(text);
        return text;
    }

    public static String formatEconomy(double amt) {
        DecimalFormat formatter = new DecimalFormat(amt == Math.ceil(amt) ? "#,###" : "#,###.00");
        return formatter.format(amt);
    }


    public static String formatDecimal(double decimal) {
        return new DecimalFormat("###.#").format(decimal);
    }

    public static String convertToInvisibleString(String s) {
        if (s == null || s.equals(""))
            return "";
        StringBuilder hidden = new StringBuilder();
        for (char c : s.toCharArray()) hidden.append(ChatColor.COLOR_CHAR + "").append(c);
        return hidden.toString();
    }
}
