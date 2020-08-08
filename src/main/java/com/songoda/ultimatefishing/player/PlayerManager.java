package com.songoda.ultimatefishing.player;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerManager {

    private final Map<UUID, FishingPlayer> registeredPlayers = new HashMap<>();

    public FishingPlayer getPlayer(Player player) {
        registeredPlayers.putIfAbsent(player.getUniqueId(), new FishingPlayer(player.getUniqueId()));
        return registeredPlayers.get(player.getUniqueId());
    }

    public List<FishingPlayer> getPlayers() {
        return new ArrayList<>(registeredPlayers.values());
    }

    public void addPlayer(FishingPlayer player) {
        registeredPlayers.put(player.getUniqueId(), player);
    }

    public void resetPlayer(OfflinePlayer offlinePlayer) {
        registeredPlayers.remove(offlinePlayer.getUniqueId());
    }
}
