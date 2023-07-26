package com.craftaro.ultimatefishing.commands;

import com.craftaro.ultimatefishing.UltimateFishing;
import com.craftaro.ultimatefishing.gui.GUILeaderboard;
import com.craftaro.core.commands.AbstractCommand;
import com.craftaro.core.gui.GuiManager;
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
