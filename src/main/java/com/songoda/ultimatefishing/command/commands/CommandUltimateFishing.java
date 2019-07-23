package com.songoda.ultimatefishing.command.commands;

import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.command.AbstractCommand;
import com.songoda.ultimatefishing.utils.Methods;
import org.bukkit.command.CommandSender;

public class CommandUltimateFishing extends AbstractCommand {

    public CommandUltimateFishing() {
        super("UltimateFishing", null, false);
    }

    @Override
    protected ReturnType runCommand(UltimateFishing instance, CommandSender sender, String... args) {
        sender.sendMessage("");
        instance.getLocale().newMessage("&7Version " + instance.getDescription().getVersion()
                + " Created with <3 by &5&l&oSongoda").sendPrefixedMessage(sender);

        for (AbstractCommand command : instance.getCommandManager().getCommands()) {
            if (command.getPermissionNode() == null || sender.hasPermission(command.getPermissionNode())) {
                sender.sendMessage(Methods.formatText("&8 - &a" + command.getSyntax() + "&7 - " + command.getDescription()));
            }
        }
        sender.sendMessage("");

        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "/UltimateFishing";
    }

    @Override
    public String getDescription() {
        return "Displays this page.";
    }
}
