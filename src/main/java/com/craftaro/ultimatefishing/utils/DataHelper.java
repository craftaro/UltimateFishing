package com.craftaro.ultimatefishing.utils;

import com.craftaro.core.database.DataManager;
import com.craftaro.third_party.org.jooq.Record;
import com.craftaro.third_party.org.jooq.Result;
import com.craftaro.third_party.org.jooq.impl.DSL;
import com.craftaro.ultimatefishing.UltimateFishing;
import com.craftaro.ultimatefishing.player.FishingPlayer;
import com.craftaro.ultimatefishing.rarity.Rarity;
import com.craftaro.ultimatefishing.rarity.RarityManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class DataHelper {

    private static final DataManager dataManager = UltimateFishing.getInstance().getDataManager();

    public static void updateCaught(Player player, Rarity rarity, int amount) {
        dataManager.getAsyncPool().execute(() -> {
            dataManager.getDatabaseConnector().connectDSL(dslContext -> {
                dslContext.update(DSL.table(dataManager.getTablePrefix()+"caught"))
                        .set(DSL.field("amount"), amount)
                        .where(DSL.field("uuid").eq(player.getUniqueId().toString()))
                        .and(DSL.field("rarity").eq(rarity.getRarity()))
                        .execute();
            });
        });
    }


    public static void createCaught(Player player, Rarity rarity, int amount) {
        dataManager.getAsyncPool().execute(() -> {
            dataManager.getDatabaseConnector().connectDSL(dslContext -> {
                dslContext.insertInto(DSL.table(dataManager.getTablePrefix()+"caught"))
                        .columns(DSL.field("uuid"), DSL.field("rarity"), DSL.field("amount"))
                        .values(player.getUniqueId().toString(), rarity.getRarity(), amount)
                        .execute();
            });
        });
    }

    public static void deleteCaught(OfflinePlayer player) {
        dataManager.getAsyncPool().execute(() -> {
            dataManager.getDatabaseConnector().connectDSL(dslContext -> {
                dslContext.delete(DSL.table(dataManager.getTablePrefix()+"caught"))
                        .where(DSL.field("uuid").eq(player.getUniqueId().toString()))
                        .execute();
            });
        });
    }

    public static void getPlayers(Consumer<Map<UUID, FishingPlayer>> callback) {
        UltimateFishing plugin = UltimateFishing.getInstance();
        dataManager.getAsyncPool().execute(() -> {
            dataManager.getDatabaseConnector().connectDSL(dslContext -> {
                RarityManager rarityManager = plugin.getRarityManager();

                Result<Record> records = dslContext.select().from(DSL.table(dataManager.getTablePrefix()+"caught")).fetch();
                Map<UUID, FishingPlayer> players = new HashMap<>();

                records.forEach(record -> {
                    UUID uuid = UUID.fromString(record.get("uuid").toString());
                    String rarityStr = record.get("rarity").toString();

                    if (!rarityManager.isRarity(rarityStr)) {
                        return;
                    }

                    Rarity rarity = rarityManager.getRarity(record.get("rarity").toString());
                    int amount = Integer.parseInt(record.get("amount").toString());

                    players.putIfAbsent(uuid, new FishingPlayer(uuid));

                    players.get(uuid).addCatch(rarity, amount);
                });
                Bukkit.getScheduler().runTask(plugin, () -> callback.accept(players));
            });
        });
    }
}
