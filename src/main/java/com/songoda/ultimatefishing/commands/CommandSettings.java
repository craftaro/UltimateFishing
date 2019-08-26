package com.songoda.ultimatefishing.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.settings.editor.ConfigSelectionGUI;
import com.songoda.ultimatefishing.UltimateFishing;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandSettings extends AbstractCommand {

    final UltimateFishing instance;

    public CommandSettings(UltimateFishing instance) {
        super(true, "settings");
        this.instance = instance;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        new ConfigSelectionGUI(instance, (Player) sender, instance.getMainConfig(), instance.getRarityConfig());
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

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }
}
