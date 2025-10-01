package com.jerae.zephaire.particles.animations;

import com.jerae.zephaire.particles.ParticleScheduler;
import com.jerae.zephaire.particles.ParticleSpawnData;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.managers.CollisionManager;
import com.jerae.zephaire.particles.managers.PerformanceManager;
import com.jerae.zephaire.particles.util.ParticleUtils;
import com.jerae.zephaire.particles.util.VectorUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
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
    private final int despawnTimer;
    private final boolean hasGravity;
    private final int loopDelay;

    private double currentRadius;
    private int tickCounter = 0;
    private int loopDelayCounter = 0;

    // --- PERFORMANCE: Reusable objects to avoid creating new ones every tick ---
    private final Location currentLocation;
    private final Vector particleVector;
    private final Vector rotatedPos = new Vector();


    public PulsingCircleParticleTask(Location center, Particle particle, double maxRadius, double pulseSpeed, int particleCount, double pitch, double yaw, boolean expand, Object options, ConditionManager conditionManager, int period, boolean collisionEnabled, int despawnTimer, boolean hasGravity, int loopDelay) {
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
        this.despawnTimer = despawnTimer;
        this.hasGravity = hasGravity;
        this.loopDelay = loopDelay;

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

        if (loopDelayCounter > 0) {
            loopDelayCounter--;
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
                loopDelayCounter = loopDelay;
            }
        } else {
            currentRadius -= pulseSpeed;
            if (currentRadius <= 0) {
                currentRadius = maxRadius;
                loopDelayCounter = loopDelay;
            }
        }

        for (int i = 0; i < particleCount; i++) {
            double angle = (2 * Math.PI * i) / particleCount;
            double xOffset = currentRadius * Math.cos(angle);
            double zOffset = currentRadius * Math.sin(angle);

            // --- PERFORMANCE: Reuse the particleVector object ---
            particleVector.setX(xOffset).setY(0).setZ(zOffset);
            VectorUtils.rotateVector(particleVector, pitch, yaw, rotatedPos);

            // --- PERFORMANCE: Reuse the currentLocation object ---
            currentLocation.setX(center.getX() + rotatedPos.getX());
            currentLocation.setY(center.getY() + rotatedPos.getY());
            currentLocation.setZ(center.getZ() + rotatedPos.getZ());

            if (collisionEnabled && CollisionManager.isColliding(currentLocation)) {
                continue;
            }
            if (particle == null && options instanceof ItemStack) {
                ParticleScheduler.queueParticle(new ParticleSpawnData(currentLocation, (ItemStack) options, despawnTimer, hasGravity));
            } else if (particle != null) {
                if (particle == Particle.SHRIEK && options instanceof Integer) {
                    ParticleScheduler.queueParticle(new ParticleSpawnData(particle, currentLocation, (Integer) options));
                } else if (particle == Particle.VIBRATION && options instanceof org.bukkit.Vibration) {
                    ParticleScheduler.queueParticle(new ParticleSpawnData(particle, currentLocation, (org.bukkit.Vibration) options));
                } else if (particle == Particle.SCULK_CHARGE && options instanceof Float) {
                    ParticleScheduler.queueParticle(new ParticleSpawnData(particle, currentLocation, (Float) options));
                } else {
                    ParticleScheduler.queueParticle(new ParticleSpawnData(particle, currentLocation, 1, 0, 0, 0, 0, options));
                }
            }
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
    public int getLoopDelayCounter() {
        return loopDelayCounter;
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
        info.append(ChatColor.AQUA).append("Player Nearby: ").append(ParticleUtils.formatBoolean(PerformanceManager.isPlayerNearby(center))).append("\n");
        info.append(ChatColor.AQUA).append("Conditions Met: ").append(ParticleUtils.formatBoolean(conditionManager.allConditionsMet(center))).append("\n");
        info.append(ChatColor.AQUA).append("Collision Enabled: ").append(ParticleUtils.formatBoolean(collisionEnabled));

        return info.toString();
    }
}
