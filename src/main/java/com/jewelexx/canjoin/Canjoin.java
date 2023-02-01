package com.jewelexx.canjoin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.UUID;

class CanJoinEvents implements Listener {
    Canjoin plugin;

    CanJoinEvents(Canjoin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();

        Integer time = plugin.playerTimes.get(playerId);

        if (time >= this.plugin.maxTime) {
            Component kickMessage = Component.text("You have been kicked for playing too long");
            event.getPlayer().kick(kickMessage);
        }
    }
}

public final class Canjoin extends JavaPlugin {
    int maxTime;
    HashMap<UUID, Integer> playerTimes = new HashMap<UUID, Integer>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        maxTime = getConfig().getInt("max-time");

        // Register events
        getServer().getPluginManager().registerEvents(new CanJoinEvents(this), this);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach((player) -> updatePlayerTime(player));
            }
        }, 20L, 20L);
    }

    public void updatePlayerTime(Player player) {
        UUID playerId = player.getUniqueId();
        Integer time = playerTimes.get(playerId);

        if (time == null) {
            time = 0;
        } else if (time > maxTime) {
            Component kickMessage = Component.text("You have been kicked for playing too long");
            player.kick(kickMessage);
        }

        playerTimes.put(playerId, time + 1);

        getLogger().info("Player " + player.getName() + " has been online for " + time + " seconds");
    }

    @Override
    public void onDisable() {
    }
}
