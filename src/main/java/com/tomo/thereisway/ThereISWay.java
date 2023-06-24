package com.tomo.thereisway;

import com.tomo.thereisway.management.commands.WaypointCommandsService;
import com.tomo.thereisway.management.listeners.WpListener;
import com.tomo.thereisway.management.waypoints.WaypointHolder;
import com.tomo.thereisway.waypoints.PlayerWaypoint;
import com.tomo.thereisway.waypoints.ServerWaypoint;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Logger;

public final class ThereISWay extends JavaPlugin {

    private final String WAYPOINTS_FILE = "waypoints/wp_save.data";

    private WaypointHolder waypointHolder;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new WpListener(this), this);
        try {
            waypointHolder = WaypointHolder.loadData(WAYPOINTS_FILE);
        } catch (RuntimeException exception) {
            getLogger().warning("There was a problem while loading waypoint data\n" + exception.getMessage());
            waypointHolder = new WaypointHolder();
        }
        new WaypointCommandsService(this);
        getLogger().info("There Is Way plugin has been enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("There Is Way plugin has been disabled");
    }

    public void saveWaypoints() {
        waypointHolder.saveData(WAYPOINTS_FILE);
        getLogger().info("Waypoint config saved");
    }

    public void addPlayerWaypoint(PlayerWaypoint playerWaypoint) {
        waypointHolder.addPlayerWaypoint(playerWaypoint);
    }

    public void addServerWaypoint(ServerWaypoint serverWaypoint) {
        waypointHolder.addServerWaypoint(serverWaypoint);
    }

    public void deletePlayerWaypoint(Player player, String waypointName) {
        waypointHolder.deletePlayerWaypoint(player, waypointName);
    }

    public List<PlayerWaypoint> getPlayerWaypoints() {
        return waypointHolder.getPlayerWaypoints();
    }

    public List<ServerWaypoint> getServerWaypoints() {
        return waypointHolder.getServerWaypoints();
    }
}
