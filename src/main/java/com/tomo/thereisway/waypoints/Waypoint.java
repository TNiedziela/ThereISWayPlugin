package com.tomo.thereisway.waypoints;


import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.io.Serializable;
import java.util.*;

public abstract class Waypoint implements Serializable {

    protected String waypointName;
    protected Location placement;
    protected boolean crystalNameShow = false;

    protected Map<WaypointEffect, Boolean> effects = WaypointEffect.getDefaultEffectsMap();

    protected transient Map<WaypointEffect, Entity> relatedEntities = new HashMap<>();

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
        if (isEffectOn(effect)) {
            return;
        }
        effects.put(effect, true);
        if (effect.equals(WaypointEffect.ENDER_CRYSTAL)) {
            spawnEnderCrystal();
        }
    }

    public void turnEffectOff(WaypointEffect effect) {
        if (!isEffectOn(effect)) {
            return;
        }
        effects.put(effect, false);
        if (effect.equals(WaypointEffect.ENDER_CRYSTAL)) {
            despawnEnderCrystal();
        }
    }

    public boolean isEffectOn(WaypointEffect effect) {
        return effects.get(effect);
    }

    public void spawnEnderCrystal() {
        Location waypointPlacement = getPlacement();
        Location crystalLocation = waypointPlacement.clone().add(0, 4, 0);
        EnderCrystal crystal = (EnderCrystal) waypointPlacement.getWorld().spawnEntity(crystalLocation, EntityType.ENDER_CRYSTAL);
        crystal.setInvulnerable(true);
        crystal.setShowingBottom(false);
        crystal.setBeamTarget(waypointPlacement.clone().subtract(0, 5, 0));
        crystal.setGravity(false);
        crystal.customName(Component.text(waypointName));
        crystal.setCustomNameVisible(crystalNameShow);
        linkEnderCrystalToWaypoint(crystal);
    }

    private void linkEnderCrystalToWaypoint(Entity entity) {
        if (Objects.isNull(relatedEntities)) {
            relatedEntities = new HashMap<>(); //after server restart.
        }
        relatedEntities.put(WaypointEffect.ENDER_CRYSTAL, entity);
    }

    public void setCrystalNameVisible() {
        if (Objects.isNull(relatedEntities)) {
            crystalNameShow = true;
            return;
        }
        EnderCrystal crystal = (EnderCrystal) relatedEntities.getOrDefault(WaypointEffect.ENDER_CRYSTAL, null);
        if (Objects.nonNull(crystal)) {
            crystal.setCustomNameVisible(true);
            crystalNameShow = true;
        }
    }

    public void setCrystalNameNotVisible() {
        if (Objects.isNull(relatedEntities)) {
            crystalNameShow = false;
            return;
        }
        EnderCrystal crystal = (EnderCrystal) relatedEntities.getOrDefault(WaypointEffect.ENDER_CRYSTAL, null);
        if (Objects.nonNull(crystal)) {
            crystal.setCustomNameVisible(false);
            crystalNameShow = false;
        }
    }

    public boolean isCrystalNameVisible() {
        return crystalNameShow;
    }

    private void despawnEnderCrystal() {
        UUID entityID = relatedEntities.get(WaypointEffect.ENDER_CRYSTAL).getUniqueId();
        relatedEntities.get(WaypointEffect.ENDER_CRYSTAL).remove();
        relatedEntities.remove(WaypointEffect.ENDER_CRYSTAL);
        placement.getWorld().getEntities()
                .removeIf(entity -> entity.getUniqueId().equals(entityID));
    }

    public WaypointPOJO toWaypointPOJO() {
        return new WaypointPOJO(waypointName,
                placement.getWorld().getUID().toString(),
                placement.getBlockX(),
                placement.getBlockY(),
                placement.getBlockZ(),
                isEffectOn(WaypointEffect.ENDER_CRYSTAL),
                crystalNameShow,
                false,
                "");
    }
}
