package com.jerae.zephaire.particles.animations;

import com.jerae.zephaire.particles.ParticleScheduler;
import com.jerae.zephaire.particles.ParticleSpawnData;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.managers.CollisionManager;
import com.jerae.zephaire.particles.managers.PerformanceManager;
import com.jerae.zephaire.particles.util.ParticleDrawingUtils;
import com.jerae.zephaire.particles.util.ParticleUtils;
import com.jerae.zephaire.particles.util.VectorUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
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
    private final double height;
    private final double verticalSpeed;
    private final boolean bounce;

    private double rotationAngle = 0;
    private double currentYOffset = 0;
    private int verticalDirection = 1;

    // --- PERFORMANCE: Reusable objects to avoid creating new ones every tick ---
    private final Vector[] vertices;

    public MovingStarParticleTask(Location center, Particle particle, int points, double outerRadius, double innerRadius, double speed, double density, Object options, double pitch, double yaw, ConditionManager conditionManager, Vector velocity, boolean collisionEnabled, double height, double verticalSpeed, boolean bounce) {
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
        this.height = height;
        this.verticalSpeed = verticalSpeed;
        this.bounce = bounce;
        this.vertices = new Vector[this.points * 2];
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = new Vector();
        }
    }

    @Override
    public void tick() {
        if (!conditionManager.allConditionsMet(center) || !PerformanceManager.isPlayerNearby(center)) {
            return;
        }

        // Check for collision at the center's next position
        if (collisionEnabled && CollisionManager.isColliding(center.clone().add(velocity))) {
            return;
        }

        // Move the entire system
        center.add(velocity);
        rotationAngle += speed;

        // Handle vertical oscillation
        if (height != 0) {
            currentYOffset += verticalSpeed * verticalDirection;
            if (bounce) {
                if (currentYOffset >= height) {
                    currentYOffset = height;
                    verticalDirection = -1;
                } else if (currentYOffset <= 0) {
                    currentYOffset = 0;
                    verticalDirection = 1;
                }
            } else {
                if (currentYOffset >= height || currentYOffset < 0) { // Reset if it goes above or below (if verticalSpeed is negative)
                    currentYOffset = 0;
                }
            }
        }

        // --- Draw the star with the vertical offset ---
        int totalVertices = points * 2;
        Vector reusableVertex = new Vector();

        // Calculate all the vertices of the star with the vertical offset
        for (int i = 0; i < totalVertices; i++) {
            double angle = rotationAngle + (i * Math.PI / points);
            double radius = (i % 2 == 0) ? outerRadius : innerRadius;
            reusableVertex.setX(Math.cos(angle) * radius).setY(currentYOffset).setZ(Math.sin(angle) * radius);
            VectorUtils.rotateVector(reusableVertex, pitch, yaw, vertices[i]);
        }

        // Draw lines between the vertices
        for (int i = 0; i < totalVertices; i++) {
            Vector start = vertices[i];
            Vector end = vertices[(i + 1) % totalVertices];
            ParticleDrawingUtils.drawParticleLine(center, start, end, density, particle, options);
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
        info.append(ChatColor.AQUA).append("Height: ").append(ChatColor.WHITE).append(height).append("\n");
        info.append(ChatColor.DARK_AQUA).append("--- Status ---").append("\n");
        info.append(ChatColor.AQUA).append("Player Nearby: ").append(ParticleUtils.formatBoolean(PerformanceManager.isPlayerNearby(center))).append("\n");
        info.append(ChatColor.AQUA).append("Conditions Met: ").append(ParticleUtils.formatBoolean(conditionManager.allConditionsMet(center))).append("\n");
        info.append(ChatColor.AQUA).append("Collision Enabled: ").append(ParticleUtils.formatBoolean(collisionEnabled));

        return info.toString();
    }
}
