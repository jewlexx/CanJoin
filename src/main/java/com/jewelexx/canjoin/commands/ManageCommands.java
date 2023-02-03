package com.jewelexx.canjoin.commands;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.jewelexx.canjoin.CanJoin;

public class ManageCommands implements CommandExecutor {
    CanJoin plugin;

    public ManageCommands(CanJoin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (label.equals("ignore")) {
            if (args.length == 0) {
                sender.sendMessage("[CanJoin] Please specify a player to ignore");
                return false;
            }

            for (String arg : args) {
                Player player = Bukkit.getPlayer(arg);
                String uid = "";

                if (player == null) {
                    // We must use the deprecated method here because we need to get the UUID from
                    // an offline player
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(arg);

                    if (offlinePlayer != null) {
                        uid = offlinePlayer.getUniqueId().toString();
                    } else {
                        sender.sendMessage("[CanJoin] Player " + arg + " not found");
                        return true;
                    }
                } else {
                    uid = player.getUniqueId().toString();
                }

                if (plugin.ignoredPlayers.contains(uid)) {
                    sender.sendMessage("[CanJoin] Un-ignoring " + arg);
                    plugin.ignoredPlayers.remove(uid);
                } else {
                    sender.sendMessage("[CanJoin] Ignoring " + arg);
                    plugin.ignoredPlayers.add(uid);
                }
                plugin.updateConfig();
            }

            return true;
        } else if (label.equals("reset")) {
            if (args.length == 0) {
                sender.sendMessage("[CanJoin] Resetting all player times");
                plugin.playerTimes = new HashMap<>();
            }

            for (String arg : args) {
                Player player = Bukkit.getPlayer(arg);
                String uid = "";

                if (player == null) {
                    // We must use the deprecated method here because we need to get the UUID from
                    // an offline player
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(arg);

                    if (offlinePlayer != null) {
                        uid = offlinePlayer.getUniqueId().toString();
                    } else {
                        sender.sendMessage("[CanJoin] Player " + arg + " not found");
                        return true;
                    }
                } else {
                    uid = player.getUniqueId().toString();
                }

                sender.sendMessage("[CanJoin] Resetting " + arg);

                plugin.playerTimes.remove(uid);
            }

            return true;
        } else {
            return false;
        }
    }
}
