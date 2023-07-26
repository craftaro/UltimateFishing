package com.craftaro.ultimatefishing.player;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
