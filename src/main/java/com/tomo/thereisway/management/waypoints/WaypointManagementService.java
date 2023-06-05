package com.tomo.thereisway.management.waypoints;

import com.tomo.thereisway.ThereISWay;
import com.tomo.thereisway.management.events.WaypointModifiedEvent;
import com.tomo.thereisway.waypoints.PlayerWaypoint;
import com.tomo.thereisway.waypoints.ServerWaypoint;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

public class WaypointManagementService {
    private final ThereISWay plugin;

    public WaypointManagementService(ThereISWay plugin) {
        this.plugin = plugin;
    }

    public PlayerWaypoint createPlayerWaypoint(Player player, String waypointName) {
        Location playerLocation = player.getLocation();
        PlayerWaypoint newPlayerWaypoint = PlayerWaypoint.createWaypoint(playerLocation, player, waypointName);
        player.sendMessage("Created new waypoint at: " + newPlayerWaypoint.getLocation());
        plugin.addPlayerWaypoint(newPlayerWaypoint);

        WaypointModifiedEvent event = new WaypointModifiedEvent(newPlayerWaypoint);
        event.callEvent();
        return newPlayerWaypoint;
    }

    public ServerWaypoint createServerWaypoint(Player player, String waypointName) {
        Location playerLocation = player.getLocation();
        ServerWaypoint newServerWaypoint = ServerWaypoint.createWaypoint(playerLocation, waypointName);
        player.sendMessage("Created new server waypoint at: " + newServerWaypoint.getLocation());
        WaypointModifiedEvent event = new WaypointModifiedEvent(newServerWaypoint);
        event.callEvent();
        return newServerWaypoint;
    }

    public Optional<PlayerWaypoint> getPlayerWaypointByNameIfExists(Player player, String waypointName) {
        List<PlayerWaypoint> waypointsWithSuchName = getWaypointsOwnedByPlayer(player).stream()
                .filter(waypoint -> waypoint.getWaypointName().equals(waypointName))
                .toList();
        if (waypointsWithSuchName.size() != 1) {
            return Optional.empty();
        }
        return Optional.of(waypointsWithSuchName.get(0));
    }

    public List<PlayerWaypoint> getWaypointsOwnedByPlayer(Player player) {
        return plugin.getPlayerWaypoints().stream()
                .filter(waypoint -> waypoint.isOwnedByPlayer(player))
                .toList();
    }

}
