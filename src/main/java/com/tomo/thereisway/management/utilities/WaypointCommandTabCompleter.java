package com.tomo.thereisway.management.utilities;

import com.tomo.thereisway.management.commands.WaypointCommandsService;
import com.tomo.thereisway.management.waypoints.WaypointManagementService;
import com.tomo.thereisway.waypoints.PlayerWaypoint;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WaypointCommandTabCompleter implements TabCompleter {

    private final WaypointManagementService waypointManagementService;

    public WaypointCommandTabCompleter(WaypointManagementService waypointManagementService) {
        this.waypointManagementService = waypointManagementService;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> commandParams = List.of(args);
        if (commandParams.size() == 1) {
            return getCommandsThatStartWith(commandParams.get(0));
        } else if (commandParams.size() == 2 && commandParams.get(0).equals(WaypointCommandsService.WaypointCommandType.MOVE.getCmd())) {
            return getWaypointsThatNameStartWith((Player) sender, commandParams.get(1));
        }
        return Collections.emptyList();
    }

    private List<String> getCommandsThatStartWith(String firstLetters) {
        List<String> commandsToComplete = new ArrayList<>();
        for (WaypointCommandsService.WaypointCommandType command : WaypointCommandsService.WaypointCommandType.values()) {
            if (command.getCmd().startsWith(firstLetters) && command.isProper()) {
                commandsToComplete.add(command.getCmd());
            }
        }
        return commandsToComplete;
    }

    private List<String> getWaypointsThatNameStartWith(Player player, String firstLetters) {
        List<String> names = new ArrayList<>();
        for (PlayerWaypoint waypoint : waypointManagementService.getWaypointsOwnedByPlayer(player)) {
            if (waypoint.getWaypointName().startsWith(firstLetters)) {
                names.add(waypoint.getWaypointName());
            }
        }
        return names;
    }
}
