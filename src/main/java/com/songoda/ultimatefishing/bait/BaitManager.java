package com.songoda.ultimatefishing.bait;

import com.songoda.core.third_party.de.tr7zw.nbtapi.NBTItem;
import com.songoda.core.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BaitManager {

    private final List<Bait> registeredBaits = new ArrayList<>();

    public boolean addBait(Bait bait) {
        return this.registeredBaits.add(bait);
    }

    public List<Bait> getBaits() {
        return Collections.unmodifiableList(registeredBaits);
    }

    public Bait getBait(String bait) {
        return registeredBaits.stream().filter(b -> b.getBait().equalsIgnoreCase(bait)).findFirst().orElse(null);
    }

    public Bait getBait(ItemStack item) {
        if (item == null || item.getType().isAir() || item.getAmount() <= 0) {
            return null;
        }
        if (item.getType() == Material.ARMOR_STAND) {
            return null;
        }

        String name;

        try {
            NBTItem nbtItem = new NBTItem(item);
            if (nbtItem.hasKey("bait")) {
                name = nbtItem.getString("bait");
            } else {
                if (!item.hasItemMeta()) return null;

                ItemMeta meta = item.getItemMeta();

                if (item.getType() == Material.FISHING_ROD) {
                    if (meta.hasLore() && !meta.getLore().isEmpty()) {
                        name = TextUtils.convertFromInvisibleString(meta.getLore().get(0)).split(":")[0];
                    } else {
                        return null;
                    }
                } else {
                    if (meta.hasDisplayName()) {
                        name = TextUtils.convertFromInvisibleString(meta.getDisplayName()).split(":")[0];
                    } else {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Error processing bait item: " + e.getMessage());
            return null;
        }

        return getBait(name);
    }

}
