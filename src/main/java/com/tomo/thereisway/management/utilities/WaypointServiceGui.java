package com.tomo.thereisway.management.utilities;

import com.tomo.thereisway.ThereISWay;
import com.tomo.thereisway.management.waypoints.WaypointManagementService;
import com.tomo.thereisway.waypoints.PlayerWaypoint;
import com.tomo.thereisway.waypoints.ServerWaypoint;
import com.tomo.thereisway.waypoints.Waypoint;
import com.tomo.thereisway.waypoints.WaypointEffect;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WaypointServiceGui {
    private Inventory guiInventory;

    private final ThereISWay plugin;

    private Waypoint waypoint;

    private WaypointManagementService waypointManagementService;

    public WaypointServiceGui(ThereISWay plugin, Waypoint waypoint) {
        guiInventory = Bukkit.createInventory(null, 9, Component.text("Waypoint service"));
        this.waypoint = waypoint;
        this.plugin = plugin;
        waypointManagementService = new WaypointManagementService(plugin);
    }

    public WaypointServiceGui withWaypoint(Waypoint waypoint) {
        this.waypoint = waypoint;
        return this;
    }

    public WaypointServiceGui loadServiceFirstStepItems() {
        guiInventory.setItem(3, createGuiItem(Material.FILLED_MAP,
                ChatUtils.asGoldMessage("Global waypoints"), ChatUtils.asGreenMessage("Show global"), ChatUtils.asGreenMessage("waypoints")));
        guiInventory.setItem(5, createGuiItem(Material.FILLED_MAP,
                ChatUtils.asDarkPurpleMessage("Player waypoints"), ChatUtils.asGreenMessage("Show player"), ChatUtils.asGreenMessage("waypoints")));
        return this;
    }

    public WaypointServiceGui loadServicePlayerWaypointsListStepItems(Player player, int page) {
        List<PlayerWaypoint> playerWaypoints = waypointManagementService.getWaypointsOwnedByPlayer(player);
        guiInventory = Bukkit.createInventory(null, 18, Component.text("List of owned waypoints"));
        if (playerWaypoints.size() <= 9) {
            loadWaypointsPage(playerWaypoints, false,false, true);
        } else {
            List<PlayerWaypoint> pageWaypoints = playerWaypoints.subList(page * 9, Math.min(playerWaypoints.size(), page * 9 + 9));
            loadWaypointsPage(pageWaypoints, playerWaypoints.size() > page * 9 + 9,true, true);
        }
        return this;
    }

    public WaypointServiceGui loadServiceServerWaypointsListStepItems(int page) {
        List<ServerWaypoint> serverWaypoints = plugin.getServerWaypoints();
        guiInventory = Bukkit.createInventory(null, 18, Component.text("Server waypoints"));
        if (serverWaypoints.size() <= 9) {
            loadWaypointsPage(serverWaypoints, false,false, false);
        } else {
            List<ServerWaypoint> pageWaypoints = serverWaypoints.subList(page * 9, Math.min(serverWaypoints.size(), page * 9 + 9));
            loadWaypointsPage(pageWaypoints, serverWaypoints.size() > page * 9 + 9,true, false);
        }
        return this;
    }

    private void loadWaypointsPage(List<? extends Waypoint> pageWaypoints, boolean arrowRight, boolean arrowLeft, boolean playerWaypoints) {
        Material material = playerWaypoints ? Material.NETHER_STAR : Material.AMETHYST_SHARD;
        for(int itemIndex = 0; itemIndex < pageWaypoints.size(); itemIndex++) {
            guiInventory.setItem(itemIndex, createGuiItem(material,
                    ChatUtils.asGoldMessage(pageWaypoints.get(itemIndex).getWaypointName()), ChatUtils.asGreenMessage("Click to"), ChatUtils.asGreenMessage("teleport")));
        }
        String waypointsType = playerWaypoints ? "player waypoints" : "server waypoints";

        if (arrowLeft) {
            guiInventory.setItem(10, createGuiItem(Material.ARROW, ChatUtils.asBlueMessage("Previous" + waypointsType)));
        }
        if (arrowRight) {
            guiInventory.setItem(17, createGuiItem(Material.ARROW, ChatUtils.asBlueMessage("Next" + waypointsType)));
        }

    }

    public WaypointServiceGui loadWaypointFirstStepItems() {
        guiInventory = Bukkit.createInventory(null, 9, Component.text("Edit waypoint" + waypoint.getWaypointName()));
        guiInventory.setItem(4, createGuiItem(Material.END_CRYSTAL,
                ChatUtils.asDarkPurpleMessage("End crystal"), ChatUtils.asGreenMessage("end crystal"), ChatUtils.asGreenMessage("settings")));
        return this;
    }

    public WaypointServiceGui loadEndCrystalStepItems() {
        guiInventory.setItem(4, createGuiItem(Material.ENDER_EYE,
                ChatUtils.asDarkPurpleMessage("Visibility"), ChatUtils.asGreenMessage("set crystal"), ChatUtils.asGreenMessage("visibility")));

        guiInventory.setItem(5, createGuiItem(Material.NAME_TAG,
                ChatUtils.asDarkPurpleMessage("Name"), ChatUtils.asGreenMessage("set waypoint"), ChatUtils.asGreenMessage("name visibility")));
        return this;
    }

    private ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        List<String> loreList = Arrays.asList(lore);
        String loreOnOrOff = ChatUtils.asRedMessage("OFF");
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();
        List<String> finalLoreList = new ArrayList<>();

        if (material.equals(Material.ENDER_EYE)) {
            if (waypoint.isEffectOn(WaypointEffect.ENDER_CRYSTAL)) {
                loreOnOrOff = ChatUtils.asGreenMessage("ON");
            }
            finalLoreList.add(loreOnOrOff);
        }

        if (material.equals(Material.NAME_TAG)) {
            if (waypoint.isCrystalNameVisible()) {
                loreOnOrOff = ChatUtils.asGreenMessage("ON");
            }
            finalLoreList.add(loreOnOrOff);
        }
        finalLoreList.addAll(loreList);
        meta.displayName(Component.text(name));
        meta.lore(finalLoreList.stream().map(Component::text).collect(Collectors.toList()));

        item.setItemMeta(meta);

        return item;
    }

    public Inventory getGuiInventory() {
        return guiInventory;
    }

    public Waypoint getWaypoint() {
        return waypoint;
    }

    // You can open the inventory with this
    public void openInventory(final HumanEntity ent) {
        ent.openInventory(guiInventory);
    }
}