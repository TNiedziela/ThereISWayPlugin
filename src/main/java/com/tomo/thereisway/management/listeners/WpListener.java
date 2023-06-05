package com.tomo.thereisway.management.listeners;

import com.tomo.thereisway.ThereISWay;
import com.tomo.thereisway.management.events.WaypointModifiedEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class WpListener implements Listener {

    private static ThereISWay plugin;

    public WpListener(ThereISWay plugin) {
        WpListener.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            player.sendMessage("Welcome on TOMO server!");
        }
    }

    @EventHandler
    public void onWaypointEvent(WaypointModifiedEvent event) {
        plugin.saveWaypoints();
    }

}
