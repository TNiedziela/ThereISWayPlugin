package com.tomo.thereisway.management.commands;

import com.tomo.thereisway.ThereISWay;
import com.tomo.thereisway.management.utilities.ChatUtils;
import com.tomo.thereisway.management.utilities.WaypointCommandTabCompleter;
import com.tomo.thereisway.management.waypoints.WaypointManagementService;
import com.tomo.thereisway.waypoints.PlayerWaypoint;
import com.tomo.thereisway.waypoints.ServerWaypoint;
import com.tomo.thereisway.waypoints.Waypoint;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class WaypointCommand implements CommandExecutor {
    private final ThereISWay plugin;

    private final WaypointManagementService waypointManagementService;

    public WaypointCommand(ThereISWay plugin) {
        this.plugin = plugin;
        this.waypointManagementService = new WaypointManagementService(plugin);
        PluginCommand command = Objects.requireNonNull(plugin.getCommand("waypoint"));
        command.setTabCompleter(new WaypointCommandTabCompleter(waypointManagementService));
        command.setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> commandArgs = Arrays.asList(args);
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("Waypoint creation is possible only by players!");
            return true;
        }
        if (!player.isOp()) {
            player.sendMessage("You are not permitted to create waypoints!");
            return true;
        } else {
            WaypointCommandType actualCommand = WaypointCommandType.get(commandArgs.get(0)).orElse(WaypointCommandType.WRONG);
            String waypointName = commandArgs.size() == 2 ? commandArgs.get(1) : "";
            Map<WaypointCommandType, Runnable> commands = getCommands(player, waypointName);
            commands.get(actualCommand).run();
        }
        return false;
    }

    private Map<WaypointCommandType, Runnable> getCommands(Player player, String waypointName) {
        Map<WaypointCommandType, Runnable> commands = new HashMap<>();
        commands.put(WaypointCommandType.CREATE, () -> waypointManagementService.createPlayerWaypoint(player, waypointName));
        commands.put(WaypointCommandType.DELETE, () -> waypointManagementService.deletePlayerWaypoint(player, waypointName));
        commands.put(WaypointCommandType.MOVE, () -> movePlayerToPlayerWaypointIfPossible(player, waypointName));
        commands.put(WaypointCommandType.SHOW, () -> showPlayerHisWaypoints(player));
        commands.put(WaypointCommandType.SHOW_ALL, () -> showAllWaypoints(player));
        commands.put(WaypointCommandType.WRONG, () -> wrongCommandProvidedMessage(player));
        return commands;
    }

    private void movePlayerToPlayerWaypointIfPossible(Player player, String waypointName) {
        if (waypointNameNotProvided(player, waypointName)) {
            return;
        }
        Optional<PlayerWaypoint> playerWaypoint = waypointManagementService.getPlayerWaypointByNameIfExists(player, waypointName);
        if (playerWaypoint.isEmpty()) {
            player.sendMessage("You don't have waypoint with such name (" + waypointName + ")");
        } else {
            PlayerWaypoint waypoint = playerWaypoint.get();
            movePlayerToWaypointAndApplyEffects(player, waypoint);
        }
    }

    private void movePlayerToWaypointAndApplyEffects(Player player, Waypoint waypoint) {
        int moveDurationInTicks = 60;
        applyTeleportEffectOnPlayer(player);
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> player.teleport(waypoint.getPlacement()), moveDurationInTicks);
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> player
                        .playSound(Sound.sound(org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT.key(),
                                Sound.Source.PLAYER, 1f, 1f)), moveDurationInTicks);
        Bukkit.getScheduler().runTaskLater(this.plugin,
                () -> player.sendMessage(ChatUtils.asGreenMessage("Successfully moved to: " + waypoint.getWaypointName())), moveDurationInTicks);
    }

    private void applyTeleportEffectOnPlayer(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 60, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 60, 4));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 100));
    }

    private void movePlayerToServerWaypointIfPossible(Player player, String waypointName) {
        if (waypointNameNotProvided(player, waypointName)) {
            return;
        }
        Optional<ServerWaypoint> serverWaypoint = waypointManagementService.getServerWaypointByNameIfExists(waypointName);
        if (serverWaypoint.isEmpty()) {
            player.sendMessage("There is no server waypoint with such name (" + waypointName + ")");
        } else {
            ServerWaypoint waypoint = serverWaypoint.get();
            player.teleport(waypoint.getPlacement());
            player.sendMessage(ChatUtils.asGreenMessage("Successfully moved to: " + waypointName));
        }
    }

    private boolean waypointNameNotProvided(Player player, String waypointName) {
        if (waypointName.isEmpty()) {
            player.sendMessage("You must provide name of the waypoint that you want to teleport to.");
            return true;
        }
        return false;
    }

    private void wrongCommandProvidedMessage(Player player) {
        player.sendMessage(ChatUtils.asRedMessage("Wrong command provided!\n"));
    }


    private void showPlayerHisWaypoints(Player player) {
        List<PlayerWaypoint> ownedByPlayerWaypoints = waypointManagementService.getWaypointsOwnedByPlayer(player);
        if (ownedByPlayerWaypoints.isEmpty()) {
            player.sendMessage("You don't have any waypoints.");
            return;
        }
        player.sendMessage("List of owned waypoints: ");
        for (PlayerWaypoint waypoint : ownedByPlayerWaypoints) {
            player.sendMessage(waypoint.asClickableColoredMessage());
        }
    }

    private void showAllWaypoints(Player player) {
        List<PlayerWaypoint> waypoints = plugin.getPlayerWaypoints();
        player.sendMessage("List of all waypoints: ");
        for (PlayerWaypoint waypoint : waypoints) {
            player.sendMessage(waypoint.toString());
        }
    }

    public enum WaypointCommandType {
        CREATE("create"),
        DELETE("delete"),
        MOVE("move"),
        SHOW("show"),
        SHOW_ALL("showAll"),
        WRONG("wrongCommand");

        private final String cmd;

        WaypointCommandType(String cmd) {
            this.cmd = cmd;
        }

        public String getCmd() {
            return cmd;
        }

        public boolean isProper() {
            return !this.equals(WRONG);
        }

        public static Optional<WaypointCommandType> get(String cmdProvided) {
            return Arrays.stream(WaypointCommandType.values())
                    .filter(val -> val.cmd.equals(cmdProvided))
                    .findFirst();
        }
    }
}
