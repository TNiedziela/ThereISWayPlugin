package com.tomo.thereisway.waypoints;

import org.bukkit.Location;

import java.util.Optional;

public class ServerWaypoint extends Waypoint{

    private String waypointName;

    private ServerWaypoint(Location waypointLocation, String waypointName) {
        this.waypointName = waypointName;
        super.placement = waypointLocation;
    }

    public static ServerWaypoint createWaypoint(Location waypointLocation, String waypointName) {
        return new ServerWaypoint(waypointLocation, waypointName);
    }

    @Override
    public void saveWaypoint() {
        return;
    }

    public void deleteWaypoint() {

    }
}
