package com.tomo.thereisway.waypoints;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum WaypointEffect {
    ENDER_CRYSTAL("enderCrystalEffect");

    private final String effectName;

    WaypointEffect(String effectName) {
        this.effectName = effectName;
    }

    public String getEffectName() {
        return effectName;
    }

    public static Optional<WaypointEffect> get(String effectName) {
        return Arrays.stream(WaypointEffect.values())
                .filter(val -> val.effectName.equals(effectName))
                .findFirst();
    }

    public static Map<WaypointEffect, Boolean> getDefaultEffectsMap() {
        Map<WaypointEffect, Boolean> result = new HashMap<>();
        for (WaypointEffect effect : WaypointEffect.values()) {
            result.put(effect, false);
        }
        return result;
    }
}
