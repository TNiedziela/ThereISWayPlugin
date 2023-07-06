package com.tomo.thereisway.management.events;

import com.tomo.thereisway.waypoints.Waypoint;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class WaypointRelatedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player trigger;
    Waypoint waypoint;
    Reason reason;

    private WaypointRelatedEvent(Waypoint waypoint, Reason reason, Player player) {
        super();
        this.reason = reason;
        this.waypoint = waypoint;
        this.trigger = player;
    }

    public static WaypointRelatedEvent waypointCreatedEvent(Waypoint waypoint, Player player) {
        return new WaypointRelatedEvent(waypoint, Reason.CREATE, player);
    }

    public static WaypointRelatedEvent waypointDeletedEvent(Waypoint waypoint, Player player) {
        return new WaypointRelatedEvent(waypoint, Reason.REMOVE, player);
    }

    public static WaypointRelatedEvent waypointOpenServiceEvent(Player player) {
        return new WaypointRelatedEvent(null, Reason.OPEN_SERVICE, player);
    }

    public static WaypointRelatedEvent waypointOpenEditEvent(Waypoint waypoint, Player player) {
        return new WaypointRelatedEvent(waypoint, Reason.OPEN_EDIT, player);
    }

    public static WaypointRelatedEvent waypointSaveEditEvent(Waypoint waypoint, Player player) {
        return new WaypointRelatedEvent(waypoint, Reason.SAVE_EDIT, player);
    }

    public Waypoint getWaypoint() {
        return waypoint;
    }

    public Player getTrigger() {
        return trigger;
    }

    public Reason getReason() {
        return reason;
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
        OPEN_SERVICE,
        OPEN_EDIT,
        SAVE_EDIT

    }

}
