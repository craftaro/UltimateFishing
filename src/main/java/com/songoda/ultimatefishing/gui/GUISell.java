package com.songoda.ultimatefishing.gui;

import com.songoda.core.compatibility.CompatibleSounds;
import com.songoda.core.compatibility.LegacyMaterials;
import com.songoda.core.gui.GUI;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.utils.TempUtils;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class GUISell extends GUI {

    private final UltimateFishing plugin;
    private final Player player;
    private int task;

    public GUISell(UltimateFishing plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        setTitle(plugin.getLocale().getMessage("interface.sell.title").getMessage());
        setRows(6);

        //TODO: change this to use a modern name, then use LegacyMaterials to load
        ItemStack glass2 = GuiUtils.getBorderItem(TempUtils.getGlassPane(plugin.getConfig().getInt("Interfaces.Glass Type 2")));
        ItemStack glass3 = GuiUtils.getBorderItem(TempUtils.getGlassPane(plugin.getConfig().getInt("Interfaces.Glass Type 3")));

        // edges will be type 3
        setDefaultItem(glass3);

        // decorate corners
        setItem(0, 0, glass2);
        setItem(1, 0, glass2);
        setItem(0, 1, glass2);

        setItem(0, 8, glass2);
        setItem(1, 8, glass2);
        setItem(0, 7, glass2);

        setItem(5, 0, glass2);
        setItem(4, 0, glass2);
        setItem(5, 1, glass2);

        setItem(5, 8, glass2);
        setItem(4, 8, glass2);
        setItem(5, 7, glass2);

        // open up the center area
        for (int row = 1; row < 5; ++row) {
            for (int col = 1; col < 8; ++col) {
                setItem(row, col, AIR);
                setUnlocked(row, col);
            }
        }

        // set up prices info (icon only)
        // TODO: need to add this line to language file
        setItem(0, 4, GuiUtils.createButtonItem(LegacyMaterials.BOOK,
                ChatColor.translateAlternateColorCodes('&', "&6&lSell Prices:"),
                plugin.getRarityManager().getRarities().stream()
                        .map(r -> ChatColor.translateAlternateColorCodes('&', "&l&" + r.getColor() + r.getRarity() + " &7 - &a" + EconomyManager.formatEconomy(r.getSellPrice())))
                        .collect(Collectors.toList())
        ));

        setButton(5, 4, GuiUtils.createButtonItem(LegacyMaterials.SUNFLOWER,
                ChatColor.translateAlternateColorCodes('&', "&7Sell for &a$" + EconomyManager.formatEconomy(0))),
                (slot) -> sellAll());
        
        setOnOpen((playr, gui) -> runTask());
        setOnClose((playr, gui) -> onClose());
    }

    private void runTask() {
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::updateSell, 7L, 7L);
    }
    
    private void updateSell() {
        double totalSale = UltimateFishing.calculateTotalValue(inventory);
        updateItem(5, 4, ChatColor.translateAlternateColorCodes('&', "&7Sell for &a$" + EconomyManager.formatEconomy(totalSale)));
    }

    private void sellAll() {
        double totalSale = UltimateFishing.calculateTotalValue(inventory);

        if (totalSale <= 0) {
            plugin.getLocale().getMessage("event.sell.fail").sendPrefixedMessage(player);

            player.playSound(player.getLocation(), CompatibleSounds.ENTITY_VILLAGER_NO.getSound(), 1L, 1L);

            player.closeInventory();
            return;
        }

        EconomyManager.deposit(player, totalSale);

        // clear items from gui
        for (int row = 1; row < 5; ++row) {
            for (int col = 1; col < 8; ++col) {
                ItemStack itemStack = inventory.getItem(col + row * 9);
                if(itemStack != null && plugin.getRarityManager().getRarity(itemStack) != null) {
                    inventory.setItem(col + row * 9, null);
                }
            }
        }
        plugin.getLocale().getMessage("event.sell.success")
                .processPlaceholder("total", EconomyManager.formatEconomy(totalSale))
                .sendPrefixedMessage(player);
        player.playSound(player.getLocation(), CompatibleSounds.ENTITY_VILLAGER_YES.getSound(), 1L, 1L);
        
        player.closeInventory();
    }

    private void returnItem(Player player, ItemStack itemStack) {
        if (itemStack == null) return;
        Map<Integer, ItemStack> overfilled = player.getInventory().addItem(itemStack);
        if(!overfilled.isEmpty())
            overfilled.values().forEach(item2 -> player.getWorld().dropItemNaturally(player.getLocation(), item2));
    }

    private void onClose() {
        // stop updating the inventory
        Bukkit.getScheduler().cancelTask(task);
        // return any items that were left in the gui
        for (int row = 1; row < 5; ++row) {
            for (int col = 1; col < 8; ++col) {
                ItemStack itemStack = inventory.getItem(col + row * 9);
                if(itemStack != null && itemStack.getType() != Material.AIR) {
                    returnItem(player, itemStack);
                }
            }
        }
    }
}
