package com.jewelexx.canjoin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import com.jewelexx.canjoin.commands.ManageCommands;

class CanJoinEvents implements Listener {
    CanJoin plugin;

    CanJoinEvents(CanJoin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();

        Integer time = plugin.playerTimes.getOrDefault(playerId.toString(), 0);

        if (time >= this.plugin.maxTime) {
            event.getPlayer().kickPlayer("You have been kicked for playing too long");
        }
    }
}

public final class CanJoin extends JavaPlugin {
    public ArrayList<String> ignoredPlayers;
    public HashMap<String, Integer> playerTimes = new HashMap<>();
    int maxTime;

    @Override
    public void onEnable() {
        resetPlayerTimes();

        try {
            File timesFile = new File("player-times.yml");
            FileInputStream fileInputStream = new FileInputStream(timesFile);
            Yaml yamlFile = new Yaml();

            HashMap<String, Integer> map = yamlFile.load(fileInputStream);

            if (getCurrentDate().equals(map.get("date"))) {
                playerTimes = map;
                // Bukkit.getLogger().info(map);
            }
        } catch (FileNotFoundException ignore) {
        }

        saveDefaultConfig();
        maxTime = getConfig().getInt("max-time");
        ignoredPlayers = new ArrayList<>((List<String>) getConfig().getList("ignored-players"));

        // Register events
        getServer().getPluginManager().registerEvents(new CanJoinEvents(this), this);

        getCommand("ignore").setExecutor(new ManageCommands(this));
        getCommand("reset").setExecutor(new ManageCommands(this));

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                // Reset the player times on a new day
                if (!getCurrentDate().equals(playerTimes.get("date"))) {
                    resetPlayerTimes();
                }

                Bukkit.getOnlinePlayers().forEach((player) -> updatePlayerTime(player));
            }
        }, 20L, 20L);
    }

    public void resetPlayerTimes() {
        playerTimes = new HashMap<>();
        playerTimes.put("date", getCurrentDate());
    }

    public void updateConfig() {
        getConfig().set("ignored-players", ignoredPlayers);
        saveConfig();
    }

    public void updatePlayerTime(Player player) {
        UUID playerId = player.getUniqueId();

        if (ignoredPlayers.contains(playerId.toString())) {
            return;
        }

        Integer time = playerTimes.get(playerId.toString());

        if (time == null) {
            time = 0;
        } else if (time > maxTime) {
            player.kickPlayer("You have been kicked for playing too long");
        }

        playerTimes.put(playerId.toString(), time + 1);

        getLogger().info("Player " + player.getName() + " has been online for " + time + " seconds");
    }

    public Integer getCurrentDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDateTime now = LocalDateTime.now();

        return Integer.parseInt(dtf.format(now));
    }

    public void dumpPlayerTimes() throws IOException {
        HashMap<String, Integer> playerTimes = this.playerTimes;
        playerTimes.put("date", getCurrentDate());

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
