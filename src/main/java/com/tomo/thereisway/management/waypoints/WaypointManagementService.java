package com.tomo.thereisway.management.waypoints;

import com.tomo.thereisway.ThereISWay;
import com.tomo.thereisway.management.events.WaypointModifiedEvent;
import com.tomo.thereisway.waypoints.PlayerWaypoint;
import com.tomo.thereisway.waypoints.ServerWaypoint;
import com.tomo.thereisway.waypoints.Waypoint;
import com.tomo.thereisway.waypoints.WaypointEffect;
import org.bukkit.Location;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Service provides tools to manage creating, deleting and editing waypoints.
 **/

public class WaypointManagementService {
    private final ThereISWay plugin;

    private final Logger logger;

    public WaypointManagementService(ThereISWay plugin) {
        this.plugin = plugin;
        this.logger = this.plugin.getLogger();
    }

    public void createPlayerWaypoint(Player player, String waypointName) {
        if (waypointName.isEmpty()) {
            player.sendMessage("Waypoint name not provided. aborting waypoint creation.");
            return;
        }
        Location playerLocation = player.getLocation();
        PlayerWaypoint newPlayerWaypoint = PlayerWaypoint.createWaypoint(playerLocation, player, waypointName);
        player.sendMessage("Created new waypoint at: " + newPlayerWaypoint.getLocation());
        plugin.addPlayerWaypoint(newPlayerWaypoint);

        WaypointModifiedEvent event = WaypointModifiedEvent.waypointCreatedEvent(newPlayerWaypoint);
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
        WaypointModifiedEvent event = WaypointModifiedEvent.waypointDeletedEvent(desiredWaypoint.get());
        event.callEvent();
    }


    public void createServerWaypoint(Player player, String waypointName) {
        Location playerLocation = player.getLocation();
        ServerWaypoint newServerWaypoint = ServerWaypoint.createWaypoint(playerLocation, waypointName);
        player.sendMessage("Created new server waypoint at: " + newServerWaypoint.getLocation());
        plugin.addServerWaypoint(newServerWaypoint);

        WaypointModifiedEvent event = WaypointModifiedEvent.waypointCreatedEvent(newServerWaypoint);
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

    public void spawnEnderCrystalOnWaypoint(Waypoint waypoint) {
        waypoint.turnEffectOn(WaypointEffect.ENDER_CRYSTAL);
        WaypointModifiedEvent event = WaypointModifiedEvent.waypointEffectTurnedOnEvent(waypoint);
        event.callEvent();
        logger.info("Spawned ender crystal entity on waypoint " + waypoint.getWaypointName());
    }

    public void despawnEnderCrystalFromWaypoint(Waypoint waypoint) {
        waypoint.turnEffectOff(WaypointEffect.ENDER_CRYSTAL);
        WaypointModifiedEvent event = WaypointModifiedEvent.waypointEffectTurnedOffEvent(waypoint);
        event.callEvent();
        logger.info("Spawned ender crystal entity on waypoint " + waypoint.getWaypointName());
    }

}
