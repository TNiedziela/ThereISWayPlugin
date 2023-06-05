package com.tomo.thereisway.waypoints;


import org.bukkit.Location;

import java.io.Serializable;

public abstract class Waypoint implements Serializable {

    Location placement;

    public Waypoint() {}

    public Location getPlacement() {
        return placement;
    }

    public String getLocation() {
        return "x = " + this.placement.getBlockX() + ", y = " + this.placement.getBlockY() + ", z = " + this.placement.getBlockZ();
    }

    protected abstract void saveWaypoint();
}
