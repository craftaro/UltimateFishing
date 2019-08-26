package com.songoda.ultimatefishing.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.utils.Methods;
import java.util.List;
import org.bukkit.command.CommandSender;

public class CommandUltimateFishing extends AbstractCommand {

    final UltimateFishing instance;

    public CommandUltimateFishing(UltimateFishing instance) {
        super(false, "UltimateFishing");
        this.instance = instance;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        sender.sendMessage("");
        instance.getLocale().newMessage("&7Version " + instance.getDescription().getVersion()
                + " Created with <3 by &5&l&oSongoda").sendPrefixedMessage(sender);

        for (AbstractCommand command : instance.getCommandManager().getAllCommands()) {
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

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }
}
