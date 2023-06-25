package com.tomo.thereisway.management.waypoints;


import com.google.gson.Gson;
import com.tomo.thereisway.waypoints.*;
import org.bukkit.entity.Player;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.ArrayList;
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
            List<WaypointPOJO> waypointPOJO = playerWaypoints.stream().map(PlayerWaypoint::toWaypointPOJO).collect(Collectors.toList());
            waypointPOJO.addAll(serverWaypoints.stream().map(ServerWaypoint::toWaypointPOJO).toList());

            WaypointPOJO[] waypointsArr = waypointPOJO.toArray(new WaypointPOJO[0]);

            Gson gson = new Gson();
            gson.toJson(waypointsArr, new FileWriter(file));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static WaypointHolder loadDataFromJson(String filePath) {
//        try {
//            File file = new File(filePath);
//            if (!file.exists()) {
//                return new WaypointHolder();
//            }
//            ObjectMapper mapper = new ObjectMapper();
//            List<WaypointPOJO> waypoints = mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, WaypointPOJO.class));
//
//            WaypointHolder waypointHolder = new WaypointHolder();
//            waypoints.stream().map(WaypointPOJO::toWaypoint).forEach(waypointHolder::addWaypoint);
//            for (Waypoint waypoint : waypointHolder.playerWaypoints) {
//                if (waypoint.isEffectOn(WaypointEffect.ENDER_CRYSTAL)) {
//                    waypoint.spawnEnderCrystal();
//                }
//            }
//            for (Waypoint waypoint : waypointHolder.serverWaypoints) {
//                if (waypoint.isEffectOn(WaypointEffect.ENDER_CRYSTAL)) {
//                    waypoint.spawnEnderCrystal();
//                }
//            }
//            return waypointHolder;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    private void addWaypoint(Waypoint waypoint) {
        if (waypoint instanceof PlayerWaypoint) {
            playerWaypoints.add((PlayerWaypoint) waypoint);
        }
        serverWaypoints.add((ServerWaypoint) waypoint);
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
