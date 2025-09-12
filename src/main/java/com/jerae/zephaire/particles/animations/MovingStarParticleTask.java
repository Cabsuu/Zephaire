package com.jerae.zephaire.particles.animations;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.CollisionManager;
import com.jerae.zephaire.particles.Debuggable;
import com.jerae.zephaire.particles.PerformanceManager;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.util.VectorUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class MovingStarParticleTask implements AnimatedParticle {

    private final Location center;
    private final Particle particle;
    private final int points;
    private final double outerRadius;
    private final double innerRadius;
    private final double speed;
    private final double density;
    private final Object options;
    private final double pitch;
    private final double yaw;
    private final ConditionManager conditionManager;
    private final Vector velocity;
    private final boolean collisionEnabled;

    private double rotationAngle = 0;

    // --- PERFORMANCE: Reusable objects to avoid creating new ones every tick ---
    private final Vector[] vertices;
    private final Vector reusableVertex = new Vector();
    private final Vector lineDirection = new Vector();
    private final Vector currentLinePoint = new Vector();
    private final Location particleLoc;

    public MovingStarParticleTask(Location center, Particle particle, int points, double outerRadius, double innerRadius, double speed, double density, Object options, double pitch, double yaw, ConditionManager conditionManager, Vector velocity, boolean collisionEnabled) {
        this.center = center;
        this.particle = particle;
        this.points = Math.max(2, points);
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
        this.speed = speed;
        this.density = density;
        this.options = options;
        this.pitch = pitch;
        this.yaw = yaw;
        this.conditionManager = conditionManager;
        this.velocity = velocity;
        this.collisionEnabled = collisionEnabled;
        this.vertices = new Vector[this.points * 2];
        this.particleLoc = center.clone();
    }

    @Override
    public void tick() {
        if (!conditionManager.allConditionsMet(center) || !PerformanceManager.isPlayerNearby(center)) {
            return;
        }

        // Check for collision at the center before moving
        if (collisionEnabled && CollisionManager.isColliding(center.clone().add(velocity))) {
            return;
        }

        center.add(velocity);
        rotationAngle += speed;
        drawStar();
    }

    private void drawStar() {
        int totalVertices = points * 2;

        for (int i = 0; i < totalVertices; i++) {
            double angle = rotationAngle + (i * Math.PI / points);
            double radius = (i % 2 == 0) ? outerRadius : innerRadius;
            // --- PERFORMANCE: Use the reusable vector instead of creating a new one ---
            reusableVertex.setX(Math.cos(angle) * radius).setY(0).setZ(Math.sin(angle) * radius);
            vertices[i] = VectorUtils.rotateVector(reusableVertex, pitch, yaw);
        }

        for (int i = 0; i < totalVertices; i++) {
            Vector start = vertices[i];
            Vector end = vertices[(i + 1) % totalVertices];
            drawParticleLine(start, end);
        }
    }

    private void drawParticleLine(Vector start, Vector end) {
        // --- PERFORMANCE: Reuse the lineDirection vector ---
        lineDirection.copy(end).subtract(start);
        double length = lineDirection.length();
        lineDirection.normalize();

        for (double d = 0; d < length; d += (1.0 / density)) {
            // --- PERFORMANCE: Reuse the currentLinePoint vector and particleLoc location ---
            currentLinePoint.copy(lineDirection).multiply(d).add(start);
            particleLoc.setX(center.getX() + currentLinePoint.getX());
            particleLoc.setY(center.getY() + currentLinePoint.getY());
            particleLoc.setZ(center.getZ() + currentLinePoint.getZ());
            new BukkitRunnable() {
                @Override
                public void run() {
            center.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0, options);
                }
            }.runTask(JavaPlugin.getPlugin(Zephaire.class));
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
    public String getDebugInfo() {
        StringBuilder info = new StringBuilder();
        info.append(ChatColor.AQUA).append("Type: ").append(ChatColor.WHITE).append("ANIMATED").append("\n");
        info.append(ChatColor.AQUA).append("Shape: ").append(ChatColor.WHITE).append("MOVING_STAR").append("\n");
        info.append(ChatColor.AQUA).append("Center: ").append(ChatColor.WHITE).append(String.format("%.2f, %.2f, %.2f", center.getX(), center.getY(), center.getZ())).append("\n");
        info.append(ChatColor.AQUA).append("Velocity: ").append(ChatColor.WHITE).append(String.format("x:%.2f, y:%.2f, z:%.2f", velocity.getX(), velocity.getY(), velocity.getZ())).append("\n");
        info.append(ChatColor.AQUA).append("Points: ").append(ChatColor.WHITE).append(points).append("\n");
        info.append(ChatColor.AQUA).append("Radii: ").append(ChatColor.WHITE).append(String.format("Outer:%.1f, Inner:%.1f", outerRadius, innerRadius)).append("\n");
        info.append(ChatColor.AQUA).append("Rotation Speed: ").append(ChatColor.WHITE).append(speed).append("\n");
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

