package com.songoda.ultimatefishing.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.gui.GuiManager;
import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.gui.GUILeaderboard;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandLeaderboard extends AbstractCommand {

    private final UltimateFishing plugin;
    private final GuiManager guiManager;

    public CommandLeaderboard(UltimateFishing plugin, GuiManager guiManager) {
        super(CommandType.PLAYER_ONLY, "leaderboard");
        this.plugin = plugin;
        this.guiManager = guiManager;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        guiManager.showGUI((Player) sender, new GUILeaderboard(plugin));
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
