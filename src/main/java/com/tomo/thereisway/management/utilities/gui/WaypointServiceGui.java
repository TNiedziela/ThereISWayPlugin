package com.tomo.thereisway.management.utilities.gui;

import com.tomo.thereisway.ThereISWay;
import com.tomo.thereisway.management.commands.WaypointCommandsService;
import com.tomo.thereisway.management.utilities.ChatUtils;
import com.tomo.thereisway.management.waypoints.WaypointManagementService;
import com.tomo.thereisway.waypoints.PlayerWaypoint;
import com.tomo.thereisway.waypoints.ServerWaypoint;
import com.tomo.thereisway.waypoints.Waypoint;
import com.tomo.thereisway.waypoints.WaypointEffect;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class WaypointServiceGui {

    private final ThereISWay plugin;
    private final Player player;
    private InventoryGui gui;
    private final WaypointManagementService waypointManagementService;
    private final WaypointCommandsService commandsService;

    private final Map<Integer, List<PlayerWaypoint>> playerWaypointPages = new HashMap<>();
    private final Map<Integer, List<ServerWaypoint>> serverWaypointPages = new HashMap<>();

    public WaypointServiceGui(ThereISWay plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        waypointManagementService = new WaypointManagementService(plugin);
        commandsService = new WaypointCommandsService(plugin);
        populatePages();
        loadFirstStep();
    }

    public void loadFirstStep() {

        Map<Integer, ItemStack> itemsMap = new HashMap<>();
        itemsMap.put(3, createGuiItem(Material.FILLED_MAP, ChatUtils.asGoldMessage("Global waypoints"), ChatUtils.asGreenMessage("Show global"), ChatUtils.asGreenMessage("waypoints")));
        itemsMap.put(5, createGuiItem(Material.FILLED_MAP, ChatUtils.asDarkPurpleMessage("Player waypoints"), ChatUtils.asGreenMessage("Show player"), ChatUtils.asGreenMessage("waypoints")));

        Map<Integer, Runnable> methodsMap = new HashMap<>();
        methodsMap.put(3, () -> loadServerWaypointsPage(0));
        methodsMap.put(5, () -> loadPlayerWaypointsPage(0));

        InventoryGui gui = InventoryGui.Builder.createBuilder()
                .withRowCount(2)
                .withName("Waypoints service")
                .withItems(itemsMap)
                .withMethods(methodsMap)
                .build();

        gui.openInventory(player);
        this.gui = gui;
    }

    public boolean isEqualToInventory(Inventory inventory) {
        return gui.getInventory().equals(inventory);
    }

    public void playerProceedOnSlot(int slot) {
        gui.run(slot);
    }


    private void loadPlayerWaypointsPage(int page) {
        List<PlayerWaypoint> pageWaypoints = playerWaypointPages.get(page);
        Map<Integer, ItemStack> itemsMap = new HashMap<>();
        Map<Integer, Runnable> methodsMap = new HashMap<>();

        for(int itemIndex = 0; itemIndex < pageWaypoints.size(); itemIndex++) {
            Waypoint waypoint = pageWaypoints.get(itemIndex);
            itemsMap.put(itemIndex, createGuiItem(Material.NETHER_STAR,
                    ChatUtils.asGoldMessage(waypoint.getWaypointName()), ChatUtils.asGreenMessage("Open waypoint"), ChatUtils.asGreenMessage("menu")));
            methodsMap.put(itemIndex, () -> loadWaypointMainGui(waypoint, page));
        }

        if (page > 0) {
            itemsMap.put(9, createGuiItem(Material.ARROW, ChatUtils.asBlueMessage("Previous player waypoints")));
            methodsMap.put(9, () -> loadPlayerWaypointsPage(page - 1));
        }
        if (page < playerWaypointPages.size() - 1) {
            itemsMap.put(17, createGuiItem(Material.ARROW, ChatUtils.asBlueMessage("Next player waypoints")));
            methodsMap.put(17, () -> loadPlayerWaypointsPage(page + 1));
        }

        InventoryGui gui = InventoryGui.Builder.createBuilder()
                .withRowCount(2)
                .withName("Player waypoints")
                .withItems(itemsMap)
                .withMethods(methodsMap)
                .build();

        gui.openInventory(player);
        this.gui = gui;
    }

    private void loadWaypointMainGui(Waypoint waypoint, int fromPage) {
        Map<Integer, ItemStack> itemsMap = new HashMap<>();
        Map<Integer, Runnable> methodsMap = new HashMap<>();
        itemsMap.put(3, createGuiItem(Material.COMPASS,
                ChatUtils.asGoldMessage("Teleport"), ChatUtils.asGreenMessage("Click to"), ChatUtils.asGreenMessage("teleport")));
        itemsMap.put(5, createGuiItem(Material.WRITABLE_BOOK,
                ChatUtils.asGoldMessage("Edit"), ChatUtils.asGreenMessage("Click to"), ChatUtils.asGreenMessage("edit waypoint")));

        if (waypoint instanceof PlayerWaypoint) {
            methodsMap.put(3, () -> moveToPlayerWaypoint(player, waypoint.getWaypointName()));
        } else {
            methodsMap.put(3, () -> moveToServerWaypoint(player, waypoint.getWaypointName()));
        }
        methodsMap.put(5, () -> loadWaypointEditGui(waypoint, fromPage));

        itemsMap.put(9, createGuiItem(Material.ARROW, ChatUtils.asBlueMessage("Return")));
        if (waypoint instanceof PlayerWaypoint) {
            methodsMap.put(9, () -> loadPlayerWaypointsPage(fromPage));
        } else {
            methodsMap.put(9, () -> loadServerWaypointsPage(fromPage));
        }

        InventoryGui gui = InventoryGui.Builder.createBuilder()
                .withRowCount(2)
                .withName(waypoint.getWaypointName() + " waypoint main menu")
                .withItems(itemsMap)
                .withMethods(methodsMap)
                .build();

        gui.openInventory(player);
        this.gui = gui;
    }
    private void loadWaypointEditGui(Waypoint waypoint, int fromPage) {
        Map<Integer, ItemStack> itemsMap = new HashMap<>();
        Map<Integer, Runnable> methodsMap = new HashMap<>();
        itemsMap.put(4, createGuiItem(Material.END_CRYSTAL,
                ChatUtils.asDarkPurpleMessage("End crystal"), ChatUtils.asGreenMessage("end crystal"), ChatUtils.asGreenMessage("settings")));
        methodsMap.put(4, () -> loadEnderCrystalEditGui(waypoint, fromPage));

        itemsMap.put(9, createGuiItem(Material.ARROW, ChatUtils.asBlueMessage("Return")));
        methodsMap.put(9, () -> loadWaypointMainGui(waypoint, fromPage));

        InventoryGui gui = InventoryGui.Builder.createBuilder()
                .withRowCount(2)
                .withName("Edit " + waypoint.getWaypointName() + " waypoint")
                .withItems(itemsMap)
                .withMethods(methodsMap)
                .build();

        gui.openInventory(player);
        this.gui = gui;
    }
    private void loadEnderCrystalEditGui(Waypoint waypoint, int fromPage) {
        Map<Integer, ItemStack> itemsMap = new HashMap<>();
        Map<Integer, Runnable> methodsMap = new HashMap<>();
        String crystalOnOrOff = waypoint.isEffectOn(WaypointEffect.ENDER_CRYSTAL) ? ChatUtils.asGreenMessage("ON") : ChatUtils.asRedMessage("OFF");
        String nameOnOrOff = waypoint.isCrystalNameVisible() ? ChatUtils.asGreenMessage("ON") : ChatUtils.asRedMessage("OFF");

        itemsMap.put(4, createGuiItem(Material.ENDER_EYE,
                ChatUtils.asDarkPurpleMessage("Visibility"), ChatUtils.asGreenMessage("set crystal"), ChatUtils.asGreenMessage("visibility"), crystalOnOrOff));
        methodsMap.put(4, () -> switchCrystalStatus(waypoint, fromPage));

        itemsMap.put(5, createGuiItem(Material.NAME_TAG,
                ChatUtils.asDarkPurpleMessage("Name"), ChatUtils.asGreenMessage("set waypoint"), ChatUtils.asGreenMessage("name visibility"), nameOnOrOff));
        methodsMap.put(5, () -> switchCrystalNameVisible(waypoint, fromPage));

        itemsMap.put(9, createGuiItem(Material.ARROW, ChatUtils.asBlueMessage("Return")));
        methodsMap.put(9, () -> loadWaypointEditGui(waypoint, fromPage));

        InventoryGui gui = InventoryGui.Builder.createBuilder()
                .withRowCount(2)
                .withName("Ender crystal settings")
                .withItems(itemsMap)
                .withMethods(methodsMap)
                .build();

        gui.openInventory(player);
        this.gui = gui;
    }

    private void switchCrystalStatus(Waypoint waypoint, int fromPage) {
        if (waypoint.isEffectOn(WaypointEffect.ENDER_CRYSTAL)) {
            removeCrystalFromWaypoint(waypoint);
        } else {
            spawnCrystalOnWaypoint(waypoint);
        }
        loadEnderCrystalEditGui(waypoint, fromPage);
    }

    private void switchCrystalNameVisible(Waypoint waypoint, int fromPage) {
        if (!waypoint.isCrystalNameVisible()) {
                waypoint.setCrystalNameVisible();
        } else {
                waypoint.setCrystalNameNotVisible();
        }
        loadEnderCrystalEditGui(waypoint, fromPage);
    }

    private void spawnCrystalOnWaypoint(Waypoint waypoint) {
        waypointManagementService.spawnEnderCrystalOnWaypoint(waypoint);
    }

    private void removeCrystalFromWaypoint(Waypoint waypoint) {
        waypointManagementService.despawnEnderCrystalFromWaypoint(waypoint);
    }

    private void loadServerWaypointsPage(int page) {
        List<ServerWaypoint> pageWaypoints = serverWaypointPages.get(page);
        Map<Integer, ItemStack> itemsMap = new HashMap<>();
        Map<Integer, Runnable> methodsMap = new HashMap<>();

        for(int itemIndex = 0; itemIndex < pageWaypoints.size(); itemIndex++) {
            Waypoint waypoint = pageWaypoints.get(itemIndex);
            itemsMap.put(itemIndex, createGuiItem(Material.AMETHYST_SHARD,
                    ChatUtils.asGoldMessage(waypoint.getWaypointName()), ChatUtils.asGreenMessage("Open waypoint"), ChatUtils.asGreenMessage("menu")));
            methodsMap.put(itemIndex, () -> loadWaypointMainGui(waypoint, page));
        }

        if (page > 0) {
            itemsMap.put(9, createGuiItem(Material.ARROW, ChatUtils.asBlueMessage("Previous server waypoints")));
            methodsMap.put(9, () -> loadServerWaypointsPage(page - 1));
        }
        if (page < serverWaypointPages.size() - 1) {
            itemsMap.put(17, createGuiItem(Material.ARROW, ChatUtils.asBlueMessage("Next server waypoints")));
            methodsMap.put(17, () -> loadServerWaypointsPage(page + 1));
        }

        InventoryGui gui = InventoryGui.Builder.createBuilder()
                .withRowCount(2)
                .withName("Server waypoints")
                .withItems(itemsMap)
                .withMethods(methodsMap)
                .build();

        gui.openInventory(player);
        this.gui = gui;
    }

    private void moveToPlayerWaypoint(Player player, String waypointName) {
        commandsService.movePlayerToPlayerWaypointIfPossible(player, waypointName);
        player.closeInventory();
    }

    private void moveToServerWaypoint(Player player, String waypointName) {
        commandsService.movePlayerToServerWaypointIfPossible(player, waypointName);
        player.closeInventory();
    }

    private void populatePages() {
        List<PlayerWaypoint> playerWaypoints = waypointManagementService.getWaypointsOwnedByPlayer(player);
        List<ServerWaypoint> serverWaypoints = plugin.getServerWaypoints();

        for (int i = 0; i < Math.ceil(playerWaypoints.size()/9.0); i++) {
            List<PlayerWaypoint> pagePlayerWaypoints = playerWaypoints.subList(i*9, Math.min(playerWaypoints.size(), (i+1)*9));
            playerWaypointPages.put(i, pagePlayerWaypoints);
        }

        for (int j = 0; j < Math.ceil(serverWaypoints.size()/9.0); j++) {
            List<ServerWaypoint> pageServerWaypoints = serverWaypoints.subList(j*9, Math.min(serverWaypoints.size(), (j+1)*9));
            serverWaypointPages.put(j, pageServerWaypoints);
        }
    }

    private ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        List<String> loreList = Arrays.asList(lore);
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text(name));
        meta.lore(loreList.stream().map(Component::text).collect(Collectors.toList()));

        item.setItemMeta(meta);

        return item;
    }
}
