package com.songoda.ultimatefishing.commands;

import com.songoda.core.library.commands.AbstractCommand;
import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.gui.GUISell;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandSell extends AbstractCommand {

    final UltimateFishing instance;

    public CommandSell(UltimateFishing instance) {
        super(true, "sell");
        this.instance = instance;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        new GUISell(instance, (Player)sender);
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "ultimatefishing.sell";
    }

    @Override
    public String getSyntax() {
        return "/uf sell";
    }

    @Override
    public String getDescription() {
        return "Open the sell GUI.";
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }
}
