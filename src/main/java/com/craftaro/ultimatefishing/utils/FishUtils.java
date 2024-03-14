package com.craftaro.ultimatefishing.utils;

import org.bukkit.inventory.ItemStack;

public class FishUtils {

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
}
