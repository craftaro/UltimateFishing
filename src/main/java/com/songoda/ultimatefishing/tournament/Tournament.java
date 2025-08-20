package com.songoda.ultimatefishing.tournament;

import com.songoda.core.hooks.EconomyManager;
import com.songoda.core.utils.TextUtils;
import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.rarity.Rarity;
import com.songoda.ultimatefishing.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Tournament {
    private final UltimateFishing plugin;
    private final String name;
    private final long duration;
    private final Map<UUID, TournamentParticipant> participants = new ConcurrentHashMap<>();
    private final Map<String, Integer> rarityPoints = new HashMap<>();
    
    private TournamentState state = TournamentState.WAITING;
    private long startTime;
    private long endTime;
    private int taskId = -1;
    private int countdownSeconds;
    
    public Tournament(UltimateFishing plugin, String name, long duration) {
        this.plugin = plugin;
        this.name = name;
        this.duration = duration;
        loadRarityPoints();
    }
    
    private void loadRarityPoints() {
        for (Rarity rarity : plugin.getRarityManager().getRarities()) {
            rarityPoints.put(rarity.getRarity(), rarity.getTournamentValue());
        }
    }
    
    public void startCountdown(int seconds) {
        if (state != TournamentState.WAITING) {
            return;
        }
        
        state = TournamentState.COUNTDOWN;
        countdownSeconds = seconds;
        
        taskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (countdownSeconds <= 0) {
                start();
                return;
            }
            
            if (countdownSeconds == 600) { // 10 minutes
                broadcast("&b&lFISHING TOURNAMENT &7» &fTournament '" + name + "' starting in &b10 minutes!");
            } else if (countdownSeconds == 300) { // 5 minutes
                broadcast("&b&lFISHING TOURNAMENT &7» &fTournament '" + name + "' starting in &b5 minutes!");
            } else if (countdownSeconds == 60) { // 1 minute
                broadcast("&b&lFISHING TOURNAMENT &7» &fTournament '" + name + "' starting in &b1 minute!");
            } else if (countdownSeconds == 30) { // 30 seconds
                broadcast("&b&lFISHING TOURNAMENT &7» &fTournament '" + name + "' starting in &b30 seconds!");
            } else if (countdownSeconds == 10) { // 10 seconds
                broadcast("&b&lFISHING TOURNAMENT &7» &fTournament '" + name + "' starting in &b10 seconds!");
            } else if (countdownSeconds <= 3 && countdownSeconds > 0) {
                broadcast("&b&lFISHING TOURNAMENT &7» &fStarting in &b" + countdownSeconds + "...");
            }
            
            countdownSeconds--;
        }, 0L, 20L).getTaskId();
    }
    
    private void start() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
        
        state = TournamentState.ACTIVE;
        startTime = System.currentTimeMillis();
        endTime = startTime + (duration * 1000);
        
        broadcast("&b&lFISHING TOURNAMENT &7» &fThe tournament has &aSTARTED! &fCatch as many fish as you can!");
        broadcast("&b&lFISHING TOURNAMENT &7» &fDuration: &b" + (duration / 60) + " minutes");
        
        taskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            long remaining = endTime - System.currentTimeMillis();
            
            if (remaining <= 0) {
                end();
                return;
            }
            
            long secondsRemaining = remaining / 1000;
            if (secondsRemaining == 60) {
                broadcast("&b&lFISHING TOURNAMENT &7» &f1 minute remaining!");
            } else if (secondsRemaining == 30) {
                broadcast("&b&lFISHING TOURNAMENT &7» &f30 seconds remaining!");
            } else if (secondsRemaining == 10) {
                broadcast("&b&lFISHING TOURNAMENT &7» &f10 seconds remaining!");
            }
        }, 20L, 20L).getTaskId();
    }
    
    private void end() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
        
        state = TournamentState.ENDED;
        
        List<TournamentParticipant> winners = getTopParticipants(3);
        
        broadcast("&b&lFISHING TOURNAMENT &7» &fThe tournament has &cENDED!");
        
        if (winners.isEmpty()) {
            broadcast("&b&lFISHING TOURNAMENT &7» &fNo participants caught any fish!");
            return;
        }
        
        broadcast("&b&lFISHING TOURNAMENT &7» &6&lWinners:");
        
        for (int i = 0; i < winners.size(); i++) {
            TournamentParticipant winner = winners.get(i);
            Player player = Bukkit.getPlayer(winner.getPlayerUUID());
            String playerName = player != null ? player.getName() : "Unknown";
            
            String place = "";
            switch (i) {
                case 0:
                    place = "&6&l1st";
                    break;
                case 1:
                    place = "&7&l2nd";
                    break;
                case 2:
                    place = "&c&l3rd";
                    break;
            }
            
            broadcast(place + " &7- &f" + playerName + " &7(&b" + winner.getPoints() + " points&7)");
        }
        
        distributeRewards();
    }
    
    public void recordCatch(Player player, Rarity rarity) {
        if (state != TournamentState.ACTIVE) {
            return;
        }
        
        TournamentParticipant participant = participants.computeIfAbsent(
            player.getUniqueId(), 
            uuid -> new TournamentParticipant(uuid)
        );
        
        int points = rarityPoints.getOrDefault(rarity.getRarity(), 1);
        participant.addCatch(rarity, points);
        
        player.sendMessage(TextUtils.formatText("&b&lTOURNAMENT &7» &fYou caught a &b" + rarity.getColor() + rarity.getRarity() +
            " &ffish! &7(+" + points + " points, Total: " + participant.getPoints() + ")"));
    }
    
    public void addParticipant(Player player) {
        if (state == TournamentState.ACTIVE || state == TournamentState.COUNTDOWN) {
            participants.putIfAbsent(player.getUniqueId(), new TournamentParticipant(player.getUniqueId()));
        }
    }
    
    public void removeParticipant(Player player) {
        participants.remove(player.getUniqueId());
    }
    
    private void distributeRewards() {
        if (!plugin.getTournamentConfig().getBoolean("Tournament.Rewards.Enabled")) {
            return;
        }
        
        List<TournamentParticipant> winners = getTopParticipants(3);
        
        for (int i = 0; i < winners.size() && i < 3; i++) {
            TournamentParticipant winner = winners.get(i);
            Player player = Bukkit.getPlayer(winner.getPlayerUUID());
            
            if (player == null) {
                continue;
            }
            
            String rewardType;
            double economyAmount = 0;
            List<String> items = null;
            
            switch (i) {
                case 0: // First place
                    rewardType = plugin.getTournamentConfig().getString("Tournament.Rewards.First Place.Type");
                    economyAmount = plugin.getTournamentConfig().getDouble("Tournament.Rewards.First Place.Economy Amount");
                    items = plugin.getTournamentConfig().getStringList("Tournament.Rewards.First Place.Items");
                    break;
                case 1: // Second place
                    rewardType = plugin.getTournamentConfig().getString("Tournament.Rewards.Second Place.Type");
                    economyAmount = plugin.getTournamentConfig().getDouble("Tournament.Rewards.Second Place.Economy Amount");
                    items = plugin.getTournamentConfig().getStringList("Tournament.Rewards.Second Place.Items");
                    break;
                case 2: // Third place
                    rewardType = plugin.getTournamentConfig().getString("Tournament.Rewards.Third Place.Type");
                    economyAmount = plugin.getTournamentConfig().getDouble("Tournament.Rewards.Third Place.Economy Amount");
                    items = plugin.getTournamentConfig().getStringList("Tournament.Rewards.Third Place.Items");
                    break;
                default:
                    continue;
            }
            
            // Give economy reward
            if ((rewardType.equalsIgnoreCase("ECONOMY") || rewardType.equalsIgnoreCase("BOTH")) && economyAmount > 0) {
                EconomyManager.deposit(player, economyAmount);
                player.sendMessage(TextUtils.formatText("&b&lTOURNAMENT &7» &fYou received &a$" + economyAmount + " &ffor placing " + getPlaceString(i + 1) + "!"));
            }
            
            // Give item rewards
            if ((rewardType.equalsIgnoreCase("ITEM") || rewardType.equalsIgnoreCase("BOTH")) && items != null) {
                for (String itemString : items) {
                    String[] parts = itemString.split(":");
                    if (parts.length == 2) {
                        try {
                            org.bukkit.Material material = org.bukkit.Material.valueOf(parts[0].toUpperCase());
                            int amount = Integer.parseInt(parts[1]);
                            
                            org.bukkit.inventory.ItemStack itemStack = new org.bukkit.inventory.ItemStack(material, amount);
                            
                            if (player.getInventory().firstEmpty() != -1) {
                                player.getInventory().addItem(itemStack);
                            } else {
                                player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                            }
                        } catch (Exception e) {
                            plugin.getLogger().warning("Invalid tournament reward item: " + itemString);
                        }
                    }
                }
                player.sendMessage(TextUtils.formatText("&b&lTOURNAMENT &7» &fYou received item rewards for placing " + getPlaceString(i + 1) + "!"));
            }
        }
    }
    
    private String getPlaceString(int place) {
        switch (place) {
            case 1:
                return "&6&l1st";
            case 2:
                return "&7&l2nd";
            case 3:
                return "&c&l3rd";
            default:
                return place + "th";
        }
    }
    
    private void broadcast(String message) {
        String formatted = TextUtils.formatText(message);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(formatted);
        }
    }
    
    public List<TournamentParticipant> getTopParticipants(int count) {
        return participants.values().stream()
            .sorted((a, b) -> Integer.compare(b.getPoints(), a.getPoints()))
            .limit(count)
            .collect(Collectors.toList());
    }
    
    public List<TournamentParticipant> getAllParticipants() {
        return new ArrayList<>(participants.values());
    }
    
    public boolean isParticipant(UUID playerUUID) {
        return participants.containsKey(playerUUID);
    }
    
    public TournamentParticipant getParticipant(UUID playerUUID) {
        return participants.get(playerUUID);
    }
    
    public String getName() {
        return name;
    }
    
    public TournamentState getState() {
        return state;
    }
    
    public long getRemainingTime() {
        if (state != TournamentState.ACTIVE) {
            return 0;
        }
        return Math.max(0, endTime - System.currentTimeMillis());
    }
    
    public long getDuration() {
        return duration;
    }
    
    public int getParticipantCount() {
        return participants.size();
    }
    
    public enum TournamentState {
        WAITING,
        COUNTDOWN,
        ACTIVE,
        ENDED
    }
}