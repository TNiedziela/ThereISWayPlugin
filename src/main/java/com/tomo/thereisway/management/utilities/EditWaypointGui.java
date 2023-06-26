package com.tomo.thereisway.management.utilities;

import com.tomo.thereisway.management.utilities.ChatUtils;
import com.tomo.thereisway.waypoints.Waypoint;
import com.tomo.thereisway.waypoints.WaypointEffect;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EditWaypointGui {
    private final Inventory guiInventory;

    private final Waypoint waypoint;

    public EditWaypointGui(Waypoint waypoint) {
        guiInventory = Bukkit.createInventory(null, 9, Component.text("Edit waypoint " + waypoint.getWaypointName()));
        this.waypoint = waypoint;

        loadFirstStepItems();
    }

    public void loadFirstStepItems() {
        guiInventory.setItem(4, createGuiItem(Material.END_CRYSTAL,
                ChatUtils.asDarkPurpleMessage("End crystal"), ChatUtils.asGreenMessage("end crystal"), ChatUtils.asGreenMessage("settings")));
    }

    public void loadEndCrystalStepItems() {
        guiInventory.setItem(4, createGuiItem(Material.ENDER_EYE,
                ChatUtils.asDarkPurpleMessage("Visibility"), ChatUtils.asGreenMessage("set crystal"), ChatUtils.asGreenMessage("visibility")));

        guiInventory.setItem(5, createGuiItem(Material.NAME_TAG,
                ChatUtils.asDarkPurpleMessage("Name"), ChatUtils.asGreenMessage("set waypoint"), ChatUtils.asGreenMessage("name visibility")));
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