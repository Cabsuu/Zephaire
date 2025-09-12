package com.jerae.zephaire.particles.statics;

import com.jerae.zephaire.particles.Debuggable;
import com.jerae.zephaire.particles.ParticleScheduler;
import com.jerae.zephaire.particles.ParticleSpawnData;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.managers.CollisionManager;
import com.jerae.zephaire.particles.managers.PerformanceManager;
import com.jerae.zephaire.particles.util.ParticleUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

public class RandomBurstRegionParticleTask extends BukkitRunnable implements Debuggable {

    private final Particle particle;
    private final Object particleOptions;
    private final ConditionManager conditionManager;
    private final World world;
    private final double minX, minY, minZ;
    private final double maxX, maxY, maxZ;
    private final long activeDuration;
    private final long cooldownDuration;
    private final double burstRadius;
    private final int spawnRate;
    private final long spawnPeriod;
    private final boolean collisionEnabled;

    private long timer;
    private long spawnTickCounter = 0;
    private final Location currentBurstLocation; // Reusable location object
    private final Location spawnLoc; // Reusable location for spawning particles

    public RandomBurstRegionParticleTask(Location corner1, Location corner2, Particle particle, Object particleOptions, ConditionManager conditionManager, long activeDuration, long cooldownDuration, double burstRadius, int spawnRate, long spawnPeriod, boolean collisionEnabled) {
        this.particle = particle;
        this.particleOptions = particleOptions;
        this.conditionManager = conditionManager;
        this.world = corner1.getWorld();
        this.minX = Math.min(corner1.getX(), corner2.getX());
        this.minY = Math.min(corner1.getY(), corner2.getY());
        this.minZ = Math.min(corner1.getZ(), corner2.getZ());
        this.maxX = Math.max(corner1.getX(), corner2.getX());
        this.maxY = Math.max(corner1.getY(), corner2.getY());
        this.maxZ = Math.max(corner1.getZ(), corner2.getZ());
        this.activeDuration = activeDuration;
        this.cooldownDuration = cooldownDuration;
        this.burstRadius = burstRadius;
        this.spawnRate = spawnRate;
        this.spawnPeriod = Math.max(1, spawnPeriod);
        this.collisionEnabled = collisionEnabled;
        this.timer = 0;
        this.currentBurstLocation = new Location(world, 0, 0, 0);
        this.spawnLoc = new Location(world, 0, 0, 0);
        pickNewLocation();
    }

    private void pickNewLocation() {
        double x = ThreadLocalRandom.current().nextDouble(minX, maxX);
        double y = ThreadLocalRandom.current().nextDouble(minY, maxY);
        double z = ThreadLocalRandom.current().nextDouble(minZ, maxZ);
        this.currentBurstLocation.setX(x);
        this.currentBurstLocation.setY(y);
        this.currentBurstLocation.setZ(z);
    }

    @Override
    public void run() {
        timer++;

        if (timer <= activeDuration) {
            // Active phase
            if (!PerformanceManager.isPlayerNearby(currentBurstLocation) || !conditionManager.allConditionsMet(currentBurstLocation)) {
                return;
            }

            spawnTickCounter++;
            if (spawnTickCounter >= spawnPeriod) {
                spawnTickCounter = 0;

                for (int i = 0; i < spawnRate; i++) {
                    double offsetX = ThreadLocalRandom.current().nextDouble(-burstRadius, burstRadius);
                    double offsetY = ThreadLocalRandom.current().nextDouble(-burstRadius, burstRadius);
                    double offsetZ = ThreadLocalRandom.current().nextDouble(-burstRadius, burstRadius);

                    spawnLoc.setX(currentBurstLocation.getX() + offsetX);
                    spawnLoc.setY(currentBurstLocation.getY() + offsetY);
                    spawnLoc.setZ(currentBurstLocation.getZ() + offsetZ);

                    if (collisionEnabled && CollisionManager.isColliding(spawnLoc)) {
                        continue;
                    }

                    ParticleScheduler.queueParticle(new ParticleSpawnData(particle, spawnLoc, 1, 0, 0, 0, 0, particleOptions));
                }
            }
        } else if (timer > activeDuration + cooldownDuration) {
            // Cooldown finished, reset and pick a new location
            timer = 0;
            pickNewLocation();
        }
    }

    @Override
    public String getDebugInfo() {
        StringBuilder info = new StringBuilder();
        info.append(ChatColor.AQUA).append("Type: ").append(ChatColor.WHITE).append("STATIC").append("\n");
        info.append(ChatColor.AQUA).append("Shape: ").append(ChatColor.WHITE).append("RANDOM_BURST_REGION").append("\n");
        info.append(ChatColor.AQUA).append("Current Burst Location: ").append(ChatColor.WHITE).append(String.format("%.2f, %.2f, %.2f", currentBurstLocation.getX(), currentBurstLocation.getY(), currentBurstLocation.getZ())).append("\n");
        info.append(ChatColor.AQUA).append("Timer: ").append(ChatColor.WHITE).append(timer).append(" / ").append(activeDuration + cooldownDuration).append("\n");
        info.append(ChatColor.AQUA).append("State: ").append(ChatColor.WHITE).append(timer <= activeDuration ? "Active" : "Cooldown").append("\n");
        info.append(ChatColor.DARK_AQUA).append("--- Status ---").append("\n");
        info.append(ChatColor.AQUA).append("Player Nearby: ").append(ParticleUtils.formatBoolean(PerformanceManager.isPlayerNearby(currentBurstLocation))).append("\n");
        info.append(ChatColor.AQUA).append("Conditions Met: ").append(ParticleUtils.formatBoolean(conditionManager.allConditionsMet(currentBurstLocation))).append("\n");
        info.append(ChatColor.AQUA).append("Collision Enabled: ").append(ParticleUtils.formatBoolean(collisionEnabled));

        return info.toString();
    }
}
