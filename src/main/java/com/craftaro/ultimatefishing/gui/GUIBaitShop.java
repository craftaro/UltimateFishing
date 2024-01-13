package com.craftaro.ultimatefishing.gui;

import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.ultimatefishing.UltimateFishing;
import com.craftaro.ultimatefishing.bait.Bait;
import com.craftaro.core.gui.CustomizableGui;
import com.craftaro.core.gui.GuiUtils;
import com.craftaro.core.hooks.EconomyManager;
import com.craftaro.core.utils.TextUtils;
import com.craftaro.ultimatefishing.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class GUIBaitShop extends CustomizableGui {

    public GUIBaitShop(UltimateFishing plugin) {
        super(plugin, "baitshop");
        setTitle(plugin.getLocale().getMessage("interface.bait.title").getMessage());
        List<Bait> baits = plugin.getBaitManager().getBaits();
        setRows(baits.size() > 7 ? 4 : 3);

        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial());
        ItemStack glass3 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_3.getMaterial());

        setDefaultItem(null);

        // decorate corners
        mirrorFill("mirrorfill_1", 0, 0, true, true, glass2);
        mirrorFill("mirrorfill_2", 0, 1, true, true, glass2);
        mirrorFill("mirrorfill_3", 0, 2, true, true, glass3);
        mirrorFill("mirrorfill_4", 0, 3, true, true, glass3);
        mirrorFill("mirrorfill_5", 0, 4, true, false, glass3);
        mirrorFill("mirrorfill_6", 1, 0, false, true, glass2);
        if (baits.size() > 7)
            mirrorFill("mirrorfill_7", 2, 0, false, true, glass2);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            int i = 10;
            for (Bait bait : baits) {
                if (getItem(i) == null) {
                    setButton(i, GuiUtils.createButtonItem(bait.asItemStack(),
                            TextUtils.formatText("&" + bait.getColor()) + bait.getBait(),
                            plugin.getLocale().getMessage("interface.bait.buyfor").processPlaceholder("price", plugin.formatEconomy(bait.getSellPrice())).getMessage()
                    ), (event) -> {
                        if (!EconomyManager.hasBalance(event.player, bait.getSellPrice())) {
                            plugin.getLocale().getMessage("interface.bait.cannotafford").sendPrefixedMessage(event.player);
                            XSound.ENTITY_VILLAGER_NO.play(event.player);
                            return;
                        }
                        XSound.ENTITY_PLAYER_LEVELUP.play(event.player);
                        EconomyManager.withdrawBalance(event.player, bait.getSellPrice());
                        event.player.getInventory().addItem(bait.asItemStack());
                    });
                }
                i++;
            }
        }, 1L);
    }
}
