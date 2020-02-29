package com.songoda.ultimatefishing.tasks;

import com.songoda.core.compatibility.CompatibleParticleHandler;
import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.bait.Bait;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class BaitParticleTask extends BukkitRunnable {

    private static BaitParticleTask instance;
    private final UltimateFishing plugin;

    private final Map<Entity, Bait> activeBait = new HashMap<>();

    private BaitParticleTask(UltimateFishing plugin) {
        this.plugin = plugin;
    }

    public static BaitParticleTask startTask(UltimateFishing plugin) {
        if (instance == null) {
            instance = new BaitParticleTask(plugin);
            instance.runTaskTimerAsynchronously(plugin, 0, 5);
        }

        return instance;
    }

    @Override
    public void run() {
        for (Map.Entry<Entity, Bait> entry : new HashMap<>(activeBait).entrySet()) {
            Entity entity = entry.getKey();
            Bait bait = entry.getValue();
            if (bait == null || entity == null || entity.isDead() || !entity.isValid()) {
                activeBait.remove(entity);
                continue;
            }

            int red = 0;
            int green = 0;
            int blue = 0;
            switch (bait.getColor()) {
                case "1":
                    blue = 170;
                    break;
                case "2":
                    green = 170;
                    break;
                case "3":
                    blue = 170;
                    green = 170;
                    break;
                case "4":
                    red = 170;
                    break;
                case "5":
                    red = 170;
                    blue = 170;
                    break;
                case "6":
                    red = 255;
                    green = 170;
                    break;
                case "7":
                    red = 170;
                    blue = 170;
                    green = 170;
                    break;
                case "8":
                    red = 85;
                    blue = 85;
                    green = 85;
                    break;
                case "9":
                    red = 85;
                    blue = 255;
                    green = 85;
                    break;
                case "a":
                    red = 85;
                    blue = 85;
                    green = 255;
                    break;
                case "b":
                    red = 85;
                    blue = 255;
                    green = 255;
                    break;
                case "c":
                    red = 255;
                    blue = 85;
                    green = 85;
                    break;
                case "d":
                    red = 255;
                    blue = 255;
                    green = 85;
                    break;
                case "e":
                    red = 255;
                    blue = 85;
                    green = 255;
                    break;
                case "f":
                    red = 255;
                    blue = 255;
                    green = 255;
                    break;
            }
            CompatibleParticleHandler.redstoneParticles(entry.getKey().getLocation(), red, green, blue, .8f, 2, .0005f);
        }
    }

    public void addBait(Bait bait, Entity entity) {
        activeBait.put(entity, bait);
    }
}