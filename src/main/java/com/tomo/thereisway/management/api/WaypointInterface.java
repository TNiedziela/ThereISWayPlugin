package com.tomo.thereisway.management.api;

import org.bukkit.Location;

public interface WaypointInterface {

    void teleportTo();

    void showWaypoint();

    WaypointInterface createWaypoint(Location placement);
}
