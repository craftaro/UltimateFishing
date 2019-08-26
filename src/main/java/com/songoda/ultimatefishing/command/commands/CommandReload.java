package com.songoda.ultimatefishing.command.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.ultimatefishing.UltimateFishing;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CommandReload extends AbstractCommand {

    final UltimateFishing instance;

    public CommandReload(UltimateFishing instance) {
        super(false, "reload");
        this.instance = instance;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        instance.reload();
        instance.getLocale().getMessage("&7Configuration and Language files reloaded.").sendPrefixedMessage(sender);
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "ultimatefishing.admin";
    }

    @Override
    public String getSyntax() {
        return "/uf reload";
    }

    @Override
    public String getDescription() {
        return "Reload the Configuration and Language files.";
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }
}
