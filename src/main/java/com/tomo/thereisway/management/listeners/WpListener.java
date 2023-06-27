package com.tomo.thereisway.management.listeners;

import com.tomo.thereisway.ThereISWay;
import com.tomo.thereisway.management.events.WaypointRelatedEvent;
import com.tomo.thereisway.management.utilities.ChatUtils;
import com.tomo.thereisway.management.utilities.WaypointServiceGui;
import com.tomo.thereisway.management.waypoints.WaypointManagementService;
import com.tomo.thereisway.waypoints.Waypoint;
import com.tomo.thereisway.waypoints.WaypointEffect;
import net.kyori.adventure.text.Component;
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

    private WaypointServiceGui waypointServiceGui;
    private final WaypointManagementService waypointManagementService;

    private int page = 0;

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
    public void onWaypointEvent(WaypointRelatedEvent event) {
        if (event.getReason().equals(WaypointRelatedEvent.Reason.OPEN_SERVICE)) {
            waypointServiceGui = new WaypointServiceGui(plugin, event.getWaypoint())
                    .loadServiceFirstStepItems();
            waypointServiceGui.openInventory(event.getTrigger());
        } else if (event.getReason().equals(WaypointRelatedEvent.Reason.OPEN_EDIT)) {
            waypointServiceGui = new WaypointServiceGui(plugin, event.getWaypoint())
                    .loadWaypointFirstStepItems();
            waypointServiceGui.openInventory(event.getTrigger());
        } else if (event.getReason().equals(WaypointRelatedEvent.Reason.SAVE_EDIT)) {
            waypointServiceGui = null;
        }
        plugin.saveWaypoints();
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (Objects.isNull(waypointServiceGui)) return;
        if (!e.getInventory().equals(waypointServiceGui.getGuiInventory())) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        final Player p = (Player) e.getWhoClicked();

        if (clickedItem.getType().equals(Material.END_CRYSTAL)) {
            waypointServiceGui.loadEndCrystalStepItems();
        }

        if (clickedItem.getType().equals(Material.FILLED_MAP)) {
            if (Objects.equals(clickedItem.getItemMeta().displayName(), Component.text(ChatUtils.asGoldMessage("Global waypoints")))) {
                waypointServiceGui.loadServiceServerWaypointsListStepItems(page);
            }
            if (Objects.equals(clickedItem.getItemMeta().displayName(), Component.text(ChatUtils.asDarkPurpleMessage("Player waypoints")))) {
                waypointServiceGui.loadServicePlayerWaypointsListStepItems((Player) e.getWhoClicked(), page);
            }
            waypointServiceGui.openInventory(e.getWhoClicked());
        }

        if (clickedItem.getType().equals(Material.ARROW)) {
            if (Objects.equals(clickedItem.getItemMeta().displayName(), Component.text(ChatUtils.asBlueMessage("Next server waypoints")))) {
                page++;
                waypointServiceGui.loadServiceServerWaypointsListStepItems(page);
            }
            if (Objects.equals(clickedItem.getItemMeta().displayName(), Component.text(ChatUtils.asBlueMessage("Previous server waypoints")))) {
                page--;
                waypointServiceGui.loadServiceServerWaypointsListStepItems(page);
            }

            if (Objects.equals(clickedItem.getItemMeta().displayName(), Component.text(ChatUtils.asBlueMessage("Next player waypoints")))) {
                page++;
                waypointServiceGui.loadServicePlayerWaypointsListStepItems((Player) e.getWhoClicked(), page);
            }
            if (Objects.equals(clickedItem.getItemMeta().displayName(), Component.text(ChatUtils.asBlueMessage("Previous player waypoints")))) {
                page--;
                waypointServiceGui.loadServicePlayerWaypointsListStepItems((Player) e.getWhoClicked(),page);
            }
        }

        if (clickedItem.getType().equals(Material.NETHER_STAR)) {
            waypointServiceGui.withWaypoint(waypointManagementService.getPlayerWaypointByNameIfExists((Player) e.getWhoClicked(),
                    clickedItem.getItemMeta().getDisplayName()).get());
            waypointServiceGui.loadWaypointFirstStepItems();
            waypointServiceGui.openInventory(e.getWhoClicked());
        }
        if (clickedItem.getType().equals(Material.AMETHYST_SHARD)) {
            waypointServiceGui.withWaypoint(waypointManagementService.getServerWaypointByNameIfExists(clickedItem.getItemMeta().getDisplayName()).get());
            waypointServiceGui.loadWaypointFirstStepItems();
            waypointServiceGui.openInventory(e.getWhoClicked());
        }

        if (clickedItem.getType().equals(Material.ENDER_EYE)) {
            Waypoint waypoint = waypointServiceGui.getWaypoint();
            if (!waypoint.isEffectOn(WaypointEffect.ENDER_CRYSTAL)) {
                spawnCrystalOnWaypoint(waypoint);
            } else {
                removeCrystalFromWaypoint(waypoint);
            }
            waypointServiceGui.loadEndCrystalStepItems();
        }

        if (clickedItem.getType().equals(Material.NAME_TAG)) {
            Waypoint waypoint = waypointServiceGui.getWaypoint();
            if (!waypoint.isCrystalNameVisible()) {
                waypoint.setCrystalNameVisible();
            } else {
                waypoint.setCrystalNameNotVisible();
            }
            waypointServiceGui.loadEndCrystalStepItems();
        }

    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (Objects.isNull(waypointServiceGui)) return;
        if (e.getInventory().equals(waypointServiceGui.getGuiInventory())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent e) {
        page = 0;
        if (Objects.isNull(waypointServiceGui)) return;
        if (e.getInventory().equals(waypointServiceGui.getGuiInventory())) {
            WaypointRelatedEvent.waypointSaveEditEvent(waypointServiceGui.getWaypoint(), (Player) e.getPlayer()).callEvent();
        }
    }

    private void spawnCrystalOnWaypoint(Waypoint waypoint) {
        waypointManagementService.spawnEnderCrystalOnWaypoint(waypoint);
    }

    private void removeCrystalFromWaypoint(Waypoint waypoint) {
        waypointManagementService.despawnEnderCrystalFromWaypoint(waypoint);
    }

}
