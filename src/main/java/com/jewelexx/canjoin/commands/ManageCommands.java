package com.jewelexx.canjoin.commands;

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
                sender.sendMessage("Please specify a player to ignore");
                return false;
            } else {
                sender.sendMessage("Ignoring " + args[0]);
            }

            for (String arg : args) {
                Player player = Bukkit.getPlayer(arg);
                sender.sendMessage(player.getUniqueId());
            }

            return true;
        } else if (label.equals("reset")) {
            return true;
        } else {
            return false;
        }
    }
}
