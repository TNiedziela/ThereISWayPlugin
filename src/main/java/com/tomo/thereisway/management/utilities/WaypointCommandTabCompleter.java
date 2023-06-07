package com.tomo.thereisway.management.utilities;

import com.tomo.thereisway.management.commands.WaypointCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WaypointCommandTabCompleter implements TabCompleter {

    public WaypointCommandTabCompleter() {}

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> commandParams = List.of(args);
        if (commandParams.size() != 1) {
            return Collections.emptyList();
        }
        return getCommandsThatStartWith(commandParams.get(0));
    }

    private List<String> getCommandsThatStartWith(String firstLetters) {
        List<String> commandsToComplete = new ArrayList<>();
        for (WaypointCommand.WaypointCommandType command : WaypointCommand.WaypointCommandType.values()) {
            if (command.getCmd().startsWith(firstLetters) && command.isProper()) {
                commandsToComplete.add(command.getCmd());
            }
        }
        return commandsToComplete;
    }
}
