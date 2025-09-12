package com.jerae.zephaire.particles.statics;

import com.jerae.zephaire.particles.Debuggable;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.managers.CollisionManager;
import com.jerae.zephaire.particles.managers.PerformanceManager;
import com.jerae.zephaire.particles.util.VectorUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class StaticStarParticleTask extends BukkitRunnable implements Debuggable {

    private final Location center;
    private final Particle particle;
    private final Object particleOptions;
    private final List<Location> particleLocations;
    private final ConditionManager conditionManager;
    private final boolean collisionEnabled;
    private final int points;
    private final double outerRadius;
    private final double innerRadius;
    private final double density;

    public StaticStarParticleTask(Location center, Particle particle, int points, double outerRadius, double innerRadius, double density, Object options, double pitch, double yaw, ConditionManager conditionManager, boolean collisionEnabled) {
        this.center = center;
        this.particle = particle;
        this.particleOptions = options;
        this.conditionManager = conditionManager;
        this.particleLocations = new ArrayList<>();
        this.collisionEnabled = collisionEnabled;
        this.points = points;
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
        this.density = density;

        int totalVertices = Math.max(2, points) * 2;
        Vector[] vertices = new Vector[totalVertices];

        // Pre-calculate all the vertices of the star.
        for (int i = 0; i < totalVertices; i++) {
            double angle = (i * Math.PI / points);
            double radius = (i % 2 == 0) ? outerRadius : innerRadius;
            vertices[i] = new Vector(Math.cos(angle) * radius, 0, Math.sin(angle) * radius);
            vertices[i] = VectorUtils.rotateVector(vertices[i], pitch, yaw);
        }

        // Pre-calculate all the particle locations along the star's outline.
        for (int i = 0; i < totalVertices; i++) {
            Vector start = vertices[i];
            Vector end = vertices[(i + 1) % totalVertices];
            addParticlesAlongLine(start, end, density);
        }
    }

    private void addParticlesAlongLine(Vector start, Vector end, double density) {
        Vector direction = end.clone().subtract(start);
        double length = direction.length();
        direction.normalize();

        for (double d = 0; d < length; d += (1.0 / density)) {
            Vector currentPoint = direction.clone().multiply(d).add(start);
            particleLocations.add(center.clone().add(currentPoint));
        }
    }

    @Override
    public void run() {
        if (!conditionManager.allConditionsMet(center) || !PerformanceManager.isPlayerNearby(center)) {
            return;
        }

        World world = center.getWorld();
        if (world == null) {
            return; // Safety check for the world.
        }

        for (Location loc : particleLocations) {
            if (collisionEnabled && CollisionManager.isColliding(loc)) {
                continue;
            }
            world.spawnParticle(particle, loc, 1, 0, 0, 0, 0, particleOptions);
        }
    }

    @Override
    public String getDebugInfo() {
        StringBuilder info = new StringBuilder();
        info.append(ChatColor.AQUA).append("Type: ").append(ChatColor.WHITE).append("STATIC").append("\n");
        info.append(ChatColor.AQUA).append("Shape: ").append(ChatColor.WHITE).append("STATIC_STAR").append("\n");
        info.append(ChatColor.AQUA).append("Center: ").append(ChatColor.WHITE).append(String.format("%.2f, %.2f, %.2f", center.getX(), center.getY(), center.getZ())).append("\n");
        info.append(ChatColor.AQUA).append("Points: ").append(ChatColor.WHITE).append(points).append("\n");
        info.append(ChatColor.AQUA).append("Radii: ").append(ChatColor.WHITE).append(String.format("Outer:%.1f, Inner:%.1f", outerRadius, innerRadius)).append("\n");
        info.append(ChatColor.AQUA).append("Density: ").append(ChatColor.WHITE).append(density).append("\n");
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
