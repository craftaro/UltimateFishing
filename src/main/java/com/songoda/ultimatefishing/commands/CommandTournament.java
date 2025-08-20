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
                    try {
                        duration = Long.parseLong(lastArg);
                        // If successful, everything except the last argument is the name
                        if (args.length > 2) {
                            StringBuilder nameBuilder = new StringBuilder();
                            for (int i = 1; i < args.length - 1; i++) {
                                if (i > 1) nameBuilder.append(" ");
                                nameBuilder.append(args[i]);
                            }
                            name = nameBuilder.toString();
                        }
                    } catch (NumberFormatException e) {
                        // If last argument is not a number, all arguments after "start" are the name
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
                
            case "join":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(TextUtils.formatText("&cOnly players can join tournaments!"));
                    return ReturnType.SUCCESS;
                }
                
                Player player = (Player) sender;
                Tournament tournament = plugin.getTournamentManager().getActiveTournament();
                
                if (tournament == null || tournament.getState() == Tournament.TournamentState.ENDED) {
                    player.sendMessage(TextUtils.formatText("&cNo active tournament to join!"));
                    return ReturnType.SUCCESS;
                }
                
                if (tournament.getState() != Tournament.TournamentState.WAITING && tournament.getState() != Tournament.TournamentState.COUNTDOWN) {
                    player.sendMessage(TextUtils.formatText("&cThe tournament has already started!"));
                    return ReturnType.SUCCESS;
                }
                
                if (tournament.isParticipant(player.getUniqueId())) {
                    player.sendMessage(TextUtils.formatText("&cYou're already in the tournament!"));
                    return ReturnType.SUCCESS;
                }
                
                tournament.addParticipant(player);
                player.sendMessage(TextUtils.formatText("&aYou've joined the tournament!"));
                break;
                
            case "leave":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(TextUtils.formatText("&cOnly players can leave tournaments!"));
                    return ReturnType.SUCCESS;
                }
                
                player = (Player) sender;
                tournament = plugin.getTournamentManager().getActiveTournament();
                
                if (tournament == null) {
                    player.sendMessage(TextUtils.formatText("&cNo active tournament to leave!"));
                    return ReturnType.SUCCESS;
                }
                
                if (!tournament.isParticipant(player.getUniqueId())) {
                    player.sendMessage(TextUtils.formatText("&cYou're not in the tournament!"));
                    return ReturnType.SUCCESS;
                }
                
                tournament.removeParticipant(player);
                player.sendMessage(TextUtils.formatText("&aYou've left the tournament!"));
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
            sender.sendMessage(TextUtils.formatText("&7/tournament start [name] [duration] &f- Start a tournament"));
            sender.sendMessage(TextUtils.formatText("&7/tournament stop &f- Stop the current tournament"));
            sender.sendMessage(TextUtils.formatText("&7/tournament join &f- Join a tournament"));
            sender.sendMessage(TextUtils.formatText("&7/tournament leave &f- Leave the tournament"));
            sender.sendMessage(TextUtils.formatText("&7/tournament top &f- View leaderboard"));
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
            return List.of("start", "stop", "join", "leave", "top", "leaderboard");
        }
        return null;
    }
    
    @Override
    public String getPermissionNode() {
        return "ultimatefishing.tournament";
    }
    
    @Override
    public String getSyntax() {
        return "tournament [start/stop/join/leave/top]";
    }
    
    @Override
    public String getDescription() {
        return "Manage and participate in fishing tournaments.";
    }
}