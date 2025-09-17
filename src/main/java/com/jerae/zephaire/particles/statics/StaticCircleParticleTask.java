package com.jerae.zephaire.particles.statics;

import com.jerae.zephaire.particles.Debuggable;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class StaticCircleParticleTask extends BukkitRunnable implements Debuggable {

    private final Location center;
    private final Particle particle;
    private final Object particleOptions;
    private final List<Location> particleLocations;
    private final ConditionManager conditionManager;
    private final boolean collisionEnabled;
    private final double radius;
    private final int particleCount;
    private final World world;
    private final int despawnTimer;

    public StaticCircleParticleTask(Location center, Particle particle, double radius, int particleCount, Object particleOptions, double pitch, double yaw, ConditionManager conditionManager, boolean collisionEnabled, int despawnTimer) {
        this.center = center;
        this.particle = particle;
        this.particleOptions = particleOptions;
        this.particleLocations = new ArrayList<>();
        this.conditionManager = conditionManager;
        this.collisionEnabled = collisionEnabled;
        this.radius = radius;
        this.particleCount = particleCount;
        this.world = center.getWorld();
        this.despawnTimer = despawnTimer;

        for (int i = 0; i < particleCount; i++) {
            double angle = (2 * Math.PI * i) / particleCount;
            // Base point on a flat XZ plane
            Vector point = new Vector(radius * Math.cos(angle), 0, radius * Math.sin(angle));

            // Apply rotations and assign the returned rotated vector back to the point
            point = VectorUtils.rotateVector(point, pitch, yaw);

            particleLocations.add(center.clone().add(point));
        }
    }

    @Override
    public void run() {
        // Use the centralized performance check.
        if (!PerformanceManager.isPlayerNearby(center) || !conditionManager.allConditionsMet(center)) {
            return;
        }

        if (world == null) {
            return; // Safety check for the world.
        }

        for (Location loc : particleLocations) {
            if (collisionEnabled && CollisionManager.isColliding(loc)) {
                continue;
            }
            if (particle == null && particleOptions instanceof ItemStack) {
                ParticleScheduler.queueParticle(new ParticleSpawnData(loc, (ItemStack) particleOptions, despawnTimer));
            } else if (particle != null) {
                ParticleScheduler.queueParticle(new ParticleSpawnData(particle, loc, 1, 0, 0, 0, 0, particleOptions));
            }
        }
    }

    @Override
    public String getDebugInfo() {
        StringBuilder info = new StringBuilder();
        info.append(ChatColor.AQUA).append("Type: ").append(ChatColor.WHITE).append("STATIC").append("\n");
        info.append(ChatColor.AQUA).append("Shape: ").append(ChatColor.WHITE).append("STATIC_CIRCLE").append("\n");
        info.append(ChatColor.AQUA).append("Center: ").append(ChatColor.WHITE).append(String.format("%.2f, %.2f, %.2f", center.getX(), center.getY(), center.getZ())).append("\n");
        info.append(ChatColor.AQUA).append("Radius: ").append(ChatColor.WHITE).append(radius).append("\n");
        info.append(ChatColor.AQUA).append("Particle Count: ").append(ChatColor.WHITE).append(particleCount).append("\n");
        info.append(ChatColor.DARK_AQUA).append("--- Status ---").append("\n");
        info.append(ChatColor.AQUA).append("Player Nearby: ").append(ParticleUtils.formatBoolean(PerformanceManager.isPlayerNearby(center))).append("\n");
        info.append(ChatColor.AQUA).append("Conditions Met: ").append(ParticleUtils.formatBoolean(conditionManager.allConditionsMet(center))).append("\n");
        info.append(ChatColor.AQUA).append("Collision Enabled: ").append(ParticleUtils.formatBoolean(collisionEnabled));

        return info.toString();
    }
}
