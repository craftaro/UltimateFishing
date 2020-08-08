package com.songoda.ultimatefishing.database;

import com.songoda.core.database.DataManagerAbstract;
import com.songoda.core.database.DatabaseConnector;
import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.player.FishingPlayer;
import com.songoda.ultimatefishing.rarity.Rarity;
import com.songoda.ultimatefishing.rarity.RarityManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class DataManager extends DataManagerAbstract {

    public DataManager(DatabaseConnector databaseConnector, Plugin plugin) {
        super(databaseConnector, plugin);
    }

    public void updateCaught(Player player, Rarity rarity, int amount) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String updateSpawner = "UPDATE " + this.getTablePrefix() + "caught SET amount = ? WHERE uuid = ? AND rarity = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateSpawner)) {
                statement.setInt(1, amount);
                statement.setString(2, player.getUniqueId().toString());
                statement.setString(3, rarity.getRarity());
                statement.executeUpdate();
            }
        }));
    }


    public void createCaught(Player player, Rarity rarity, int amount) {
        this.async(() -> this.databaseConnector.connect(connection -> {

            String createSpawner = "INSERT INTO " + this.getTablePrefix() + "caught (uuid, rarity, amount) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createSpawner)) {
                statement.setString(1, player.getUniqueId().toString());
                statement.setString(2, rarity.getRarity());
                statement.setInt(3, amount);
                statement.executeUpdate();
            }
        }));
    }

    public void deleteCaught(Player player) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String deleteSpawner = "DELETE FROM " + this.getTablePrefix() + "caught WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(deleteSpawner)) {
                statement.setString(1, player.getUniqueId().toString());
                statement.executeUpdate();
            }
        }));
    }

    public void getPlayers(Consumer<Map<UUID, FishingPlayer>> callback) {
        UltimateFishing plugin = UltimateFishing.getInstance();
        this.async(() -> this.databaseConnector.connect(connection -> {
            String selectSpawners = "SELECT * FROM " + this.getTablePrefix() + "caught";
            RarityManager rarityManager = plugin.getRarityManager();

            Map<UUID, FishingPlayer> players = new HashMap<>();

            try (Statement statement = connection.createStatement()) {
                ResultSet result = statement.executeQuery(selectSpawners);
                while (result.next()) {
                    UUID uuid = UUID.fromString(result.getString("uuid"));

                    String rarityStr = result.getString("rarity");

                    if (!rarityManager.isRarity(rarityStr))
                        continue;

                    Rarity rarity = rarityManager.getRarity(result.getString("rarity"));
                    int amount = result.getInt("amount");

                    players.putIfAbsent(uuid, new FishingPlayer(uuid));

                    players.get(uuid).addCatch(rarity, amount);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            this.sync(() -> callback.accept(players));
        }));
    }
}
