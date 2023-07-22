package com.tomo.thereisway.waypoints;


import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.io.Serializable;
import java.util.*;

public abstract class Waypoint implements Serializable {

    protected String waypointName;
    protected Location placement;

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

    public Map<WaypointEffect, Entity> getRelatedEntities() {
        return relatedEntities;
    }

    public void removeRelatedEntitiesOnRemoval() {
        if (relatedEntities.containsKey(WaypointEffect.ENDER_CRYSTAL)) {
            despawnEnderCrystal();
        }
        if (relatedEntities.containsKey(WaypointEffect.NAME_HOLO)) {
            removeNameHolo();
        }
    }

    public void turnEffectOn(WaypointEffect effect) {
        if (isEffectOn(effect)) {
            return;
        }
        effects.put(effect, true);
        if (effect.equals(WaypointEffect.ENDER_CRYSTAL)) {
            if (isEffectOn(WaypointEffect.NAME_HOLO)) {
                spawnNameHoloCrystal();
            }
            spawnEnderCrystal();
        }

        if (effect.equals(WaypointEffect.NAME_HOLO)) {
            if (isAtLeastOneEffectOn()) {
                spawnNameHoloCrystal();
            }
        }
    }

    public void turnEffectOff(WaypointEffect effect) {
        if (!isEffectOn(effect)) {
            return;
        }
        effects.put(effect, false);
        if (effect.equals(WaypointEffect.ENDER_CRYSTAL)) {
            despawnEnderCrystal();
            if (isEffectOn(WaypointEffect.NAME_HOLO)) {
                removeNameHolo();
            }
        }

        if (effect.equals(WaypointEffect.NAME_HOLO)) {
            if (isAtLeastOneEffectOn()) {
                removeNameHolo();
            }
        }
    }

    public boolean isEffectOn(WaypointEffect effect) {
        return effects.get(effect);
    }

    public boolean isAtLeastOneEffectOn() {
        for (Map.Entry<WaypointEffect, Boolean> entry : effects.entrySet()) {
            if (!entry.getKey().equals(WaypointEffect.NAME_HOLO)) {
                if (entry.getValue().equals(Boolean.TRUE)) {
                    return true;
                }
            }
        }
        return false;
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
        crystal.setCustomNameVisible(isEffectOn(WaypointEffect.NAME_HOLO));
        linkEnderCrystalToWaypoint(crystal);
    }

    private void linkEnderCrystalToWaypoint(Entity entity) {
        if (Objects.isNull(relatedEntities)) {
            relatedEntities = new HashMap<>(); //after server restart.
        }
        relatedEntities.put(WaypointEffect.ENDER_CRYSTAL, entity);
    }

    public void spawnNameHoloCrystal() {
        Location waypointPlacement = getPlacement();
        Location holoLocation = waypointPlacement.clone().add(0, 4, 0);
        ArmorStand nameHolo = (ArmorStand) waypointPlacement.getWorld().spawnEntity(holoLocation, EntityType.ARMOR_STAND);
        nameHolo.setInvulnerable(true);
        nameHolo.setGravity(false);
        nameHolo.setInvisible(true);
        nameHolo.customName(Component.text(waypointName));
        nameHolo.setCustomNameVisible(true);
        linkNameHoloToWaypoint(nameHolo);
    }

    private void linkNameHoloToWaypoint(Entity entity) {
        if (Objects.isNull(relatedEntities)) {
            relatedEntities = new HashMap<>(); //after server restart.
        }
        relatedEntities.put(WaypointEffect.NAME_HOLO, entity);
    }

    public void despawnEnderCrystal() {
        UUID entityID = relatedEntities.get(WaypointEffect.ENDER_CRYSTAL).getUniqueId();
        relatedEntities.get(WaypointEffect.ENDER_CRYSTAL).remove();
        relatedEntities.remove(WaypointEffect.ENDER_CRYSTAL);
        placement.getWorld().getEntities()
                .removeIf(entity -> entity.getUniqueId().equals(entityID));
    }

    public void removeNameHolo() {
        UUID entityID = relatedEntities.get(WaypointEffect.NAME_HOLO).getUniqueId();
        relatedEntities.get(WaypointEffect.NAME_HOLO).remove();
        relatedEntities.remove(WaypointEffect.NAME_HOLO);
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
                isEffectOn(WaypointEffect.NAME_HOLO),
                false,
                "");
    }
}
