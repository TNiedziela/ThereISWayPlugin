package com.tomo.thereisway.waypoints;

import org.bukkit.Location;

/**
 * Server waypoints will be used mostly with items like pressure plates etc. in the future.
 */
public class ServerWaypoint extends Waypoint{


    private ServerWaypoint(Location waypointLocation, String waypointName) {
        super();
        super.waypointName = waypointName;
        super.placement = waypointLocation;
    }

    public static ServerWaypoint createWaypoint(Location waypointLocation, String waypointName) {
        return new ServerWaypoint(waypointLocation, waypointName);
    }

}
