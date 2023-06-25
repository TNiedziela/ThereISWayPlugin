package com.tomo.thereisway.waypoints;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;


public class WaypointPOJO {


    private String waypointName;
    private String worldUUID;
    private double x;
    private double y;
    private double z;
    private boolean endCrystalShow = false;
    private boolean crystalNameShow = false;
    private boolean isPlayerWaypoint = true;
    private String playerUUID = "";

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
            return createPlayerWaypoint(Bukkit.getServer().getPlayer(UUID.fromString(playerUUID)));
        }
        return createPlayerWaypoint();
    }

    private PlayerWaypoint createPlayerWaypoint(Player player) {
        World world = Bukkit.getServer().getWorld(UUID.fromString(worldUUID));
        Location location = new Location(world, x, y, z);
        PlayerWaypoint newPlayerWaypoint = PlayerWaypoint.createWaypoint(location, player, waypointName);
        if (crystalNameShow) {
            newPlayerWaypoint.setCrystalNameVisible();
        }
        if (endCrystalShow) {
            newPlayerWaypoint.turnEffectOn(WaypointEffect.ENDER_CRYSTAL);
        }
        return newPlayerWaypoint;
    }

    private ServerWaypoint createPlayerWaypoint() {
        World world = Bukkit.getServer().getWorld(UUID.fromString(worldUUID));
        Location location = new Location(world, x, y, z);
        ServerWaypoint newServerWaypoint = ServerWaypoint.createWaypoint(location, waypointName);
        if (crystalNameShow) {
            newServerWaypoint.setCrystalNameVisible();
        }
        if (endCrystalShow) {
            newServerWaypoint.turnEffectOn(WaypointEffect.ENDER_CRYSTAL);
        }
        return newServerWaypoint;
    }
}
