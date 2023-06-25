package com.tomo.thereisway.management.listeners;

import com.tomo.thereisway.ThereISWay;
import com.tomo.thereisway.management.events.WaypointModifiedEvent;
import com.tomo.thereisway.management.waypoints.WaypointManagementService;
import com.tomo.thereisway.waypoints.Waypoint;
import com.tomo.thereisway.waypoints.WaypointEffect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class WpListener implements Listener {

    private static ThereISWay plugin;

    private EditWaypointGui editWaypointGui;
    private final WaypointManagementService waypointManagementService;

    public WpListener(ThereISWay plugin) {
        WpListener.plugin = plugin;
        this.waypointManagementService = new WaypointManagementService(plugin);
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
        if (event.getReason().equals(WaypointModifiedEvent.Reason.OPEN_EDIT)) {
            editWaypointGui = new EditWaypointGui(event.getWaypoint());
            editWaypointGui.openInventory(event.getTrigger());
        } else if (event.getReason().equals(WaypointModifiedEvent.Reason.SAVE_EDIT)) {
            editWaypointGui = null;
        }
        plugin.saveWaypoints();
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (Objects.isNull(editWaypointGui)) return;
        if (!e.getInventory().equals(editWaypointGui.getGuiInventory())) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        final Player p = (Player) e.getWhoClicked();

        if (clickedItem.getType().equals(Material.END_CRYSTAL)) {
            editWaypointGui.loadEndCrystalStepItems();
        }

        if (clickedItem.getType().equals(Material.ENDER_EYE)) {
            Waypoint waypoint = editWaypointGui.getWaypoint();
            if (!waypoint.isEffectOn(WaypointEffect.ENDER_CRYSTAL)) {
                spawnCrystalOnWaypoint(waypoint);
            } else {
                removeCrystalFromWaypoint(waypoint);
            }
            editWaypointGui.loadEndCrystalStepItems();
        }

        if (clickedItem.getType().equals(Material.NAME_TAG)) {
            Waypoint waypoint = editWaypointGui.getWaypoint();
            if (!waypoint.isCrystalNameVisible()) {
                waypoint.setCrystalNameVisible();
            } else {
                waypoint.setCrystalNameNotVisible();
            }
            editWaypointGui.loadEndCrystalStepItems();
        }

    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (Objects.isNull(editWaypointGui)) return;
        if (e.getInventory().equals(editWaypointGui.getGuiInventory())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent e) {
        if (Objects.isNull(editWaypointGui)) return;
        if (e.getInventory().equals(editWaypointGui.getGuiInventory())) {
            WaypointModifiedEvent.waypointSaveEditEvent(editWaypointGui.getWaypoint(), (Player) e.getPlayer()).callEvent();
        }
    }

    private void spawnCrystalOnWaypoint(Waypoint waypoint) {
        waypointManagementService.spawnEnderCrystalOnWaypoint(waypoint);
    }

    private void removeCrystalFromWaypoint(Waypoint waypoint) {
        waypointManagementService.despawnEnderCrystalFromWaypoint(waypoint);
    }

}
