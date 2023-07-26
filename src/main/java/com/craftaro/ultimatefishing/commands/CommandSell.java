package com.craftaro.ultimatefishing.commands;

import com.craftaro.ultimatefishing.UltimateFishing;
import com.craftaro.ultimatefishing.gui.GUISell;
import com.craftaro.core.commands.AbstractCommand;
import com.craftaro.core.gui.GuiManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandSell extends AbstractCommand {

    private final UltimateFishing plugin;
    private final GuiManager guiManager;

    public CommandSell(UltimateFishing plugin, GuiManager guiManager) {
        super(CommandType.PLAYER_ONLY, "sell");
        this.plugin = plugin;
        this.guiManager = guiManager;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        guiManager.showGUI((Player) sender, new GUISell(plugin, (Player) sender));
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "ultimatefishing.sell";
    }

    @Override
    public String getSyntax() {
        return "sell";
    }

    @Override
    public String getDescription() {
        return "Open the sell GUI.";
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }
}
