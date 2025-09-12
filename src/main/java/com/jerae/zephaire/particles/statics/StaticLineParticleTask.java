package com.jerae.zephaire.particles.statics;

import com.jerae.zephaire.particles.Debuggable;
import com.jerae.zephaire.particles.ParticleScheduler;
import com.jerae.zephaire.particles.ParticleSpawnData;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.managers.CollisionManager;
import com.jerae.zephaire.particles.managers.PerformanceManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class StaticLineParticleTask extends BukkitRunnable implements Debuggable {

    private final Location start;
    private final Particle particle;
    private final Object particleOptions;
    private final List<Location> particleLocations;
    private final ConditionManager conditionManager;
    private final boolean collisionEnabled;
    private final double density;
    private final Location end;

    public StaticLineParticleTask(Location start, Location end, Particle particle, double density, Object particleOptions, ConditionManager conditionManager, boolean collisionEnabled) {
        this.start = start;
        this.end = end;
        this.particle = particle;
        this.particleOptions = particleOptions;
        this.particleLocations = new ArrayList<>();
        this.conditionManager = conditionManager;
        this.collisionEnabled = collisionEnabled;
        this.density = density;

        Vector vector = end.toVector().subtract(start.toVector());
        double length = vector.length();
        vector.normalize();
        double effectiveDensity = Math.max(0.1, density);

        for (double d = 0; d < length; d += (1.0 / effectiveDensity)) {
            Vector currentPointVector = vector.clone().multiply(d);
            particleLocations.add(start.clone().add(currentPointVector));
        }
    }

    @Override
    public void run() {
        // Check performance and conditions first
        if (!PerformanceManager.isPlayerNearby(start) || !conditionManager.allConditionsMet(start)) {
            return;
        }

        World world = start.getWorld();
        if (world == null) return;

        for (Location loc : particleLocations) {
            if (collisionEnabled && CollisionManager.isColliding(loc)) {
                continue;
            }
            ParticleScheduler.queueParticle(new ParticleSpawnData(particle, loc, 1, 0, 0, 0, 0, particleOptions));
        }
    }

    @Override
    public String getDebugInfo() {
        StringBuilder info = new StringBuilder();
        info.append(ChatColor.AQUA).append("Type: ").append(ChatColor.WHITE).append("STATIC").append("\n");
        info.append(ChatColor.AQUA).append("Shape: ").append(ChatColor.WHITE).append("STATIC_LINE").append("\n");
        info.append(ChatColor.AQUA).append("Start: ").append(ChatColor.WHITE).append(String.format("%.2f, %.2f, %.2f", start.getX(), start.getY(), start.getZ())).append("\n");
        info.append(ChatColor.AQUA).append("End: ").append(ChatColor.WHITE).append(String.format("%.2f, %.2f, %.2f", end.getX(), end.getY(), end.getZ())).append("\n");
        info.append(ChatColor.AQUA).append("Density: ").append(ChatColor.WHITE).append(density).append("\n");
        info.append(ChatColor.DARK_AQUA).append("--- Status ---").append("\n");
        info.append(ChatColor.AQUA).append("Player Nearby: ").append(formatBoolean(PerformanceManager.isPlayerNearby(start))).append("\n");
        info.append(ChatColor.AQUA).append("Conditions Met: ").append(formatBoolean(conditionManager.allConditionsMet(start))).append("\n");
        info.append(ChatColor.AQUA).append("Collision Enabled: ").append(formatBoolean(collisionEnabled));

        return info.toString();
    }

    private String formatBoolean(boolean value) {
        return value ? ChatColor.GREEN + "true" : ChatColor.RED + "false";
    }
}
