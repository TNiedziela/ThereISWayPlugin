package com.tomo.thereisway.management.commands;

import com.tomo.thereisway.ThereISWay;
import com.tomo.thereisway.management.waypoints.WaypointManagementService;
import com.tomo.thereisway.waypoints.PlayerWaypoint;
import com.tomo.thereisway.waypoints.ServerWaypoint;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class WaypointCommand implements CommandExecutor {

    private final ThereISWay plugin;

    private WaypointManagementService waypointManagementService;

    public WaypointCommand(ThereISWay plugin) {
        this.plugin = plugin;
        this.waypointManagementService = new WaypointManagementService(plugin);
        Objects.requireNonNull(plugin.getCommand("waypoint")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> commandArgs = Arrays.asList(args);
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Waypoint creation is possible only by players!");
            return true;
        }
        Player player = (Player) commandSender;
        if (!player.isOp()) {
            player.sendMessage("You are not permitted to create waypoints!");
            return true;
        } else {
            String actualCommand = commandArgs.get(0);
            String waypointName = commandArgs.size() == 2 ? commandArgs.get(1) : "";
            if (!isSupportedCommand(actualCommand)) {
                player.sendMessage("Wrong command parameters provided");
                return true;
            }
            switch (actualCommand) {
                case "create" -> {
                    if (waypointName.isEmpty()) {
                        player.sendMessage("Waypoint name not provided. aborting waypoint creation.");
                        return true;
                    }
                    waypointManagementService.createPlayerWaypoint(player, waypointName);
                }
                case "delete" -> {
                    if (waypointName.isEmpty()) {
                        player.sendMessage("Waypoint name not provided. aborting waypoint deletion.");
                        return true;
                    }
                    deletePlayerWaypoint(player, waypointName);
                }
                case "move" -> {
                    if (waypointName.isEmpty()) {
                        player.sendMessage("You must provide name of the waypoint that you want to teleport to.");
                        return true;
                    }
                    movePlayerIfPossible(player, waypointName);
                }
                case "show" -> showPlayerHisWaypoints(player);

                case "showAll" -> showAllWaypoints(player);
            }
        }
        return false;
    }

    private void movePlayerIfPossible(Player player, String waypointName) {
        Optional<PlayerWaypoint> playerWaypoint = waypointManagementService.getPlayerWaypointByNameIfExists(player, waypointName);
        if (playerWaypoint.isEmpty()) {
            player.sendMessage("You don't have waypoint with such name (" + waypointName + ")");
        } else {
            PlayerWaypoint waypoint = playerWaypoint.get();
            player.teleport(waypoint.getPlacement());
            player.sendMessage("Successfully moved to: " + waypointName);
        }
    }

    private boolean isSupportedCommand(String command) {
        return command.equals("create")
                || command.equals("delete")
                || command.equals("show")
                || command.equals("move")
                || command.equals("showAll");
    }

    private void deletePlayerWaypoint(Player player, String waypointName) { //todo implement
        return;
    }

    private void createServerWaypoint(Player player, String waypointName) {
        ServerWaypoint serverWaypoint = waypointManagementService.createServerWaypoint(player, waypointName);
        plugin.addServerWaypoint(serverWaypoint);
    }

    private void showPlayerHisWaypoints(Player player) {
        List<PlayerWaypoint> ownedByPlayerWaypoints = waypointManagementService.getWaypointsOwnedByPlayer(player);
        if (ownedByPlayerWaypoints.isEmpty()) {
            player.sendMessage("You don't have any waypoints.");
            return;
        }
        player.sendMessage("List of owned waypoints: ");
        for (PlayerWaypoint waypoint : ownedByPlayerWaypoints) {
            player.sendMessage(waypoint.toString());
        }
    }

    private void showAllWaypoints(Player player) {
        List<PlayerWaypoint> waypoints = plugin.getPlayerWaypoints();
        player.sendMessage("List of all waypoints: ");
        for (PlayerWaypoint waypoint : waypoints) {
            player.sendMessage(waypoint.toString());
        }
    }
}
