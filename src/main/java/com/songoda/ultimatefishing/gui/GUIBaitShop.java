package com.songoda.ultimatefishing.gui;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.core.utils.TextUtils;
import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.bait.Bait;
import com.songoda.ultimatefishing.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class GUIBaitShop extends Gui {

    private final UltimateFishing plugin;

    public GUIBaitShop(UltimateFishing plugin) {
        this.plugin = plugin;
        setTitle(plugin.getLocale().getMessage("interface.bait.title").getMessage());
        List<Bait> baits = plugin.getBaitManager().getBaits();
        setRows(baits.size() > 7 ? 4 : 3);

        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial());
        ItemStack glass3 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_3.getMaterial());

        setDefaultItem(null);

        // decorate corners
        GuiUtils.mirrorFill(this, 0, 0, true, true, glass2);
        GuiUtils.mirrorFill(this, 0, 1, true, true, glass2);
        GuiUtils.mirrorFill(this, 0, 2, true, true, glass3);
        GuiUtils.mirrorFill(this, 0, 3, true, true, glass3);
        GuiUtils.mirrorFill(this, 0, 4, true, false, glass3);
        GuiUtils.mirrorFill(this, 1, 0, false, true, glass2);
        if (baits.size() > 7)
            GuiUtils.mirrorFill(this, 2, 0, false, true, glass2);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            int i = 10;
            for (Bait bait : baits) {
                if (getItem(i) == null) {
                    setButton(i, GuiUtils.createButtonItem(bait.asItemStack(),
                            TextUtils.formatText("&" + bait.getColor()) + bait.getBait(),
                            plugin.getLocale().getMessage("interface.bait.buyfor").processPlaceholder("price", EconomyManager.formatEconomy(bait.getSellPrice())).getMessage()
                    ), (event) -> {
                        if (!EconomyManager.hasBalance(event.player, bait.getSellPrice())) {
                            plugin.getLocale().getMessage("interface.bait.cannotafford").sendPrefixedMessage(event.player);
                            CompatibleSound.ENTITY_VILLAGER_NO.play(event.player);
                            return;
                        }
                        CompatibleSound.ENTITY_PLAYER_LEVELUP.play(event.player);
                        EconomyManager.withdrawBalance(event.player, bait.getSellPrice());
                        event.player.getInventory().addItem(bait.asItemStack());
                    });
                }
                i++;
            }
        }, 1L);
    }
}
