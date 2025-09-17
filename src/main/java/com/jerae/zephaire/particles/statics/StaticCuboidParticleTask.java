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

public class StaticCuboidParticleTask extends BukkitRunnable implements Debuggable {

    private final Location center;
    private final Object particleOptions;
    private final Particle particle;
    private final List<Location> particleLocations;
    private final ConditionManager conditionManager;
    private final boolean collisionEnabled;
    private final double width, height, depth;
    private final int despawnTimer;

    public StaticCuboidParticleTask(Location center, Particle particle, double width, double height, double depth, double density, Object particleOptions, double pitch, double yaw, ConditionManager conditionManager, boolean collisionEnabled, int despawnTimer) {
        this.center = center;
        this.particle = particle;
        this.particleOptions = particleOptions;
        this.particleLocations = new ArrayList<>();
        this.conditionManager = conditionManager;
        this.collisionEnabled = collisionEnabled;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.despawnTimer = despawnTimer;

        double halfWidth = width / 2.0;
        double halfHeight = height / 2.0;
        double halfDepth = depth / 2.0;

        // Define the 8 vertices of the cuboid relative to the center
        Vector[] vertices = {
                new Vector(-halfWidth, -halfHeight, -halfDepth),
                new Vector(halfWidth, -halfHeight, -halfDepth),
                new Vector(halfWidth, halfHeight, -halfDepth),
                new Vector(-halfWidth, halfHeight, -halfDepth),
                new Vector(-halfWidth, -halfHeight, halfDepth),
                new Vector(halfWidth, -halfHeight, halfDepth),
                new Vector(halfWidth, halfHeight, halfDepth),
                new Vector(-halfWidth, halfHeight, halfDepth)
        };

        // Rotate all vertices
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = VectorUtils.rotateVector(vertices[i], pitch, yaw);
        }

        // Define the 12 edges by connecting the vertices
        int[][] edges = {
                {0, 1}, {1, 2}, {2, 3}, {3, 0}, // Bottom face
                {4, 5}, {5, 6}, {6, 7}, {7, 4}, // Top face
                {0, 4}, {1, 5}, {2, 6}, {3, 7}  // Vertical edges
        };

        for (int[] edge : edges) {
            Vector start = vertices[edge[0]];
            Vector end = vertices[edge[1]];
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
        // Use the centralized performance and new condition checks
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
        info.append(ChatColor.AQUA).append("Shape: ").append(ChatColor.WHITE).append("CUBOID").append("\n");
        info.append(ChatColor.AQUA).append("Center: ").append(ChatColor.WHITE).append(String.format("%.2f, %.2f, %.2f", center.getX(), center.getY(), center.getZ())).append("\n");
        info.append(ChatColor.AQUA).append("Dimensions: ").append(ChatColor.WHITE).append(String.format("W:%.1f, H:%.1f, D:%.1f", width, height, depth)).append("\n");
        info.append(ChatColor.DARK_AQUA).append("--- Status ---").append("\n");
        info.append(ChatColor.AQUA).append("Player Nearby: ").append(ParticleUtils.formatBoolean(PerformanceManager.isPlayerNearby(center))).append("\n");
        info.append(ChatColor.AQUA).append("Conditions Met: ").append(ParticleUtils.formatBoolean(conditionManager.allConditionsMet(center))).append("\n");
        info.append(ChatColor.AQUA).append("Collision Enabled: ").append(ParticleUtils.formatBoolean(collisionEnabled));

        return info.toString();
    }
}
