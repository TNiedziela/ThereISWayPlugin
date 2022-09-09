package com.tomo.thereisway;

import com.tomo.thereisway.commands.CreateWaypointCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class ThereISWay extends JavaPlugin {

    @Override
    public void onEnable() {
        new CreateWaypointCommand(this);
        getLogger().info("There Is Way plugin has been enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("There Is Way plugin has been disabled");
    }
}
