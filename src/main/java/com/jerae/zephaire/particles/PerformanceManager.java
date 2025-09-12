package com.jerae.zephaire.particles;

import com.jerae.zephaire.Zephaire;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A utility class to manage and centralize performance-related checks for particles.
 * This class is now thread-safe.
 */
public final class PerformanceManager {

    private static boolean distanceCheckEnabled;
    private static double maxViewDistanceSquared;
    // A thread-safe map to cache player locations.
    private static final Map<UUID, Location> playerLocations = new ConcurrentHashMap<>();
    private static int locationUpdateTaskId = -1;

    /**
     * Loads performance settings and starts the player location caching task.
     *
     * @param config The FileConfiguration to load from.
     */
    public static void initialize(FileConfiguration config) {
        distanceCheckEnabled = config.getBoolean("performance.enable-distance-check", true);
        double maxViewDistance = config.getDouble("performance.max-view-distance", 64.0);
        maxViewDistanceSquared = maxViewDistance * maxViewDistance;

        // Cancel any existing task to prevent duplicates on reload
        if (locationUpdateTaskId != -1) {
            Bukkit.getScheduler().cancelTask(locationUpdateTaskId);
        }

        // Start a synchronous repeating task to update player locations every 5 ticks.
        // This is safe because it runs on the main server thread.
        locationUpdateTaskId = Bukkit.getScheduler().runTaskTimer(JavaPlugin.getPlugin(Zephaire.class), () -> {
            playerLocations.clear();
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerLocations.put(player.getUniqueId(), player.getLocation());
            }
        }, 0L, 5L).getTaskId();

        CollisionManager.initialize(config);
    }

    /**
     * Checks if any player is within the configured maximum distance of a location.
     * This method is now thread-safe and can be called from any thread.
     *
     * @param location The center location of the particle effect.
     * @return True if a player is nearby, otherwise false.
     */
    public static boolean isPlayerNearby(Location location) {
        if (!distanceCheckEnabled) {
            // This check can be considered safe enough for async access, but be mindful on server shutdown.
            return location.isChunkLoaded();
        }

        // Iterate over the thread-safe cache of player locations.
        for (Location playerLoc : playerLocations.values()) {
            // Ensure the player is in the same world before doing distance calculations.
            if (playerLoc.getWorld().equals(location.getWorld())) {
                // Use distanceSquared for a massive performance gain.
                if (playerLoc.distanceSquared(location) <= maxViewDistanceSquared) {
                    return true;
                }
            }
        }

        // No players were found within the required distance.
        return false;
    }
}
