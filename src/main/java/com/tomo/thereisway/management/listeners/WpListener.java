package com.tomo.thereisway.management.listeners;

import com.tomo.thereisway.ThereISWay;
import com.tomo.thereisway.management.events.WaypointRelatedEvent;
import com.tomo.thereisway.management.utilities.gui.WaypointServiceGui;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

public class WpListener implements Listener {

    private static ThereISWay plugin;
    private WaypointServiceGui waypointServiceGui;

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
    public void onWaypointEvent(WaypointRelatedEvent event) {
        if (event.getReason().equals(WaypointRelatedEvent.Reason.OPEN_SERVICE)) {
            waypointServiceGui = new WaypointServiceGui(plugin, event.getTrigger());
            waypointServiceGui.loadFirstStep();
        } else if (event.getReason().equals(WaypointRelatedEvent.Reason.SAVE_EDIT)) {
            plugin.saveWaypoints();
        }
        else if (event.getReason().equals(WaypointRelatedEvent.Reason.OPEN_EDIT)) {
            waypointServiceGui = new WaypointServiceGui(plugin, event.getTrigger());
            waypointServiceGui.loadWaypointMainGui(event.getWaypoint(), -1);
        }

    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (Objects.isNull(waypointServiceGui)) return;
        if (!waypointServiceGui.isEqualToInventory(e.getInventory())) return;
        if (e.getSlot() >= e.getInventory().getSize() || e.getSlot() <0) return;
        waypointServiceGui.playerProceedOnSlot(e.getSlot());

        e.setCancelled(true);

    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (Objects.isNull(waypointServiceGui)) return;
        if (waypointServiceGui.isEqualToInventory(e.getInventory())) {
            e.setCancelled(true);
        }
    }

}
