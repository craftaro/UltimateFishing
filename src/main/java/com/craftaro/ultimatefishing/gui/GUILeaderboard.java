package com.craftaro.ultimatefishing.gui;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.ultimatefishing.UltimateFishing;
import com.craftaro.core.gui.CustomizableGui;
import com.craftaro.core.gui.Gui;
import com.craftaro.core.gui.GuiUtils;
import com.craftaro.core.utils.ItemUtils;
import com.craftaro.core.utils.TextUtils;
import com.craftaro.ultimatefishing.player.FishingPlayer;
import com.craftaro.ultimatefishing.rarity.Rarity;
import com.craftaro.ultimatefishing.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GUILeaderboard extends CustomizableGui {

    private final UltimateFishing plugin;

    public GUILeaderboard(UltimateFishing plugin) {
        super(plugin, "leaderboard");
        this.plugin = plugin;
        setRows(6);
        setDefaultItem(null);

        setTitle(plugin.getLocale().getMessage("interface.leaderboard.title").getMessage());

        showPage();
    }

    private void showPage() {
        if (inventory != null)
            inventory.clear();
        setActionForRange(0, 53, null);

        // set up prices info (icon only)
        // TODO: need to add this line to language file
        setItem("weight", 0, 4, GuiUtils.createButtonItem(XMaterial.BOOK,
                plugin.getLocale().getMessage("interface.leaderboard.weight").getMessage(),
                plugin.getRarityManager().getRarities().stream()
                        .map(r -> ChatColor.translateAlternateColorCodes('&', "&l&" + r.getColor() + r.getRarity() + " &7 - &a" + r.getWeight()))
                        .collect(Collectors.toList())
        ));

        List<FishingPlayer> fishingPlayers = plugin.getPlayerManager().getPlayers().stream()
                .sorted(Comparator.comparingInt(FishingPlayer::getScore)).collect(Collectors.toList());

        Collections.reverse(fishingPlayers);

        int numUsers = fishingPlayers.size();
        this.pages = (int) Math.max(1, Math.ceil(numUsers / ((double) 28)));

        List<FishingPlayer> players = fishingPlayers.stream()
                .skip((page - 1) * 28).limit(28).collect(Collectors.toList());

        // enable page events
        setNextPage(0, 1, GuiUtils.createButtonItem(XMaterial.ARROW, plugin.getLocale().getMessage("interface.general.next").getMessage()));
        setPrevPage(0, 3, GuiUtils.createButtonItem(XMaterial.ARROW, plugin.getLocale().getMessage("interface.general.back").getMessage()));
        setOnPage((event) -> showPage());

        // decorate the edges
        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial(XMaterial.BLUE_STAINED_GLASS_PANE));
        ItemStack glass3 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_3.getMaterial(XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE));

        // edges will be type 3
        mirrorFill("mirrorfill_1", 0, 2, true, true, glass3);
        mirrorFill("mirrorfill_2", 1, 1, true, true, glass3);

        // decorate corners with type 2
        mirrorFill("mirrorfill_3", 0, 0, true, true, glass2);
        mirrorFill("mirrorfill_4", 1, 0, true, true, glass2);
        mirrorFill("mirrorfill_5", 0, 1, true, true, glass2);

        int place = (page - 1) * 28;
        int num = 11;
        for (FishingPlayer fishingPlayer : players) {
            if (num == 16 || num == 36)
                num = num + 2;

            place++;

            ArrayList<String> lore = new ArrayList<>();

            for (Rarity rarity : plugin.getRarityManager().getRarities()) {
                lore.add(plugin.getLocale().getMessage("interface.leaderboard.item")
                        .processPlaceholder("type", "&" + rarity.getColor() + rarity.getRarity())
                        .processPlaceholder("amount", fishingPlayer.getCaught(rarity)).getMessage());
            }

            OfflinePlayer player = Bukkit.getOfflinePlayer(fishingPlayer.getUniqueId());

            setItem(num, GuiUtils.createButtonItem(ItemUtils.getPlayerSkull(player),
                    TextUtils.formatText(plugin.getLocale().getMessage("interface.leaderboard.name")
                            .processPlaceholder("place", place)
                            .processPlaceholder("name", player.getName())
                            .processPlaceholder("score", fishingPlayer.getScore()).getMessage()), TextUtils.formatText(lore)));

            num++;
        }

    }
}
