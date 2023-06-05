package com.tomo.thereisway.management.events;

import com.tomo.thereisway.waypoints.Waypoint;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class WaypointModifiedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    Waypoint waypoint;

    public WaypointModifiedEvent(Waypoint waypoint) {
        super();
        this.waypoint = waypoint;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
