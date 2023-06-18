package com.tomo.thereisway.waypoints;


import org.bukkit.Location;

import java.io.Serializable;
import java.util.Map;

public abstract class Waypoint implements Serializable {

    protected String waypointName;
    protected Location placement;

    protected Map<WaypointEffect, Boolean> effects = WaypointEffect.getDefaultEffectsMap();

    public Waypoint() {}

    public Location getPlacement() {
        return placement;
    }

    public String getWaypointName() {
        return waypointName;
    }

    public String getLocation() {
        return "x = " + this.placement.getBlockX() + ", y = " + this.placement.getBlockY() + ", z = " + this.placement.getBlockZ();
    }

    public void turnEffectOn(WaypointEffect effect) {
        effects.put(effect, true);
    }

    public void turnEffectOff(WaypointEffect effect) {
        effects.put(effect, false);
    }

    public boolean isEffectOn(WaypointEffect effect) {
        return effects.get(effect);
    }

}
