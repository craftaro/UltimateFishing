package com.songoda.ultimatefishing.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.configuration.Config;
import com.songoda.core.utils.TextUtils;
import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.tournament.Tournament;
import com.songoda.ultimatefishing.tournament.TournamentParticipant;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandTournament extends AbstractCommand {
    private final UltimateFishing plugin;
    private final Config config;
    
    public CommandTournament(UltimateFishing plugin) {
        super(false, "tournament");
        this.plugin = plugin;
        this.config = plugin.getTournamentConfig();
    }
    
    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (!config.getBoolean("Tournament.Enabled")) {
            sender.sendMessage(TextUtils.formatText("&cTournaments are disabled!"));
            return ReturnType.SUCCESS;
        }
        
        if (args.length == 0) {
            // Show tournament status/help
            showTournamentInfo(sender);
            return ReturnType.SUCCESS;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "start":
                if (!sender.hasPermission("ultimatefishing.tournament.start")) {
                    sender.sendMessage(TextUtils.formatText("&cYou don't have permission to start tournaments!"));
                    return ReturnType.SUCCESS;
                }
                
                String name = "Manual Tournament";
                long duration = config.getInt("Tournament.Default Duration");
                
                // Fallback to 300 seconds (5 minutes) if config value is 0 or invalid
                if (duration <= 0) {
                    duration = 300;
                }
                
                if (args.length > 1) {
                    // Try to parse the last argument as duration
                    String lastArg = args[args.length - 1];
                    Long parsedDuration = parseDuration(lastArg);
                    
                    if (parsedDuration != null) {
                        duration = parsedDuration;
                        // If successful, everything except the last argument is the name
                        if (args.length > 2) {
                            StringBuilder nameBuilder = new StringBuilder();
                            for (int i = 1; i < args.length - 1; i++) {
                                if (i > 1) nameBuilder.append(" ");
                                nameBuilder.append(args[i]);
                            }
                            name = nameBuilder.toString();
                        }
                    } else {
                        // If last argument is not a valid duration, all arguments after "start" are the name
                        StringBuilder nameBuilder = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            if (i > 1) nameBuilder.append(" ");
                            nameBuilder.append(args[i]);
                        }
                        name = nameBuilder.toString();
                    }
                }
                
                if (plugin.getTournamentManager().createTournament(name, duration)) {
                    if (plugin.getTournamentManager().startTournament(config.getInt("Tournament.Countdown Duration"))) {
                        sender.sendMessage(TextUtils.formatText("&aTournament '" + name + "' has been started!"));
                    } else {
                        sender.sendMessage(TextUtils.formatText("&cNot enough players online! Minimum: " + config.getInt("Tournament.Minimum Players")));
                    }
                } else {
                    sender.sendMessage(TextUtils.formatText("&cA tournament is already active!"));
                }
                break;
                
            case "stop":
                if (!sender.hasPermission("ultimatefishing.tournament.stop")) {
                    sender.sendMessage(TextUtils.formatText("&cYou don't have permission to stop tournaments!"));
                    return ReturnType.SUCCESS;
                }
                
                if (plugin.getTournamentManager().stopTournament()) {
                    sender.sendMessage(TextUtils.formatText("&aTournament has been stopped!"));
                } else {
                    sender.sendMessage(TextUtils.formatText("&cNo active tournament to stop!"));
                }
                break;
                
                
            case "top":
            case "leaderboard":
                showLeaderboard(sender);
                break;
                
            default:
                showTournamentInfo(sender);
                break;
        }
        
        return ReturnType.SUCCESS;
    }
    
    private void showTournamentInfo(CommandSender sender) {
        Tournament tournament = plugin.getTournamentManager().getActiveTournament();
        
        sender.sendMessage(TextUtils.formatText("&b&lFISHING TOURNAMENT"));
        sender.sendMessage("");
        
        if (tournament == null || tournament.getState() == Tournament.TournamentState.ENDED) {
            sender.sendMessage(TextUtils.formatText("&7No active tournament."));
            sender.sendMessage("");
            sender.sendMessage(TextUtils.formatText("&fCommands:"));
            plugin.getLocale().getMessage("tournament.commands.start").sendPrefixedMessage(sender);
            plugin.getLocale().getMessage("tournament.commands.startdesc").sendPrefixedMessage(sender);
            plugin.getLocale().getMessage("tournament.commands.stop").sendPrefixedMessage(sender);
            plugin.getLocale().getMessage("tournament.commands.top").sendPrefixedMessage(sender);
        } else {
            sender.sendMessage(TextUtils.formatText("&fTournament: &b" + tournament.getName()));
            sender.sendMessage(TextUtils.formatText("&fStatus: &b" + tournament.getState().toString()));
            sender.sendMessage(TextUtils.formatText("&fParticipants: &b" + tournament.getParticipantCount()));
            
            if (tournament.getState() == Tournament.TournamentState.ACTIVE) {
                long remaining = tournament.getRemainingTime();
                long minutes = remaining / 60000;
                long seconds = (remaining % 60000) / 1000;
                sender.sendMessage(TextUtils.formatText("&fTime Remaining: &b" + minutes + "m " + seconds + "s"));
                
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    TournamentParticipant participant = tournament.getParticipant(player.getUniqueId());
                    if (participant != null) {
                        sender.sendMessage(TextUtils.formatText("&fYour Points: &b" + participant.getPoints()));
                        sender.sendMessage(TextUtils.formatText("&fYour Catches: &b" + participant.getTotalCatches()));
                    }
                }
            }
        }
    }
    
    private void showLeaderboard(CommandSender sender) {
        Tournament tournament = plugin.getTournamentManager().getActiveTournament();
        
        if (tournament == null || tournament.getState() == Tournament.TournamentState.WAITING) {
            sender.sendMessage(TextUtils.formatText("&cNo active tournament!"));
            return;
        }
        
        List<TournamentParticipant> topParticipants = tournament.getTopParticipants(10);
        
        sender.sendMessage(TextUtils.formatText("&b&lTOURNAMENT LEADERBOARD"));
        sender.sendMessage("");
        
        if (topParticipants.isEmpty()) {
            sender.sendMessage(TextUtils.formatText("&7No participants have caught any fish yet!"));
            return;
        }
        
        for (int i = 0; i < topParticipants.size(); i++) {
            TournamentParticipant participant = topParticipants.get(i);
            Player player = plugin.getServer().getPlayer(participant.getPlayerUUID());
            String playerName = player != null ? player.getName() : "Unknown";
            
            String position = String.format("&7%d. ", i + 1);
            if (i == 0) position = "&6&l1. ";
            else if (i == 1) position = "&7&l2. ";
            else if (i == 2) position = "&c&l3. ";
            
            sender.sendMessage(TextUtils.formatText(position + "&f" + playerName + " &7- &b" + 
                participant.getPoints() + " points &7(" + participant.getTotalCatches() + " catches)"));
        }
    }
    
    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if (args.length == 1) {
            return List.of("start", "stop", "top", "leaderboard");
        }
        return null;
    }
    
    @Override
    public String getPermissionNode() {
        return "ultimatefishing.tournament";
    }
    
    @Override
    public String getSyntax() {
        return "tournament [start <name> <duration>|stop|top]";
    }
    
    @Override
    public String getDescription() {
        return "Manage and participate in fishing tournaments.";
    }
    
    private Long parseDuration(String durationStr) {
        if (durationStr == null || durationStr.isEmpty()) {
            return null;
        }
        
        // Try to parse as plain number (seconds)
        try {
            return Long.parseLong(durationStr);
        } catch (NumberFormatException ignored) {}
        
        // Try to parse with time suffix
        String numPart = durationStr.substring(0, durationStr.length() - 1);
        char suffix = Character.toLowerCase(durationStr.charAt(durationStr.length() - 1));
        
        try {
            long num = Long.parseLong(numPart);
            switch (suffix) {
                case 's': // seconds
                    return num;
                case 'm': // minutes
                    return num * 60;
                case 'h': // hours
                    return num * 3600;
                case 'd': // days
                    return num * 86400;
                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }
}