package com.jerae.zephaire.particles.animations;

import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.managers.CollisionManager;
import com.jerae.zephaire.particles.managers.PerformanceManager;
import com.jerae.zephaire.particles.util.VectorUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class PulsingCircleParticleTask implements AnimatedParticle {

    private final Location center;
    private final Particle particle;
    private final double maxRadius;
    private final double pulseSpeed;
    private final int particleCount;
    private final double pitch;
    private final double yaw;
    private final boolean expand;
    private final Object options;
    private final ConditionManager conditionManager;
    private final int period;
    private final boolean collisionEnabled;

    private double currentRadius;
    private int tickCounter = 0;

    // --- PERFORMANCE: Reusable objects to avoid creating new ones every tick ---
    private final Location currentLocation;
    private final Vector particleVector;


    public PulsingCircleParticleTask(Location center, Particle particle, double maxRadius, double pulseSpeed, int particleCount, double pitch, double yaw, boolean expand, Object options, ConditionManager conditionManager, int period, boolean collisionEnabled) {
        this.center = center;
        this.particle = particle;
        this.maxRadius = maxRadius;
        this.pulseSpeed = pulseSpeed;
        this.particleCount = particleCount;
        this.pitch = pitch;
        this.yaw = yaw;
        this.expand = expand;
        this.options = options;
        this.conditionManager = conditionManager;
        this.period = Math.max(1, period);
        this.collisionEnabled = collisionEnabled;

        // --- PERFORMANCE: Initialize reusable objects in the constructor ---
        this.currentLocation = center.clone();
        this.particleVector = new Vector();

        this.currentRadius = expand ? 0 : maxRadius;
    }

    @Override
    public void tick() {
        if (!conditionManager.allConditionsMet(center)) {
            return;
        }

        tickCounter++;
        if (tickCounter < period) {
            return;
        }
        tickCounter = 0;

        if (expand) {
            currentRadius += pulseSpeed;
            if (currentRadius >= maxRadius) {
                currentRadius = 0;
            }
        } else {
            currentRadius -= pulseSpeed;
            if (currentRadius <= 0) {
                currentRadius = maxRadius;
            }
        }

        for (int i = 0; i < particleCount; i++) {
            double angle = (2 * Math.PI * i) / particleCount;
            double xOffset = currentRadius * Math.cos(angle);
            double zOffset = currentRadius * Math.sin(angle);

            // --- PERFORMANCE: Reuse the particleVector object ---
            particleVector.setX(xOffset).setY(0).setZ(zOffset);
            Vector rotatedVector = VectorUtils.rotateVector(particleVector, pitch, yaw);

            // --- PERFORMANCE: Reuse the currentLocation object ---
            currentLocation.setX(center.getX() + rotatedVector.getX());
            currentLocation.setY(center.getY() + rotatedVector.getY());
            currentLocation.setZ(center.getZ() + rotatedVector.getZ());

            if (collisionEnabled && CollisionManager.isColliding(currentLocation)) {
                continue;
            }
            center.getWorld().spawnParticle(particle, currentLocation, 1, 0, 0, 0, 0, options);
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
        info.append(ChatColor.AQUA).append("Shape: ").append(ChatColor.WHITE).append("PULSING_CIRCLE").append("\n");
        info.append(ChatColor.AQUA).append("Center: ").append(ChatColor.WHITE).append(String.format("%.2f, %.2f, %.2f", center.getX(), center.getY(), center.getZ())).append("\n");
        info.append(ChatColor.AQUA).append("Max Radius: ").append(ChatColor.WHITE).append(maxRadius).append("\n");
        info.append(ChatColor.AQUA).append("Pulse Speed: ").append(ChatColor.WHITE).append(pulseSpeed).append("\n");
        info.append(ChatColor.AQUA).append("Direction: ").append(ChatColor.WHITE).append(expand ? "Expand" : "Shrink").append("\n");
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
