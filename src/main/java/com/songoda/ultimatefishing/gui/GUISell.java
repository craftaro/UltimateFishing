package com.songoda.ultimatefishing.gui;

import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.rarity.Rarity;
import com.songoda.ultimatefishing.utils.Methods;
import com.songoda.ultimatefishing.utils.gui.AbstractGUI;
import com.songoda.ultimatefishing.utils.gui.Range;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class GUISell extends AbstractGUI {

    private final UltimateFishing plugin;

    private int task;

    public GUISell(UltimateFishing plugin, Player player) {
        super(player);
        this.plugin = plugin;

        init(plugin.getLocale().getMessage("interface.sell.title").getMessage(), 54);
        runTask();
    }

    @Override
    protected void constructGUI() {
        resetClickables();
        registerClickables();

        inventory.setItem(0, Methods.getBackgroundGlass(true));
        inventory.setItem(1, Methods.getBackgroundGlass(true));
        inventory.setItem(2, Methods.getBackgroundGlass(false));
        inventory.setItem(3, Methods.getBackgroundGlass(false));
        inventory.setItem(5, Methods.getBackgroundGlass(false));
        inventory.setItem(6, Methods.getBackgroundGlass(false));
        inventory.setItem(7, Methods.getBackgroundGlass(true));
        inventory.setItem(8, Methods.getBackgroundGlass(true));

        inventory.setItem(9, Methods.getBackgroundGlass(true));

        addDraggable(new Range(10, 16, null, false), true);

        inventory.setItem(17, Methods.getBackgroundGlass(true));

        inventory.setItem(18, Methods.getBackgroundGlass(false));

        addDraggable(new Range(19, 25, null, false), true);

        inventory.setItem(26, Methods.getBackgroundGlass(false));

        inventory.setItem(27, Methods.getBackgroundGlass(false));

        addDraggable(new Range(28, 34, null, false), true);

        inventory.setItem(35, Methods.getBackgroundGlass(false));

        inventory.setItem(36, Methods.getBackgroundGlass(true));

        addDraggable(new Range(37, 43, null, false), true);

        inventory.setItem(44, Methods.getBackgroundGlass(true));

        inventory.setItem(45, Methods.getBackgroundGlass(true));
        inventory.setItem(46, Methods.getBackgroundGlass(true));
        inventory.setItem(47, Methods.getBackgroundGlass(false));
        inventory.setItem(48, Methods.getBackgroundGlass(false));
        inventory.setItem(50, Methods.getBackgroundGlass(false));
        inventory.setItem(51, Methods.getBackgroundGlass(false));
        inventory.setItem(52, Methods.getBackgroundGlass(true));
        inventory.setItem(53, Methods.getBackgroundGlass(true));

        ArrayList<String> lore = new ArrayList<>();
        for (Rarity rarity : plugin.getRarityManager().getRarities())
            lore.add("&l&" + rarity.getColor() + rarity.getRarity() + " &7 - &a$" + Methods.formatEconomy(rarity.getSellPrice()));


        createButton(4, Material.BOOK, "&6&lSell Prices:", lore);

        double total = calculateTotal();

        createButton(49, Material.SUNFLOWER, "&7Sell for &a$" + Methods.formatEconomy(total));
        registerClickable(49, ((player1, inventory1, cursor, slot, type) -> {
            double totalNew = calculateTotal();
            if (totalNew == 0)
            plugin.getEconomy().deposit(player, totalNew);

            plugin.getLocale().getMessage("event.sell.success")
                    .processPlaceholder("total", Methods.formatEconomy(totalNew))
                    .sendPrefixedMessage(player);

            for (ItemStack itemStack : inventory.getContents()) {
                Rarity rarity = plugin.getRarityManager().getRarity(itemStack);

                if (rarity == null) continue;
                inventory.remove(itemStack);
            }
        }));
    }

    private double calculateTotal() {
        double total = 0;
        for (ItemStack itemStack : inventory.getContents()) {
            Rarity rarity = plugin.getRarityManager().getRarity(itemStack);

            if (rarity == null) continue;
            total += rarity.getSellPrice() * itemStack.getAmount();
        }
        return total;
    }

    private void runTask() {
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::constructGUI, 5L, 5L);
    }

    @Override
    protected void registerClickables() {
    }

    @Override
    protected void registerOnCloses() {
        registerOnClose(((player1, inventory1) -> Bukkit.getScheduler().cancelTask(task)));
    }
}
