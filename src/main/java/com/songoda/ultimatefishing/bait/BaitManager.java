package com.songoda.ultimatefishing.bait;

import com.songoda.core.nms.NmsManager;
import com.songoda.core.nms.nbt.NBTItem;
import com.songoda.core.utils.TextUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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
        if (item == null) return null;
        String name;
        NBTItem nbtItem = NmsManager.getNbt().of(item);
        if (nbtItem.has("bait")) {
            name = nbtItem.getNBTObject("bait").asString();
        } else {
            if (!item.hasItemMeta() || !item.getItemMeta().hasLore() || item.getItemMeta().getLore().isEmpty())
                return null;
            name = TextUtils.convertFromInvisibleString(item.getType() == Material.FISHING_ROD
                    ? item.getItemMeta().getLore().get(0)
                    : item.getItemMeta().getDisplayName()).split(":")[0];
        }

        return getBait(name);
    }
}
