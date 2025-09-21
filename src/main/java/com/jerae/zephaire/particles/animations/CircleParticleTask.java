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
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
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
    private final int despawnTimer;
    private final boolean hasGravity;
    private final int loopDelay;

    private double angle = 0;
    private int loopDelayCounter = 0;
    // --- PERFORMANCE: Reusable objects to avoid creating new ones every tick ---
    private final Location spawnLocation;
    private final Vector relativePos;
    private final Vector rotatedPos = new Vector();

    public CircleParticleTask(Location center, Particle particle, double radius, double speed, int particleCount, Object options, double pitch, double yaw, ConditionManager conditionManager, boolean collisionEnabled, int despawnTimer, boolean hasGravity, int loopDelay) {
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
        this.despawnTimer = despawnTimer;
        this.hasGravity = hasGravity;
        this.loopDelay = loopDelay;
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

        if (loopDelayCounter > 0) {
            loopDelayCounter--;
            return;
        }

        angle += speed;

        if (angle >= 2 * Math.PI) {
            angle = 0;
            loopDelayCounter = loopDelay;
        }

        for (int i = 0; i < particleCount; i++) {
            double particleAngle = angle + (2 * Math.PI * i) / particleCount;

            // --- PERFORMANCE: Reuse the relativePos vector ---
            relativePos.setX(radius * Math.cos(particleAngle));
            relativePos.setY(0);
            relativePos.setZ(radius * Math.sin(particleAngle));

            VectorUtils.rotateVector(relativePos, pitch, yaw, rotatedPos);

            // --- PERFORMANCE: Reuse the spawnLocation object ---
            spawnLocation.setX(center.getX() + rotatedPos.getX());
            spawnLocation.setY(center.getY() + rotatedPos.getY());
            spawnLocation.setZ(center.getZ() + rotatedPos.getZ());

            if (collisionEnabled && CollisionManager.isColliding(spawnLocation)) {
                continue;
            }
            if (particle == null && options instanceof ItemStack) {
                ParticleScheduler.queueParticle(new ParticleSpawnData(spawnLocation, (ItemStack) options, despawnTimer, hasGravity));
            } else if (particle != null) {
                if (particle == Particle.SHRIEK && options instanceof Integer) {
                    ParticleScheduler.queueParticle(new ParticleSpawnData(particle, spawnLocation, (Integer) options));
                } else if (particle == Particle.VIBRATION && options instanceof org.bukkit.Vibration) {
                    ParticleScheduler.queueParticle(new ParticleSpawnData(particle, spawnLocation, (org.bukkit.Vibration) options));
                } else if (particle == Particle.SCULK_CHARGE && options instanceof Float) {
                    ParticleScheduler.queueParticle(new ParticleSpawnData(particle, spawnLocation, (Float) options));
                } else {
                    ParticleScheduler.queueParticle(new ParticleSpawnData(particle, spawnLocation, 1, 0, 0, 0, 0, options));
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
        info.append(ChatColor.AQUA).append("Shape: ").append(ChatColor.WHITE).append("CIRCLE").append("\n");
        info.append(ChatColor.AQUA).append("Center: ").append(ChatColor.WHITE).append(String.format("%.2f, %.2f, %.2f", center.getX(), center.getY(), center.getZ())).append("\n");
        info.append(ChatColor.AQUA).append("Radius: ").append(ChatColor.WHITE).append(radius).append("\n");
        info.append(ChatColor.AQUA).append("Speed: ").append(ChatColor.WHITE).append(speed).append("\n");
        info.append(ChatColor.DARK_AQUA).append("--- Status ---").append("\n");
        info.append(ChatColor.AQUA).append("Player Nearby: ").append(ParticleUtils.formatBoolean(PerformanceManager.isPlayerNearby(center))).append("\n");
        info.append(ChatColor.AQUA).append("Conditions Met: ").append(ParticleUtils.formatBoolean(conditionManager.allConditionsMet(center))).append("\n");
        info.append(ChatColor.AQUA).append("Collision Enabled: ").append(ParticleUtils.formatBoolean(collisionEnabled));

        return info.toString();
    }
}
