package com.jerae.zephaire.particles.animations;

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
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class StarParticleTask implements AnimatedParticle {

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
    private final boolean collisionEnabled;
    private final int despawnTimer;
    private final boolean hasGravity;
    private final int loopDelay;

    private double rotationAngle = 0;
    private int loopDelayCounter = 0;

    // --- PERFORMANCE: Reusable objects to avoid creating new ones every tick ---
    private final Vector[] vertices;
    private final Vector reusableVertex = new Vector();
    private final Vector lineDirection = new Vector();
    private final Vector currentLinePoint = new Vector();
    private final Location particleLoc;
    private final Vector rotatedPos = new Vector();


    public StarParticleTask(Location center, Particle particle, int points, double outerRadius, double innerRadius, double speed, double density, Object options, double pitch, double yaw, ConditionManager conditionManager, boolean collisionEnabled, int despawnTimer, boolean hasGravity, int loopDelay) {
        this.center = center;
        this.particle = particle;
        this.points = Math.max(2, points); // A star must have at least 2 points
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
        this.speed = speed;
        this.density = density;
        this.options = options;
        this.pitch = pitch;
        this.yaw = yaw;
        this.conditionManager = conditionManager;
        this.collisionEnabled = collisionEnabled;
        this.despawnTimer = despawnTimer;
        this.hasGravity = hasGravity;
        this.loopDelay = loopDelay;
        this.vertices = new Vector[this.points * 2];
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = new Vector();
        }
        this.particleLoc = center.clone();
    }

    @Override
    public void tick() {
        if (!conditionManager.allConditionsMet(center) || !PerformanceManager.isPlayerNearby(center)) {
            return;
        }

        if (loopDelayCounter > 0) {
            loopDelayCounter--;
            return;
        }

        rotationAngle += speed;

        if (rotationAngle >= 2 * Math.PI) {
            rotationAngle = 0;
            loopDelayCounter = loopDelay;
        }

        int totalVertices = points * 2;

        // Calculate all the vertices of the star
        for (int i = 0; i < totalVertices; i++) {
            double angle = rotationAngle + (i * Math.PI / points);
            // Alternate between outer and inner radius for each vertex
            double radius = (i % 2 == 0) ? outerRadius : innerRadius;
            // --- PERFORMANCE: Use the reusable vector instead of creating a new one ---
            reusableVertex.setX(Math.cos(angle) * radius).setY(0).setZ(Math.sin(angle) * radius);
            VectorUtils.rotateVector(reusableVertex, pitch, yaw, vertices[i]);
        }

        // Draw lines between the vertices to form the star's outline
        for (int i = 0; i < totalVertices; i++) {
            Vector start = vertices[i];
            // Connect to the next vertex, wrapping around at the end
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

            if (collisionEnabled && CollisionManager.isColliding(particleLoc)) {
                continue;
            }
            if (particle == null && options instanceof ItemStack) {
                ParticleScheduler.queueParticle(new ParticleSpawnData(particleLoc, (ItemStack) options, despawnTimer, hasGravity));
            } else if (particle != null) {
                ParticleScheduler.queueParticle(new ParticleSpawnData(particle, particleLoc, 1, 0, 0, 0, 0, options));
            }
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
    public int getLoopDelayCounter() {
        return loopDelayCounter;
    }

    @Override
    public String getDebugInfo() {
        StringBuilder info = new StringBuilder();
        info.append(ChatColor.AQUA).append("Type: ").append(ChatColor.WHITE).append("ANIMATED").append("\n");
        info.append(ChatColor.AQUA).append("Shape: ").append(ChatColor.WHITE).append("STAR").append("\n");
        info.append(ChatColor.AQUA).append("Center: ").append(ChatColor.WHITE).append(String.format("%.2f, %.2f, %.2f", center.getX(), center.getY(), center.getZ())).append("\n");
        info.append(ChatColor.AQUA).append("Points: ").append(ChatColor.WHITE).append(points).append("\n");
        info.append(ChatColor.AQUA).append("Radii: ").append(ChatColor.WHITE).append(String.format("Outer:%.1f, Inner:%.1f", outerRadius, innerRadius)).append("\n");
        info.append(ChatColor.AQUA).append("Rotation Speed: ").append(ChatColor.WHITE).append(speed).append("\n");
        info.append(ChatColor.DARK_AQUA).append("--- Status ---").append("\n");
        info.append(ChatColor.AQUA).append("Player Nearby: ").append(ParticleUtils.formatBoolean(PerformanceManager.isPlayerNearby(center))).append("\n");
        info.append(ChatColor.AQUA).append("Conditions Met: ").append(ParticleUtils.formatBoolean(conditionManager.allConditionsMet(center))).append("\n");
        info.append(ChatColor.AQUA).append("Collision Enabled: ").append(ParticleUtils.formatBoolean(collisionEnabled));

        return info.toString();
    }
}
