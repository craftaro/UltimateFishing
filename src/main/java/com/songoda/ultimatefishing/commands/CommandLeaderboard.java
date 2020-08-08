package com.songoda.ultimatefishing.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.gui.GuiManager;
import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.gui.GUIBaitShop;
import com.songoda.ultimatefishing.gui.GUILeaderboard;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandLeaderboard extends AbstractCommand {

    final UltimateFishing instance;
    final GuiManager guiManager;

    public CommandLeaderboard(UltimateFishing instance, GuiManager guiManager) {
        super(true, "leaderboard");
        this.instance = instance;
        this.guiManager = guiManager;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        guiManager.showGUI((Player) sender, new GUILeaderboard(instance, (Player) sender));
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "ultimatefishing.leaderboard";
    }

    @Override
    public String getSyntax() {
        return "leaderboard";
    }

    @Override
    public String getDescription() {
        return "Open the leaderboard.";
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }
}
