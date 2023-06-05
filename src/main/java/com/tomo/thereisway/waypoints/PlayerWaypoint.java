package com.tomo.thereisway.waypoints;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class PlayerWaypoint extends Waypoint{

    private String waypointName;
    private final UUID ownerID;

    public PlayerWaypoint(Location placement, Player owner, String waypointName) {
        this.waypointName = waypointName;
        super.placement = placement;
        this.ownerID = owner.getPlayerProfile().getId();
    }

    public static PlayerWaypoint createWaypoint(Location waypointLocation,Player player, String waypointName) {
        return new PlayerWaypoint(waypointLocation, player, waypointName);
    }

    public boolean isOwnedByPlayer(Player player) {
        return Objects.equals(player.getPlayerProfile().getId(), ownerID);
    }

    public String getWaypointName() {
        return waypointName;
    }

    @Override
    public void saveWaypoint() {

    }

    @Override
    public String toString() {
        String result = "Waypoint: " + waypointName + ", ";
        result = result + "Location = " + getLocation();
        return result;
    }
}
