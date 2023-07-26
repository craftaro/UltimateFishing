package com.craftaro.ultimatefishing.commands;

import com.craftaro.ultimatefishing.UltimateFishing;
import com.craftaro.core.commands.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CommandResetPlayer extends AbstractCommand {

    private final UltimateFishing plugin;

    public CommandResetPlayer(UltimateFishing plugin) {
        super(CommandType.PLAYER_ONLY, "resetplayer");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length != 1) return ReturnType.SYNTAX_ERROR;

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);

        plugin.getPlayerManager().resetPlayer(offlinePlayer);
        plugin.getDataManager().deleteCaught(offlinePlayer);

        plugin.getLocale().newMessage("&cPlayer reset successfully.").sendPrefixedMessage(sender);

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "ultimatefishing.resetplayer";
    }

    @Override
    public String getSyntax() {
        return "resetplayer";
    }

    @Override
    public String getDescription() {
        return "Reset a players catches.";
    }
}
