package com.tomo.thereisway.waypoints;

import com.tomo.thereisway.management.utilities.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class PlayerWaypoint extends Waypoint{


    private final UUID ownerID;

    public PlayerWaypoint(Location placement, String ownerID, String waypointName) {
        super();
        super.waypointName = waypointName;
        super.placement = placement;
        this.ownerID = UUID.fromString(ownerID);
    }

    public static PlayerWaypoint createWaypoint(Location waypointLocation,String playerUUID, String waypointName) {
        return new PlayerWaypoint(waypointLocation, playerUUID, waypointName);
    }

    public boolean isOwnedByPlayer(Player player) {
        return Objects.equals(player.getPlayerProfile().getId(), ownerID);
    }


    @Override
    public String toString() {
        String result = "Waypoint: " + waypointName + ", ";
        result = result + "Location = " + getLocation();
        return result;
    }

    public String getSimpleInfo() {
        return  "Waypoint: " + ChatUtils.asGreenMessage(waypointName);
    }

    public String getWholeInfo() {
        String result = "Waypoint: " + ChatUtils.asGreenMessage(waypointName) + ", ";
        result = result + ChatUtils.asBlueMessage("Location = " + getLocation());
        return result;
    }

    public TextComponent asClickableColoredMessage() {
        TextComponent message = Component.text(getSimpleInfo());
        TextComponent clickable = Component.text(ChatUtils.coloredMessage(" [Click here to teleport]", ChatColor.GOLD))
                .clickEvent(ClickEvent.runCommand("/waypoint move " + waypointName));
        return message.append(clickable);
    }

    @Override
    public WaypointPOJO toWaypointPOJO() {
        return new WaypointPOJO(waypointName,
                placement.getWorld().getUID().toString(),
                placement.getBlockX(),
                placement.getBlockY(),
                placement.getBlockZ(),
                isEffectOn(WaypointEffect.ENDER_CRYSTAL),
                isEffectOn(WaypointEffect.NAME_HOLO),
                true,
                ownerID.toString());
    }
}
