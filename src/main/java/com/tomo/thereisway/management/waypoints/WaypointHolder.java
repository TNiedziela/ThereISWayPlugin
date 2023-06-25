package com.tomo.thereisway.management.waypoints;


import com.tomo.thereisway.waypoints.PlayerWaypoint;
import com.tomo.thereisway.waypoints.ServerWaypoint;
import com.tomo.thereisway.waypoints.Waypoint;
import com.tomo.thereisway.waypoints.WaypointEffect;
import org.bukkit.entity.Player;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class WaypointHolder implements Serializable {

    private final List<PlayerWaypoint> playerWaypoints = new ArrayList<>();
    private final List<ServerWaypoint> serverWaypoints = new ArrayList<>();

    public WaypointHolder() {}

    public void saveData(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(filePath)));
            out.writeObject(this);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static WaypointHolder loadData(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return new WaypointHolder();
            }
            BukkitObjectInputStream in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(filePath)));
            WaypointHolder waypointHolder = (WaypointHolder) in.readObject();
            in.close();
            for (Waypoint waypoint : waypointHolder.playerWaypoints) {
                if (waypoint.isEffectOn(WaypointEffect.ENDER_CRYSTAL)) {
                    waypoint.spawnEnderCrystal();
                }
            }
            for (Waypoint waypoint : waypointHolder.serverWaypoints) {
                if (waypoint.isEffectOn(WaypointEffect.ENDER_CRYSTAL)) {
                    waypoint.spawnEnderCrystal();
                }
            }
            return waypointHolder;
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void addPlayerWaypoint(PlayerWaypoint playerWaypoint) {
        playerWaypoints.add(playerWaypoint);
    }

    public void addServerWaypoint(ServerWaypoint serverWaypoint) {
        serverWaypoints.add(serverWaypoint);
    }

    public void deletePlayerWaypoint(Player player, String waypointName) {
        for (int i = 0; i < playerWaypoints.size(); i++) {
            if (playerWaypoints.get(i).isOwnedByPlayer(player) && playerWaypoints.get(i).getWaypointName().equals(waypointName)) {
                playerWaypoints.remove(i);
                break;
            }
        }
    }

    public List<PlayerWaypoint> getPlayerWaypoints() {
        return playerWaypoints;
    }

    public List<ServerWaypoint> getServerWaypoints() {
        return serverWaypoints;
    }
}
