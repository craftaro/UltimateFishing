package com.songoda.ultimatefishing.gui;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.stream.Collectors;

public final class GUISell extends Gui {

    private final UltimateFishing plugin;
    private int task;

    public GUISell(UltimateFishing plugin, Player player) {
        this.plugin = plugin;
        setTitle(plugin.getLocale().getMessage("interface.sell.title").getMessage());
        setRows(6);

        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial());
        ItemStack glass3 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_3.getMaterial());

        // edges will be type 3
        setDefaultItem(glass3);

        // decorate corners
        GuiUtils.mirrorFill(this, 0, 0, true, true, glass2);
        GuiUtils.mirrorFill(this, 1, 0, true, true, glass2);
        GuiUtils.mirrorFill(this, 0, 1, true, true, glass2);

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
        setItem(0, 4, GuiUtils.createButtonItem(CompatibleMaterial.BOOK,
                plugin.getLocale().getMessage("interface.sell.prices").getMessage(),
                plugin.getRarityManager().getRarities().stream()
                        .map(r -> ChatColor.translateAlternateColorCodes('&', "&l&" + r.getColor() + r.getRarity() + " &7 - &a" + EconomyManager.formatEconomy(r.getSellPrice())))
                        .collect(Collectors.toList())
        ));

        setButton(5, 4, GuiUtils.createButtonItem(CompatibleMaterial.SUNFLOWER,
                plugin.getLocale().getMessage("interface.sell.sellfor").processPlaceholder("price", EconomyManager.formatEconomy(0)).getMessage()),
                (event) -> sellAll(event.player));

        if (player.hasPermission("ultimatefishing.baitshop"))
            setButton(5, 8, GuiUtils.createButtonItem(CompatibleMaterial.STRING,
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
        updateItem(5, 4, plugin.getLocale().getMessage("interface.sell.sellfor").processPlaceholder("price", EconomyManager.formatEconomy(totalSale)).getMessage());
    }

    private void sellAll(Player player) {
        double totalSale = UltimateFishing.calculateTotalValue(inventory);

        if (totalSale <= 0) {
            plugin.getLocale().getMessage("event.sell.fail").sendPrefixedMessage(player);

            player.playSound(player.getLocation(), CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1L, 1L);

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
                .processPlaceholder("total", EconomyManager.formatEconomy(totalSale))
                .sendPrefixedMessage(player);
        player.playSound(player.getLocation(), CompatibleSound.ENTITY_VILLAGER_YES.getSound(), 1L, 1L);

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
