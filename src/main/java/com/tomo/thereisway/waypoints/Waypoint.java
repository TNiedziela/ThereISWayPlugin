package com.tomo.thereisway.waypoints;


import org.bukkit.Location;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.io.Serializable;
import java.util.*;

public abstract class Waypoint implements Serializable {

    protected String waypointName;
    protected Location placement;
    protected Map<WaypointEffect, Boolean> effects = WaypointEffect.getDefaultEffectsMap();

    protected Map<WaypointEffect, Entity> relatedEntities = new HashMap<>();

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
        if (effect.equals(WaypointEffect.ENDER_CRYSTAL)) {
            if (isEffectOn(effect)) {
                return;
            }
            spawnEnderCrystal();
        }
    }

    public void turnEffectOff(WaypointEffect effect) {
        effects.put(effect, false);
        if (effect.equals(WaypointEffect.ENDER_CRYSTAL)) {
            despawnEnderCrystal();
        }
    }

    public boolean isEffectOn(WaypointEffect effect) {
        return effects.get(effect);
    }

    private void spawnEnderCrystal() {
        Location waypointPlacement = getPlacement();
        Location crystalLocation = waypointPlacement.clone().add(0, 4, 0);
        EnderCrystal crystal = (EnderCrystal) waypointPlacement.getWorld().spawnEntity(crystalLocation, EntityType.ENDER_CRYSTAL);
        crystal.setInvulnerable(true);
        crystal.setShowingBottom(false);
        crystal.setBeamTarget(waypointPlacement.clone().subtract(0, 5, 0));
        crystal.setGravity(false);
        linkEnderCrystalToWaypoint(crystal);
    }

    private void linkEnderCrystalToWaypoint(Entity entity) {
        relatedEntities.put(WaypointEffect.ENDER_CRYSTAL, entity);
    }

    private void despawnEnderCrystal() {
        UUID entityID = relatedEntities.get(WaypointEffect.ENDER_CRYSTAL).getUniqueId();
        relatedEntities.get(WaypointEffect.ENDER_CRYSTAL).remove();
        relatedEntities.remove(WaypointEffect.ENDER_CRYSTAL);
        placement.getWorld().getEntities()
                .removeIf(entity -> entity.getUniqueId().equals(entityID));
    }
}
