package com.songoda.ultimatefishing.economy;

import org.bukkit.OfflinePlayer;

public interface Economy {

    boolean hasBalance(OfflinePlayer player, double cost);

    boolean withdrawBalance(OfflinePlayer player, double cost);

    boolean deposit(OfflinePlayer player, double amount);
}