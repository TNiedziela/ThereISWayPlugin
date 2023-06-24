package com.tomo.thereisway.management.events;

import com.tomo.thereisway.waypoints.Waypoint;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class WaypointModifiedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    Waypoint waypoint;

    Reason reason;

    private WaypointModifiedEvent(Waypoint waypoint, Reason reason) {
        super();
        this.reason = reason;
        this.waypoint = waypoint;
    }

    public static WaypointModifiedEvent waypointCreatedEvent(Waypoint waypoint) {
        return new WaypointModifiedEvent(waypoint, Reason.CREATE);
    }

    public static WaypointModifiedEvent waypointDeletedEvent(Waypoint waypoint) {
        return new WaypointModifiedEvent(waypoint, Reason.REMOVE);
    }

    public static WaypointModifiedEvent waypointEffectTurnedOnEvent(Waypoint waypoint) {
        return new WaypointModifiedEvent(waypoint, Reason.EFFECT_TURNED_ON);
    }

    public static WaypointModifiedEvent waypointEffectTurnedOffEvent(Waypoint waypoint) {
        return new WaypointModifiedEvent(waypoint, Reason.EFFECT_TURNED_OFF);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public enum Reason {
        CREATE,
        REMOVE,
        EFFECT_TURNED_ON,
        EFFECT_TURNED_OFF


    }

}
