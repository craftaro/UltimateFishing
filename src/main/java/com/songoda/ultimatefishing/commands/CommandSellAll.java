package com.songoda.ultimatefishing.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.rarity.Rarity;
import java.util.List;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandSellAll extends AbstractCommand {

    final UltimateFishing instance;

    public CommandSellAll(UltimateFishing instance) {
        super(true, "sellall");
        this.instance = instance;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {

        Player player = (Player)sender;

        double totalNew = UltimateFishing.calculateTotalValue(player.getInventory());
        if (totalNew == 0) {
            instance.getLocale().getMessage("event.sell.fail").sendPrefixedMessage(player);
            return ReturnType.SUCCESS;
        }

        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null) continue;

            Rarity rarity = instance.getRarityManager().getRarity(itemStack);

            if (rarity == null) continue;
            player.getInventory().remove(itemStack);
        }

        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9)) {
            ItemStack itemStack = player.getInventory().getItemInOffHand();
            Rarity rarity = instance.getRarityManager().getRarity(itemStack);
            if (rarity != null)
                player.getInventory().setItemInOffHand(null);
        }

        EconomyManager.deposit(player, totalNew);

        instance.getLocale().getMessage("event.sell.success")
                .processPlaceholder("total", EconomyManager.formatEconomy(totalNew))
                .sendPrefixedMessage(player);

        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "ultimatefishing.sellall";
    }

    @Override
    public String getSyntax() {
        return "/uf sellall";
    }

    @Override
    public String getDescription() {
        return "Sells all fish in your inventory.";
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }
}
