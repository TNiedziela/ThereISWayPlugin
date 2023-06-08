package com.tomo.thereisway;

import com.tomo.thereisway.management.commands.WaypointCommand;
import com.tomo.thereisway.management.listeners.WpListener;
import com.tomo.thereisway.management.waypoints.WaypointHolder;
import com.tomo.thereisway.waypoints.PlayerWaypoint;
import com.tomo.thereisway.waypoints.ServerWaypoint;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class ThereISWay extends JavaPlugin {

    private final String WAYPOINTS_FILE = "waypoints/wp_save.data";
    private final String WAYPOINTS_FILE_TEXT = "waypoints/wp_save.txt";

    private WaypointHolder waypointHolder;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new WpListener(this), this);
        waypointHolder = WaypointHolder.loadData(WAYPOINTS_FILE);
        new WaypointCommand(this);
        getLogger().info("There Is Way plugin has been enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("There Is Way plugin has been disabled");
    }

    public void saveWaypoints() {
        waypointHolder.saveData(WAYPOINTS_FILE);
        waypointHolder.saveDataToTextFile(WAYPOINTS_FILE_TEXT);
        System.out.println("Waypoint config saved");
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
