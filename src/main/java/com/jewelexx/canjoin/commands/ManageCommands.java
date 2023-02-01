package com.jewelexx.canjoin.commands;

import java.util.HashMap;

import org.bukkit.Bukkit;
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
        sender.sendMessage(label);
        if (label.equals("ignore")) {
            if (args.length == 0) {
                sender.sendMessage("[CanJoin] Please specify a player to ignore");
                return false;
            }

            for (String arg : args) {
                Player player = Bukkit.getPlayer(arg);
                sender.sendMessage("[CanJoin] Ignoring " + player.getName());

                plugin.ignoredPlayers.add(player.getUniqueId().toString());
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
                sender.sendMessage("[CanJoin] Resetting " + player.getName());

                plugin.playerTimes.remove(player.getUniqueId().toString());
            }

            return true;
        } else {
            return false;
        }
    }
}
