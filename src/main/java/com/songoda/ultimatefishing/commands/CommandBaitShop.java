package com.songoda.ultimatefishing.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.gui.GuiManager;
import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.gui.GUIBaitShop;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandBaitShop extends AbstractCommand {

    final UltimateFishing instance;
    final GuiManager guiManager;

    public CommandBaitShop(UltimateFishing instance, GuiManager guiManager) {
        super(true, "baitshop");
        this.instance = instance;
        this.guiManager = guiManager;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        guiManager.showGUI((Player) sender, new GUIBaitShop(instance));
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "ultimatefishing.baitshop";
    }

    @Override
    public String getSyntax() {
        return "baitshop";
    }

    @Override
    public String getDescription() {
        return "Open the bait shop.";
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }
}
