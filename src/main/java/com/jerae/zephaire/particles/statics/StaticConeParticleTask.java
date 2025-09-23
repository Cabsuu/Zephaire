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
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class StaticConeParticleTask extends BukkitRunnable implements Debuggable {

    private final Location center;
    private final Particle particle;
    private final Object particleOptions;
    private final List<Location> particleLocations;
    private final ConditionManager conditionManager;
    private final boolean collisionEnabled;
    private final int despawnTimer;
    private final boolean hasGravity;

    public StaticConeParticleTask(Location center, Particle particle, double radius, double height, double density, Object particleOptions, ConditionManager conditionManager, boolean collisionEnabled, int despawnTimer, boolean hasGravity) {
        this.center = center;
        this.particle = particle;
        this.particleOptions = particleOptions;
        this.conditionManager = conditionManager;
        this.collisionEnabled = collisionEnabled;
        this.particleLocations = new ArrayList<>();
        this.despawnTimer = despawnTimer;
        this.hasGravity = hasGravity;

        for (double y = 0; y <= height; y += 1.0 / density) {
            double currentRadius = radius * (1 - (y / height));
            int pointsOnCircle = (int) (2 * Math.PI * currentRadius * density);
            for (int i = 0; i < pointsOnCircle; i++) {
                double angle = (2 * Math.PI * i) / pointsOnCircle;
                double x = currentRadius * Math.cos(angle);
                double z = currentRadius * Math.sin(angle);
                particleLocations.add(center.clone().add(x, y, z));
            }
        }
    }

    @Override
    public void run() {
        if (!PerformanceManager.isPlayerNearby(center) || !conditionManager.allConditionsMet(center)) {
            return;
        }

        World world = center.getWorld();
        if (world == null) return;

        for (Location loc : particleLocations) {
            if (collisionEnabled && CollisionManager.isColliding(loc)) {
                continue;
            }
            if (particle == null && particleOptions instanceof ItemStack) {
                ParticleScheduler.queueParticle(new ParticleSpawnData(loc, (ItemStack) particleOptions, despawnTimer, hasGravity));
            } else if (particle != null) {
                if (particle == Particle.SHRIEK && particleOptions instanceof Integer) {
                    ParticleScheduler.queueParticle(new ParticleSpawnData(particle, loc, (Integer) particleOptions));
                } else if (particle == Particle.VIBRATION && particleOptions instanceof org.bukkit.Vibration) {
                    ParticleScheduler.queueParticle(new ParticleSpawnData(particle, loc, (org.bukkit.Vibration) particleOptions));
                } else {
                    ParticleScheduler.queueParticle(new ParticleSpawnData(particle, loc, 1, 0, 0, 0, 0, particleOptions));
                }
            }
        }
    }

    @Override
    public String getDebugInfo() {
        return ChatColor.AQUA + "Type: " + ChatColor.WHITE + "STATIC" + "\n" +
                ChatColor.AQUA + "Shape: " + ChatColor.WHITE + "STATIC_CONE" + "\n" +
                ChatColor.AQUA + "Center: " + ChatColor.WHITE + String.format("%.2f, %.2f, %.2f", center.getX(), center.getY(), center.getZ()) + "\n" +
                ChatColor.DARK_AQUA + "--- Status ---" + "\n" +
                ChatColor.AQUA + "Player Nearby: " + ParticleUtils.formatBoolean(PerformanceManager.isPlayerNearby(center)) + "\n" +
                ChatColor.AQUA + "Conditions Met: " + ParticleUtils.formatBoolean(conditionManager.allConditionsMet(center)) + "\n" +
                ChatColor.AQUA + "Collision Enabled: " + ParticleUtils.formatBoolean(collisionEnabled);
    }
}
