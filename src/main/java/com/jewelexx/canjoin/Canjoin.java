package com.jewelexx.canjoin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import net.kyori.adventure.text.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

        Integer time = plugin.playerTimes.get(playerId.toString());

        if (time >= this.plugin.maxTime) {
            Component kickMessage = Component.text("You have been kicked for playing too long");
            event.getPlayer().kick(kickMessage);
        }
    }
}

public final class Canjoin extends JavaPlugin {
    int maxTime;
    HashMap<String, Integer> playerTimes = new HashMap<String, Integer>();

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
        Integer time = playerTimes.get(playerId.toString());

        if (time == null) {
            time = 0;
        } else if (time > maxTime) {
            Component kickMessage = Component.text("You have been kicked for playing too long");
            player.kick(kickMessage);
        }

        playerTimes.put(playerId.toString(), time + 1);

        getLogger().info("Player " + player.getName() + " has been online for " + time + " seconds");
    }

    public void dumpPlayerTimes() throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDateTime now = LocalDateTime.now();

        HashMap<String, Integer> playerTimes = this.playerTimes;
        playerTimes.put("date", Integer.parseInt(dtf.format(now)));

        Yaml yamlFile = new Yaml();
        FileWriter file = new FileWriter("player-times.yml", Charset.defaultCharset());

        yamlFile.dump(playerTimes, file);
    }

    @Override
    public void onDisable() {
        try {
            dumpPlayerTimes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
