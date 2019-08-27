package com.songoda.ultimatefishing.utils;

import com.songoda.core.compatibility.ServerVersion;
import com.songoda.ultimatefishing.UltimateFishing;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Deprecated
public class TempUtils {

    @Deprecated
    public static ItemStack getGlass() {

        UltimateFishing instance = UltimateFishing.getInstance();
        return getGlass(instance.getConfig().getBoolean("Interfaces.Replace Glass Type 1 With Rainbow Glass"), instance.getConfig().getInt("Interfaces.Glass Type 1"));
    }

    @Deprecated
    public static ItemStack getGlassPane(int data) {
        return new ItemStack(ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)
                ? Material.LEGACY_STAINED_GLASS_PANE : Material.valueOf("STAINED_GLASS_PANE"), 1, (short) data);
    }

    @Deprecated
    public static ItemStack getBackgroundGlass(boolean type) {
        UltimateFishing instance = UltimateFishing.getInstance();
        if (type) {
            return getGlass(false, instance.getConfig().getInt("Interfaces.Glass Type 2"));
        } else {
            return getGlass(false, instance.getConfig().getInt("Interfaces.Glass Type 3"));
        }
    }

    @Deprecated
    private static ItemStack getGlass(Boolean rainbow, int type) {
        int randomNum = 1 + (int) (Math.random() * 6);
        ItemStack glass;
        if (rainbow) {
            glass = new ItemStack(ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)
                    ? Material.LEGACY_STAINED_GLASS_PANE : Material.valueOf("STAINED_GLASS_PANE"), 1, (short) randomNum);
        } else {
            glass = new ItemStack(ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)
                    ? Material.LEGACY_STAINED_GLASS_PANE : Material.valueOf("STAINED_GLASS_PANE"), 1, (short) type);
        }
        ItemMeta glassmeta = glass.getItemMeta();
        glassmeta.setDisplayName("§l");
        glass.setItemMeta(glassmeta);
        return glass;
    }
}
