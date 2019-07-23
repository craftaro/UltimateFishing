package com.songoda.ultimatefishing.command.commands;

import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSettings extends AbstractCommand {

    public CommandSettings(AbstractCommand parent) {
        super("Settings", parent, true);
    }

    @Override
    protected ReturnType runCommand(UltimateFishing instance, CommandSender sender, String... args) {
        instance.getSettingsManager().openSettingsManager((Player) sender);
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "ultimatefishing.admin";
    }

    @Override
    public String getSyntax() {
        return "/uf settings";
    }

    @Override
    public String getDescription() {
        return "Edit the UltimateFishing Settings.";
    }
}
