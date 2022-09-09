package com.tomo.thereisway.commands;

import com.tomo.thereisway.ThereISWay;
import com.tomo.thereisway.waypoints.ServerWaypoint;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CreateWaypointCommand implements CommandExecutor {

    @SuppressWarnings("unused")
    private ThereISWay plugin;

    public CreateWaypointCommand(ThereISWay plugin) {
        this.plugin = plugin;
        Objects.requireNonNull(plugin.getCommand("createWaypoint")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Waypoint creation is possible only by players!");
            return true;
        }
        Player player = (Player) commandSender;
        if (!player.isOp()) {
            player.sendMessage("You are not permitted to create waypoints!");
            return true;
        } else {
            ServerWaypoint createdWaypoint = ServerWaypoint.createWaypoint(player.getLocation());
            player.sendMessage("New server waypoint was created!");
        }
        return false;
    }
}
