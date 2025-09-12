package com.jerae.zephaire.particles.statics;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.managers.CollisionManager;
import com.jerae.zephaire.particles.Debuggable;
import com.jerae.zephaire.particles.managers.PerformanceManager;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class StaticCurveParticleTask extends BukkitRunnable implements Debuggable {

    private final Location centerReference;
    private final Particle particle;
    private final Object particleOptions;
    private final List<Location> particleLocations;
    private final ConditionManager conditionManager;
    private final boolean collisionEnabled;
    private final Location start, control, end;
    private final double density;

    public StaticCurveParticleTask(Location start, Location control, Location end, Particle particle, double density, Object particleOptions, ConditionManager conditionManager, boolean collisionEnabled) {
        this.centerReference = start; // Use start as the reference for checks
        this.particle = particle;
        this.particleOptions = particleOptions;
        this.conditionManager = conditionManager;
        this.particleLocations = new ArrayList<>();
        this.collisionEnabled = collisionEnabled;
        this.start = start;
        this.control = control;
        this.end = end;
        this.density = density;

        Vector p0 = start.toVector();
        Vector p1 = control.toVector();
        Vector p2 = end.toVector();

        // Estimate the length to determine the number of steps
        double estimatedLength = start.distance(control) + control.distance(end);
        int steps = (int) (estimatedLength * density);

        // Pre-calculate all points on the curve
        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            double oneMinusT = 1.0 - t;

            // Quadratic Bézier curve formula: (1-t)^2 * P0 + 2(1-t)t * P1 + t^2 * P2
            Vector term1 = p0.clone().multiply(oneMinusT * oneMinusT);
            Vector term2 = p1.clone().multiply(2 * oneMinusT * t);
            Vector term3 = p2.clone().multiply(t * t);

            Vector pointOnCurve = term1.add(term2).add(term3);
            particleLocations.add(pointOnCurve.toLocation(start.getWorld()));
        }
    }

    @Override
    public void run() {
        if (!conditionManager.allConditionsMet(centerReference) || !PerformanceManager.isPlayerNearby(centerReference)) {
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Location loc : particleLocations) {
                    if (collisionEnabled && CollisionManager.isColliding(loc)) {
                        continue;
                    }
                centerReference.getWorld().spawnParticle(particle, loc, 1, 0, 0, 0, 0, particleOptions);
                }
            }
        }.runTask(JavaPlugin.getPlugin(Zephaire.class));
    }

    @Override
    public String getDebugInfo() {
        StringBuilder info = new StringBuilder();
        info.append(ChatColor.AQUA).append("Type: ").append(ChatColor.WHITE).append("STATIC").append("\n");
        info.append(ChatColor.AQUA).append("Shape: ").append(ChatColor.WHITE).append("STATIC_CURVE").append("\n");
        info.append(ChatColor.AQUA).append("Start: ").append(ChatColor.WHITE).append(String.format("%.2f, %.2f, %.2f", start.getX(), start.getY(), start.getZ())).append("\n");
        info.append(ChatColor.AQUA).append("Control: ").append(ChatColor.WHITE).append(String.format("%.2f, %.2f, %.2f", control.getX(), control.getY(), control.getZ())).append("\n");
        info.append(ChatColor.AQUA).append("End: ").append(ChatColor.WHITE).append(String.format("%.2f, %.2f, %.2f", end.getX(), end.getY(), end.getZ())).append("\n");
        info.append(ChatColor.AQUA).append("Density: ").append(ChatColor.WHITE).append(density).append("\n");
        info.append(ChatColor.DARK_AQUA).append("--- Status ---").append("\n");
        info.append(ChatColor.AQUA).append("Player Nearby: ").append(formatBoolean(PerformanceManager.isPlayerNearby(centerReference))).append("\n");
        info.append(ChatColor.AQUA).append("Conditions Met: ").append(formatBoolean(conditionManager.allConditionsMet(centerReference))).append("\n");
        info.append(ChatColor.AQUA).append("Collision Enabled: ").append(formatBoolean(collisionEnabled));

        return info.toString();
    }

    private String formatBoolean(boolean value) {
        return value ? ChatColor.GREEN + "true" : ChatColor.RED + "false";
    }
}
