package com.songoda.ultimatefishing.command.commands;

import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.command.AbstractCommand;
import com.songoda.ultimatefishing.gui.GUISell;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSell extends AbstractCommand {

    public CommandSell(AbstractCommand parent) {
        super("sell", parent, true);
    }

    @Override
    protected ReturnType runCommand(UltimateFishing instance, CommandSender sender, String... args) {
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
}
