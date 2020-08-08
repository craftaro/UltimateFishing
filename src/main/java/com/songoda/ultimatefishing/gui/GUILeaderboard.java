package com.songoda.ultimatefishing.gui;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.ItemUtils;
import com.songoda.core.utils.TextUtils;
import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.player.FishingPlayer;
import com.songoda.ultimatefishing.rarity.Rarity;
import com.songoda.ultimatefishing.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GUILeaderboard extends Gui {

    private final UltimateFishing plugin;

    public GUILeaderboard(UltimateFishing plugin, Player player) {
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

        List<FishingPlayer> fishingPlayers = plugin.getPlayerManager().getPlayers().stream()
                .sorted(Comparator.comparingInt(FishingPlayer::getScore)).collect(Collectors.toList());

        Collections.reverse(fishingPlayers);

        int numUsers = fishingPlayers.size();
        this.pages = (int) Math.max(1, Math.ceil(numUsers / ((double) 28)));

        List<FishingPlayer> players = fishingPlayers.stream()
                .skip((page - 1) * 28).limit(28).collect(Collectors.toList());

        // enable page events
        setNextPage(0, 1, GuiUtils.createButtonItem(CompatibleMaterial.ARROW, plugin.getLocale().getMessage("interface.general.next").getMessage()));
        setPrevPage(0, 3, GuiUtils.createButtonItem(CompatibleMaterial.ARROW, plugin.getLocale().getMessage("interface.general.back").getMessage()));
        setOnPage((event) -> showPage());

        // decorate the edges
        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial(CompatibleMaterial.BLUE_STAINED_GLASS_PANE));
        ItemStack glass3 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_3.getMaterial(CompatibleMaterial.LIGHT_BLUE_STAINED_GLASS_PANE));

        // edges will be type 3
        GuiUtils.mirrorFill(this, 0, 2, true, true, glass3);
        GuiUtils.mirrorFill(this, 1, 1, true, true, glass3);

        // decorate corners with type 2
        GuiUtils.mirrorFill(this, 0, 0, true, true, glass2);
        GuiUtils.mirrorFill(this, 1, 0, true, true, glass2);
        GuiUtils.mirrorFill(this, 0, 1, true, true, glass2);

        int place = (page - 1) * 28;
        int num = 11;
        for (FishingPlayer fishingPlayer : players) {
            if (num == 16 || num == 36)
                num = num + 2;

            place ++;

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
                            .processPlaceholder("name", player.getName()).getMessage()), TextUtils.formatText(lore)));

            num++;
        }

    }
}
