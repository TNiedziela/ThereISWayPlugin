package com.tomo.thereisway.management.commands;

import com.tomo.thereisway.ThereISWay;
import com.tomo.thereisway.management.utilities.ChatUtils;
import com.tomo.thereisway.management.waypoints.WaypointManagementService;
import com.tomo.thereisway.waypoints.PlayerWaypoint;
import com.tomo.thereisway.waypoints.ServerWaypoint;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class WaypointCommand implements CommandExecutor {
    private final ThereISWay plugin;

    private final WaypointManagementService waypointManagementService;

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
            WaypointCommandType actualCommand = WaypointCommandType.get(commandArgs.get(0)).orElse(WaypointCommandType.WRONG);
            String waypointName = commandArgs.size() == 2 ? commandArgs.get(1) : "";
            Map<WaypointCommandType, Runnable> commands = getCommands(player, waypointName);
            commands.get(actualCommand).run();
        }
        return false;
    }

    private Map<WaypointCommandType, Runnable> getCommands(Player player, String waypointName) {
        Map<WaypointCommandType, Runnable> commands = new HashMap<>();
        commands.put(WaypointCommandType.CREATE, () -> waypointManagementService.createPlayerWaypoint(player, waypointName));
        commands.put(WaypointCommandType.DELETE, () -> deletePlayerWaypoint(player, waypointName));
        commands.put(WaypointCommandType.MOVE, () -> movePlayerIfPossible(player, waypointName));
        commands.put(WaypointCommandType.SHOW, () -> showPlayerHisWaypoints(player));
        commands.put(WaypointCommandType.SHOW_ALL, () -> showAllWaypoints(player));
        commands.put(WaypointCommandType.WRONG, () -> wrongCommandProvidedMessage(player));
        return commands;
    }

    private void movePlayerIfPossible(Player player, String waypointName) {
        if (waypointName.isEmpty()) {
            player.sendMessage("You must provide name of the waypoint that you want to teleport to.");
            return;
        }
        Optional<PlayerWaypoint> playerWaypoint = waypointManagementService.getPlayerWaypointByNameIfExists(player, waypointName);
        if (playerWaypoint.isEmpty()) {
            player.sendMessage("You don't have waypoint with such name (" + waypointName + ")");
        } else {
            PlayerWaypoint waypoint = playerWaypoint.get();
            player.teleport(waypoint.getPlacement());
            player.sendMessage(ChatUtils.asGreenMessage("Successfully moved to: " + waypointName));
        }
    }

    private void deletePlayerWaypoint(Player player, String waypointName) { //todo implement
        if (waypointName.isEmpty()) {
            player.sendMessage("Waypoint name not provided. aborting waypoint deletion.");
        }
        return;
    }

    private void wrongCommandProvidedMessage(Player player) {
        player.sendMessage(ChatUtils.asRedMessage("Wrong command provided!\n"));
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
            player.sendMessage(waypoint.asClickableColoredMessage());
        }
    }

    private void showAllWaypoints(Player player) {
        List<PlayerWaypoint> waypoints = plugin.getPlayerWaypoints();
        player.sendMessage("List of all waypoints: ");
        for (PlayerWaypoint waypoint : waypoints) {
            player.sendMessage(waypoint.toString());
        }
    }

    public enum WaypointCommandType {
        CREATE("create"),
        DELETE("delete"),
        MOVE("move"),
        SHOW("show"),
        SHOW_ALL("showAll"),
        WRONG("wrongCommand");

        private String cmd;

        WaypointCommandType(String cmd) {
            this.cmd = cmd;
        }

        public String getCmd() {
            return cmd;
        }

        public static Optional<WaypointCommandType> get(String cmdProvided) {
            return Arrays.stream(WaypointCommandType.values())
                    .filter(val -> val.cmd.equals(cmdProvided))
                    .findFirst();
        }
    }
}
