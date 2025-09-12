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

public class StaticPyramidParticleTask extends BukkitRunnable implements Debuggable {

    private final Location center;
    private final Particle particle;
    private final Object particleOptions;
    private final List<Location> particleLocations;
    private final ConditionManager conditionManager;
    private final boolean collisionEnabled;
    private final double baseSize;
    private final double height;
    private final int sides;

    public StaticPyramidParticleTask(Location center, Particle particle, double baseSize, double height, int sides, double density, Object particleOptions, double pitch, double yaw, ConditionManager conditionManager, boolean collisionEnabled) {
        this.center = center;
        this.particle = particle;
        this.particleOptions = particleOptions;
        this.conditionManager = conditionManager;
        this.collisionEnabled = collisionEnabled;
        this.baseSize = baseSize;
        this.height = height;
        this.sides = sides;
        this.particleLocations = new ArrayList<>();

        // The apex of the pyramid is at the top center
        Vector apex = new Vector(0, height, 0);

        // Calculate the vertices of the base polygon
        Vector[] baseVertices = new Vector[sides];
        for (int i = 0; i < sides; i++) {
            double angle = (2 * Math.PI * i) / sides;
            double x = baseSize * Math.cos(angle);
            double z = baseSize * Math.sin(angle);
            baseVertices[i] = new Vector(x, 0, z);
        }

        // Rotate all points (apex and base)
        apex = VectorUtils.rotateVector(apex, pitch, yaw);
        for (int i = 0; i < sides; i++) {
            baseVertices[i] = VectorUtils.rotateVector(baseVertices[i], pitch, yaw);
        }

        // Create the edges of the pyramid base and the edges from the base to the apex
        for (int i = 0; i < sides; i++) {
            Vector start = baseVertices[i];
            Vector end = baseVertices[(i + 1) % sides]; // Next vertex in the base

            // Edge of the base
            addParticlesAlongLine(start, end, density);
            // Edge from base vertex to the apex
            addParticlesAlongLine(start, apex, density);
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
        if (!PerformanceManager.isPlayerNearby(center) || !conditionManager.allConditionsMet(center)) {
            return;
        }

        World world = center.getWorld();
        if (world == null) return;

        for (Location loc : particleLocations) {
            if (collisionEnabled && CollisionManager.isColliding(loc)) {
                continue;
            }
            world.spawnParticle(particle, loc, 1, 0, 0, 0, 0, particleOptions);
        }
    }

    @Override
    public String getDebugInfo() {
        return ChatColor.AQUA + "Type: " + ChatColor.WHITE + "STATIC" + "\n" +
                ChatColor.AQUA + "Shape: " + ChatColor.WHITE + "STATIC_PYRAMID" + "\n" +
                ChatColor.AQUA + "Center: " + ChatColor.WHITE + String.format("%.2f, %.2f, %.2f", center.getX(), center.getY(), center.getZ()) + "\n" +
                ChatColor.AQUA + "Base Size: " + ChatColor.WHITE + baseSize + "\n" +
                ChatColor.AQUA + "Height: " + ChatColor.WHITE + height + "\n" +
                ChatColor.AQUA + "Sides: " + ChatColor.WHITE + sides + "\n" +
                ChatColor.DARK_AQUA + "--- Status ---" + "\n" +
                ChatColor.AQUA + "Player Nearby: " + formatBoolean(PerformanceManager.isPlayerNearby(center)) + "\n" +
                ChatColor.AQUA + "Conditions Met: " + formatBoolean(conditionManager.allConditionsMet(center)) + "\n" +
                ChatColor.AQUA + "Collision Enabled: " + formatBoolean(collisionEnabled);
    }

    private String formatBoolean(boolean value) {
        return value ? ChatColor.GREEN + "true" : ChatColor.RED + "false";
    }
}
