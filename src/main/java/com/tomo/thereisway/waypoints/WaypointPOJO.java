package com.tomo.thereisway.waypoints;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;


public class WaypointPOJO {


    private final String waypointName;
    private final String worldUUID;
    private final double x;
    private final double y;
    private final double z;
    private final boolean endCrystalShow;
    private final boolean crystalNameShow;
    private final boolean isPlayerWaypoint;
    private final String playerUUID;

    public WaypointPOJO(String waypointName, String worldUUID, double x, double y, double z, boolean endCrystalShow, boolean crystalNameShow, boolean isPlayerWaypoint, String playerUUID) {
        this.waypointName = waypointName;
        this.worldUUID = worldUUID;
        this.x = x;
        this.y = y;
        this.z = z;
        this.endCrystalShow = endCrystalShow;
        this.crystalNameShow = crystalNameShow;
        this.isPlayerWaypoint = isPlayerWaypoint;
        this.playerUUID = playerUUID;
    }

    public Waypoint toWaypoint() {
        if (isPlayerWaypoint) {
            return createPlayerWaypoint(playerUUID);
        }
        return createServerWaypoint();
    }

    private PlayerWaypoint createPlayerWaypoint(String playerUUID) {
        World world = Bukkit.getServer().getWorld(UUID.fromString(worldUUID));
        Location location = new Location(world, x, y, z);
        PlayerWaypoint newPlayerWaypoint = PlayerWaypoint.createWaypoint(location, playerUUID, waypointName);
        if (crystalNameShow) {
            newPlayerWaypoint.turnEffectOn(WaypointEffect.NAME_HOLO);
        }
        if (endCrystalShow) {
            newPlayerWaypoint.turnEffectOn(WaypointEffect.ENDER_CRYSTAL);
        }
        return newPlayerWaypoint;
    }

    private ServerWaypoint createServerWaypoint() {
        World world = Bukkit.getServer().getWorld(UUID.fromString(worldUUID));
        Location location = new Location(world, x, y, z);
        ServerWaypoint newServerWaypoint = ServerWaypoint.createWaypoint(location, waypointName);
        if (crystalNameShow) {
            newServerWaypoint.turnEffectOff(WaypointEffect.NAME_HOLO);
        }
        if (endCrystalShow) {
            newServerWaypoint.turnEffectOn(WaypointEffect.ENDER_CRYSTAL);
        }
        return newServerWaypoint;
    }
}
