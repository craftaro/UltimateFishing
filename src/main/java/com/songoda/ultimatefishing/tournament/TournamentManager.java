package com.songoda.ultimatefishing.tournament;

import com.songoda.core.configuration.Config;
import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.rarity.Rarity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

public class TournamentManager {
    private final UltimateFishing plugin;
    private final Config config;
    private Tournament activeTournament;
    private int autoStartTaskId = -1;
    private long lastTournamentEnd = 0;
    
    public TournamentManager(UltimateFishing plugin) {
        this.plugin = plugin;
        this.config = plugin.getTournamentConfig();
        
        if (config.getBoolean("Tournament.Auto Start.Enabled")) {
            scheduleNextAutoTournament();
        }
    }
    
    public boolean createTournament(String name, long duration) {
        if (activeTournament != null && activeTournament.getState() != Tournament.TournamentState.ENDED) {
            return false;
        }
        
        activeTournament = new Tournament(plugin, name, duration);
        return true;
    }
    
    public boolean startTournament(int countdownSeconds) {
        if (activeTournament == null || activeTournament.getState() != Tournament.TournamentState.WAITING) {
            return false;
        }
        
        int minPlayers = config.getInt("Tournament.Minimum Players");
        if (Bukkit.getOnlinePlayers().size() < minPlayers) {
            return false;
        }
        
        // Players will be automatically added when they catch fish
        
        activeTournament.startCountdown(countdownSeconds);
        return true;
    }
    
    public boolean stopTournament() {
        if (activeTournament == null || activeTournament.getState() == Tournament.TournamentState.ENDED) {
            return false;
        }
        
        // Force end the tournament
        Bukkit.getScheduler().runTask(plugin, () -> {
            activeTournament = null;
        });
        
        lastTournamentEnd = System.currentTimeMillis();
        
        if (config.getBoolean("Tournament.Auto Start.Enabled")) {
            scheduleNextAutoTournament();
        }
        
        return true;
    }
    
    public void recordCatch(Player player, Rarity rarity) {
        if (activeTournament != null && activeTournament.getState() == Tournament.TournamentState.ACTIVE) {
            activeTournament.recordCatch(player, rarity);
        }
    }
    
    
    private void scheduleNextAutoTournament() {
        if (autoStartTaskId != -1) {
            Bukkit.getScheduler().cancelTask(autoStartTaskId);
        }
        
        long minInterval = config.getInt("Tournament.Auto Start.Min Interval") * 60L * 20L; // Convert minutes to ticks
        long maxInterval = config.getInt("Tournament.Auto Start.Max Interval") * 60L * 20L;
        
        long delay = ThreadLocalRandom.current().nextLong(minInterval, maxInterval + 1);
        
        autoStartTaskId = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (Bukkit.getOnlinePlayers().size() >= config.getInt("Tournament.Minimum Players")) {
                long duration = config.getInt("Tournament.Default Duration");
                if (duration <= 0) duration = 300; // Fallback to 5 minutes
                String name = "Auto Tournament";
                
                if (createTournament(name, duration)) {
                    startTournament(config.getInt("Tournament.Countdown Duration"));
                }
            }
            
            scheduleNextAutoTournament();
        }, delay).getTaskId();
    }
    
    public void shutdown() {
        if (autoStartTaskId != -1) {
            Bukkit.getScheduler().cancelTask(autoStartTaskId);
            autoStartTaskId = -1;
        }
        
        if (activeTournament != null) {
            stopTournament();
        }
    }
    
    public Tournament getActiveTournament() {
        return activeTournament;
    }
    
    public boolean hasActiveTournament() {
        return activeTournament != null && activeTournament.getState() != Tournament.TournamentState.ENDED;
    }
    
    public boolean isInTournament(Player player) {
        return activeTournament != null && activeTournament.isParticipant(player.getUniqueId());
    }
}