package com.tomo.thereisway.management.waypoints;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tomo.thereisway.waypoints.*;
import org.bukkit.entity.Player;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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

    public void saveDataToJson(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            Writer writer = new FileWriter(file);

            List<WaypointPOJO> waypointPOJO = playerWaypoints.stream().map(PlayerWaypoint::toWaypointPOJO).collect(Collectors.toList());
            waypointPOJO.addAll(serverWaypoints.stream().map(ServerWaypoint::toWaypointPOJO).toList());

            WaypointPOJO[] waypointsArr = waypointPOJO.toArray(WaypointPOJO[]::new);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(waypointsArr, writer);

            writer.flush();
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        public static WaypointHolder loadDataFromJson(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return new WaypointHolder();
            }
            Reader reader = new FileReader(file);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            List<WaypointPOJO> waypoints = Arrays.stream(gson.fromJson(reader, WaypointPOJO[].class)).toList();

            WaypointHolder waypointHolder = new WaypointHolder();
            waypoints.forEach(waypoint -> waypointHolder.addWaypoint(waypoint.toWaypoint()));

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
            reader.close();
            return waypointHolder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addWaypoint(Waypoint waypoint) {
        if (waypoint instanceof PlayerWaypoint) {
            playerWaypoints.add((PlayerWaypoint) waypoint);
        }
        if (waypoint instanceof ServerWaypoint) {
            serverWaypoints.add((ServerWaypoint) waypoint);
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

    public void deleteServerWaypoint(String waypointName) {
        for (int i = 0; i < serverWaypoints.size(); i++) {
            if (serverWaypoints.get(i).getWaypointName().equals(waypointName)) {
                serverWaypoints.remove(i);
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
