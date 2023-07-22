package com.tomo.thereisway;

import com.tomo.thereisway.management.commands.WaypointCommandsService;
import com.tomo.thereisway.management.listeners.WpListener;
import com.tomo.thereisway.management.waypoints.WaypointHolder;
import com.tomo.thereisway.waypoints.PlayerWaypoint;
import com.tomo.thereisway.waypoints.ServerWaypoint;
import com.tomo.thereisway.waypoints.Waypoint;
import com.tomo.thereisway.waypoints.WaypointEffect;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class ThereISWay extends JavaPlugin {

    private final String WAYPOINTS_FILE_JSON = "waypoints/wp_save.json";

    private WaypointHolder waypointHolder;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new WpListener(this), this);
        try {
            Path waypointsPath = Paths.get("waypoints");
            Files.createDirectories(waypointsPath);

            waypointHolder = WaypointHolder.loadDataFromJson(WAYPOINTS_FILE_JSON);
        } catch (RuntimeException exception) {
            getLogger().warning("There was a problem while loading waypoint data\n" + exception.getMessage());
            exception.printStackTrace();
            waypointHolder = new WaypointHolder();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        new WaypointCommandsService(this);
        getLogger().info("There Is Way plugin has been enabled");
    }

    @Override
    public void onDisable() {
        despawnWaypointRelatedEntitesOnClose();
        getLogger().info("There Is Way plugin has been disabled");
    }

    public void saveWaypoints() {
        try {
            waypointHolder.saveDataToJson(WAYPOINTS_FILE_JSON);
            getLogger().info("Waypoint config saved");
        } catch (RuntimeException e) {
            getLogger().warning("Couldn't save waypoint config, an exception occurred: " + e.getMessage());
        }
    }

    public void addPlayerWaypoint(PlayerWaypoint playerWaypoint) {
        waypointHolder.addPlayerWaypoint(playerWaypoint);
    }

    public void addServerWaypoint(ServerWaypoint serverWaypoint) {
        waypointHolder.addServerWaypoint(serverWaypoint);
    }

    public void deleteWaypoint(Waypoint waypoint) {
        waypointHolder.deleteWaypoint(waypoint);
    }

    public List<PlayerWaypoint> getPlayerWaypoints() {
        return waypointHolder.getPlayerWaypoints();
    }

    public List<ServerWaypoint> getServerWaypoints() {
        return waypointHolder.getServerWaypoints();
    }

    private void despawnWaypointRelatedEntitesOnClose() {
        List<Waypoint> waypoints = new ArrayList<>(getPlayerWaypoints());
        waypoints.addAll(getServerWaypoints());
        for (Waypoint waypoint : waypoints) {
            getLogger().info("Waypoint: " + waypoint.getWaypointName() + " removal: ");
            getLogger().info("Entities to remove: " + waypoint.getRelatedEntities());
            if (waypoint.isEffectOn(WaypointEffect.ENDER_CRYSTAL)) {
                waypoint.despawnEnderCrystal();
            }
            if (waypoint.isEffectOn(WaypointEffect.NAME_HOLO) && waypoint.isAtLeastOneEffectOn()) {
                waypoint.removeNameHolo();
            }
        }
    }
}
