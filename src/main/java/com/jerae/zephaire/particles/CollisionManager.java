package com.jerae.zephaire.particles;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public final class CollisionManager {

    private static boolean globalCollisionEnabled = false;

    public static void initialize(FileConfiguration config) {
        if (config != null) {
            globalCollisionEnabled = config.getBoolean("performance.enable-collision-check", false);
        }
    }

    public static boolean shouldCollide(ConfigurationSection particleConfig) {
        if (particleConfig == null) {
            return globalCollisionEnabled;
        }
        // A particle's local setting can override the global one.
        return particleConfig.getBoolean("enable-collision", globalCollisionEnabled);
    }

    public static boolean isColliding(Location location) {
        if (location == null || location.getWorld() == null) {
            return false;
        }
        // Check if the block at the particle's location is solid.
        return location.getBlock().getType().isSolid();
    }
}
