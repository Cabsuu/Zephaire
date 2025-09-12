package com.jerae.zephaire.particles.statics;

import com.jerae.zephaire.particles.Debuggable;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.managers.CollisionManager;
import com.jerae.zephaire.particles.managers.PerformanceManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

public class StaticRegionParticleTask extends BukkitRunnable implements Debuggable {

    private final Location corner1;
    private final Location corner2;
    private final Particle particle;
    private final int particleCount;
    private final Object particleOptions;
    private final double minX, minY, minZ;
    private final double maxX, maxY, maxZ;
    private final Location reusableLocation;
    private final ConditionManager conditionManager;
    private final boolean collisionEnabled;
    private final Location center;

    public StaticRegionParticleTask(Location corner1, Location corner2, Particle particle, int particleCount, Object particleOptions, ConditionManager conditionManager, boolean collisionEnabled) {
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.particle = particle;
        this.particleCount = particleCount;
        this.particleOptions = particleOptions;
        this.minX = Math.min(corner1.getX(), corner2.getX());
        this.minY = Math.min(corner1.getY(), corner2.getY());
        this.minZ = Math.min(corner1.getZ(), corner2.getZ());
        this.maxX = Math.max(corner1.getX(), corner2.getX());
        this.maxY = Math.max(corner1.getY(), corner2.getY());
        this.maxZ = Math.max(corner1.getZ(), corner2.getZ());
        this.reusableLocation = new Location(corner1.getWorld(), 0, 0, 0);
        this.conditionManager = conditionManager;
        this.collisionEnabled = collisionEnabled;
        // Pre-calculate the center of the region to avoid recalculating it on every run.
        this.center = new Location(corner1.getWorld(), (minX + maxX) / 2, (minY + maxY) / 2, (minZ + maxZ) / 2);
    }

    @Override
    public void run() {
        // Use the pre-calculated center for performance and condition checks.
        if (!PerformanceManager.isPlayerNearby(center) || !conditionManager.allConditionsMet(center)) {
            return;
        }
        World world = center.getWorld();
        if (world == null) {
            return; // Add a safety check for the world.
        }

        for (int i = 0; i < particleCount; i++) {
            double x = ThreadLocalRandom.current().nextDouble(minX, maxX);
            double y = ThreadLocalRandom.current().nextDouble(minY, maxY);
            double z = ThreadLocalRandom.current().nextDouble(minZ, maxZ);

            reusableLocation.setX(x);
            reusableLocation.setY(y);
            reusableLocation.setZ(z);

            if (collisionEnabled && CollisionManager.isColliding(reusableLocation)) {
                continue;
            }

            world.spawnParticle(particle, reusableLocation, 1, 0, 0, 0, 0, particleOptions);
        }
    }

    @Override
    public String getDebugInfo() {
        StringBuilder info = new StringBuilder();
        info.append(ChatColor.AQUA).append("Type: ").append(ChatColor.WHITE).append("STATIC").append("\n");
        info.append(ChatColor.AQUA).append("Shape: ").append(ChatColor.WHITE).append("STATIC_REGION").append("\n");
        info.append(ChatColor.AQUA).append("Corner 1: ").append(ChatColor.WHITE).append(String.format("%.2f, %.2f, %.2f", corner1.getX(), corner1.getY(), corner1.getZ())).append("\n");
        info.append(ChatColor.AQUA).append("Corner 2: ").append(ChatColor.WHITE).append(String.format("%.2f, %.2f, %.2f", corner2.getX(), corner2.getY(), corner2.getZ())).append("\n");
        info.append(ChatColor.AQUA).append("Particle Count: ").append(ChatColor.WHITE).append(particleCount).append("\n");
        info.append(ChatColor.DARK_AQUA).append("--- Status ---").append("\n");
        info.append(ChatColor.AQUA).append("Player Nearby: ").append(formatBoolean(PerformanceManager.isPlayerNearby(center))).append("\n");
        info.append(ChatColor.AQUA).append("Conditions Met: ").append(formatBoolean(conditionManager.allConditionsMet(center))).append("\n");
        info.append(ChatColor.AQUA).append("Collision Enabled: ").append(formatBoolean(collisionEnabled));

        return info.toString();
    }

    private String formatBoolean(boolean value) {
        return value ? ChatColor.GREEN + "true" : ChatColor.RED + "false";
    }
}
