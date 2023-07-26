package com.craftaro.ultimatefishing.gui;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.ultimatefishing.UltimateFishing;
import com.craftaro.core.gui.CustomizableGui;
import com.craftaro.core.gui.Gui;
import com.craftaro.core.gui.GuiUtils;
import com.craftaro.core.hooks.EconomyManager;
import com.craftaro.ultimatefishing.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.stream.Collectors;

public final class GUISell extends CustomizableGui {

    private final UltimateFishing plugin;
    private int task;

    public GUISell(UltimateFishing plugin, Player player) {
        super(plugin, "sell");
        this.plugin = plugin;
        setTitle(plugin.getLocale().getMessage("interface.sell.title").getMessage());
        setRows(6);

        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial());
        ItemStack glass3 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_3.getMaterial());

        // edges will be type 3
        setDefaultItem(glass3);

        // decorate corners
        mirrorFill("mirrorfill_1", 0, 0, true, true, glass2);
        mirrorFill("mirrorfill_2", 1, 0, true, true, glass2);
        mirrorFill("mirrorfill_3", 0, 1, true, true, glass2);

        // open up the center area
        setAcceptsItems(true);
        for (int row = 1; row < 5; ++row) {
            for (int col = 1; col < 8; ++col) {
                setItem(row, col, AIR);
                setUnlocked(row, col);
            }
        }

        // set up prices info (icon only)
        // TODO: need to add this line to language file
        setItem("prices", 0, 4, GuiUtils.createButtonItem(XMaterial.BOOK,
                plugin.getLocale().getMessage("interface.sell.prices").getMessage(),
                plugin.getRarityManager().getRarities().stream()
                        .map(r -> ChatColor.translateAlternateColorCodes('&', "&l&" + r.getColor() + r.getRarity() + " &7 - &a" + plugin.formatEconomy(r.getSellPrice())))
                        .collect(Collectors.toList())
        ));

        setButton("sellfor", 5, 4, GuiUtils.createButtonItem(XMaterial.SUNFLOWER,
                plugin.getLocale().getMessage("interface.sell.sellfor").processPlaceholder("price", plugin.formatEconomy(0)).getMessage()),
                (event) -> sellAll(event.player));

        if (player.hasPermission("ultimatefishing.baitshop"))
            setButton("accessbaitshop", 5, 8, GuiUtils.createButtonItem(XMaterial.STRING,
                    plugin.getLocale().getMessage("interface.sell.accessbaitshop").getMessage()), (event) -> {
                guiManager.showGUI(event.player, new GUIBaitShop(plugin));
                onClose(player);
            });

        setOnOpen((event) -> runTask());
        setOnClose((event) -> onClose(event.player));
    }

    private void runTask() {
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::updateSell, 7L, 7L);
    }

    private void updateSell() {
        double totalSale = UltimateFishing.calculateTotalValue(inventory);
        updateItem("sellfor",5, 4, plugin.getLocale().getMessage("interface.sell.sellfor").processPlaceholder("price", plugin.formatEconomy(totalSale)).getMessage());
    }

    private void sellAll(Player player) {
        double totalSale = UltimateFishing.calculateTotalValue(inventory);

        if (totalSale <= 0) {
            plugin.getLocale().getMessage("event.sell.fail").sendPrefixedMessage(player);

            player.playSound(player.getLocation(), XSound.ENTITY_VILLAGER_NO.parseSound(), 1L, 1L);

            player.closeInventory();
            return;
        }

        EconomyManager.deposit(player, totalSale);

        // clear items from gui
        for (int row = 1; row < 5; ++row) {
            for (int col = 1; col < 8; ++col) {
                ItemStack itemStack = inventory.getItem(col + row * 9);
                if (itemStack != null && plugin.getRarityManager().getRarity(itemStack) != null) {
                    inventory.setItem(col + row * 9, null);
                }
            }
        }
        plugin.getLocale().getMessage("event.sell.success")
                .processPlaceholder("total", plugin.formatEconomy(totalSale))
                .sendPrefixedMessage(player);
        player.playSound(player.getLocation(), XSound.ENTITY_VILLAGER_YES.parseSound(), 1L, 1L);

        player.closeInventory();
    }

    private void returnItem(Player player, ItemStack itemStack) {
        if (itemStack == null) return;
        Map<Integer, ItemStack> overfilled = player.getInventory().addItem(itemStack);
        if (!overfilled.isEmpty())
            overfilled.values().forEach(item2 -> player.getWorld().dropItemNaturally(player.getLocation(), item2));
    }

    private void onClose(Player player) {
        // stop updating the inventory
        Bukkit.getScheduler().cancelTask(task);
        // return any items that were left in the gui
        for (int row = 1; row < 5; ++row) {
            for (int col = 1; col < 8; ++col) {
                ItemStack itemStack = inventory.getItem(col + row * 9);
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    returnItem(player, itemStack);
                }
            }
        }
    }
}
