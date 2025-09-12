package com.jerae.zephaire.particles.animations;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.managers.CollisionManager;
import com.jerae.zephaire.particles.managers.PerformanceManager;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.util.VectorUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class CircleParticleTask implements AnimatedParticle {

    private final Location center;
    private final Particle particle;
    private final double radius;
    private final double speed;
    private final Object options;
    private final double pitch;
    private final double yaw;
    private final ConditionManager conditionManager;
    private final int particleCount;
    private final boolean collisionEnabled;
    private final World world;

    private double angle = 0;
    // --- PERFORMANCE: Reusable objects to avoid creating new ones every tick ---
    private final Location spawnLocation;
    private final Vector relativePos;

    public CircleParticleTask(Location center, Particle particle, double radius, double speed, int particleCount, Object options, double pitch, double yaw, ConditionManager conditionManager, boolean collisionEnabled) {
        this.center = center;
        this.particle = particle;
        this.radius = radius;
        this.speed = speed;
        this.particleCount = Math.max(1, particleCount);
        this.options = options;
        this.pitch = pitch;
        this.yaw = yaw;
        this.conditionManager = conditionManager;
        this.collisionEnabled = collisionEnabled;
        this.world = center.getWorld();
        // --- PERFORMANCE: Initialize reusable objects in the constructor ---
        this.spawnLocation = center.clone();
        this.relativePos = new Vector();
    }

    @Override
    public void tick() {
        if (world == null) {
            return;
        }

        if (!conditionManager.allConditionsMet(center) || !PerformanceManager.isPlayerNearby(center)) {
            return;
        }

        angle += speed;

        for (int i = 0; i < particleCount; i++) {
            double particleAngle = angle + (2 * Math.PI * i) / particleCount;

            // --- PERFORMANCE: Reuse the relativePos vector ---
            relativePos.setX(radius * Math.cos(particleAngle));
            relativePos.setY(0);
            relativePos.setZ(radius * Math.sin(particleAngle));

            Vector rotatedPos = VectorUtils.rotateVector(relativePos, pitch, yaw);

            // --- PERFORMANCE: Reuse the spawnLocation object ---
            spawnLocation.setX(center.getX() + rotatedPos.getX());
            spawnLocation.setY(center.getY() + rotatedPos.getY());
            spawnLocation.setZ(center.getZ() + rotatedPos.getZ());

            if (collisionEnabled && CollisionManager.isColliding(spawnLocation)) {
                continue;
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    world.spawnParticle(particle, spawnLocation, 1, 0, 0, 0, 0, options);
                }
            }.runTask(JavaPlugin.getPlugin(Zephaire.class));
        }
    }

    @Override
    public Location getCurrentLocation() {
        return center;
    }

    @Override
    public boolean shouldCollide() {
        return collisionEnabled;
    }

    @Override
    public String getDebugInfo() {
        StringBuilder info = new StringBuilder();
        info.append(ChatColor.AQUA).append("Type: ").append(ChatColor.WHITE).append("ANIMATED").append("\n");
        info.append(ChatColor.AQUA).append("Shape: ").append(ChatColor.WHITE).append("CIRCLE").append("\n");
        info.append(ChatColor.AQUA).append("Center: ").append(ChatColor.WHITE).append(String.format("%.2f, %.2f, %.2f", center.getX(), center.getY(), center.getZ())).append("\n");
        info.append(ChatColor.AQUA).append("Radius: ").append(ChatColor.WHITE).append(radius).append("\n");
        info.append(ChatColor.AQUA).append("Speed: ").append(ChatColor.WHITE).append(speed).append("\n");
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
