package com.tomo.thereisway.waypoints;

import org.bukkit.Location;

public class ServerWaypoint{

    private final Location waypointLocation;

    private ServerWaypoint(Location waypointLocation) {
        this.waypointLocation = waypointLocation;
    }

    public static ServerWaypoint createWaypoint(Location waypointLocation) {
        return new ServerWaypoint(waypointLocation);
    }

    public String getLocation() {
        return "x = " + this.waypointLocation.getBlockX() + ", y = " + this.waypointLocation.getBlockY() + ", z = " + this.waypointLocation.getBlockZ();
    }

    public void deleteWaypoint() {

    }
}
