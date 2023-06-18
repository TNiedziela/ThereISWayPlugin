package com.tomo.thereisway.management.waypoints;

import com.tomo.thereisway.ThereISWay;
import com.tomo.thereisway.management.events.WaypointModifiedEvent;
import com.tomo.thereisway.waypoints.PlayerWaypoint;
import com.tomo.thereisway.waypoints.ServerWaypoint;
import com.tomo.thereisway.waypoints.Waypoint;
import org.bukkit.Location;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

/**
 * Service provides tools to manage creating, deleting and editing waypoints.
 **/

public class WaypointManagementService {
    private final ThereISWay plugin;

    public WaypointManagementService(ThereISWay plugin) {
        this.plugin = plugin;
    }

    public void createPlayerWaypoint(Player player, String waypointName) {
        if (waypointName.isEmpty()) {
            player.sendMessage("Waypoint name not provided. aborting waypoint creation.");
            return;
        }
        Location playerLocation = player.getLocation();
        PlayerWaypoint newPlayerWaypoint = PlayerWaypoint.createWaypoint(playerLocation, player, waypointName);
        spawnEnderCrystalOnWaypoint(newPlayerWaypoint);
        player.sendMessage("Created new waypoint at: " + newPlayerWaypoint.getLocation());
        plugin.addPlayerWaypoint(newPlayerWaypoint);

        WaypointModifiedEvent event = new WaypointModifiedEvent(newPlayerWaypoint);
        event.callEvent();
    }

    public void deletePlayerWaypoint(Player player, String waypointName) {
        if (waypointName.isEmpty()) {
            player.sendMessage("Waypoint name not provided. aborting waypoint deletion.");
            return;
        }
        List<PlayerWaypoint> playerWaypoints = getWaypointsOwnedByPlayer(player);
        Optional<PlayerWaypoint> desiredWaypoint = playerWaypoints.stream().filter(wp -> wp.getWaypointName().equals(waypointName)).findFirst();
        if (desiredWaypoint.isEmpty()) {
            player.sendMessage("You don't own waypoint with such name.");
            return;
        }
        plugin.deletePlayerWaypoint(player, waypointName);
        WaypointModifiedEvent event = new WaypointModifiedEvent(desiredWaypoint.get());
        event.callEvent();
    }


    public void createServerWaypoint(Player player, String waypointName) {
        Location playerLocation = player.getLocation();
        ServerWaypoint newServerWaypoint = ServerWaypoint.createWaypoint(playerLocation, waypointName);
        player.sendMessage("Created new server waypoint at: " + newServerWaypoint.getLocation());
        plugin.addServerWaypoint(newServerWaypoint);

        WaypointModifiedEvent event = new WaypointModifiedEvent(newServerWaypoint);
        event.callEvent();
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

    public Optional<ServerWaypoint> getServerWaypointByNameIfExists(String waypointName) {
        List<ServerWaypoint> waypointsWithSuchName = plugin.getServerWaypoints().stream()
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

    private void spawnEnderCrystalOnWaypoint(Waypoint waypoint) {
        Location waypointPlacement = waypoint.getPlacement();
        EnderCrystal crystal = (EnderCrystal) waypointPlacement.getWorld().spawnEntity(waypointPlacement, EntityType.ENDER_CRYSTAL);
        crystal.getLocation().add(0,2,0);
        crystal.setShowingBottom(false);
        crystal.setBeamTarget(waypointPlacement);
    }

}
