package com.jerae.zephaire.particles.animations;

import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.managers.CollisionManager;
import com.jerae.zephaire.particles.managers.PerformanceManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class CurveParticleTask implements AnimatedParticle {

    private final Particle particle;
    private final Object options;
    private final ConditionManager conditionManager;
    private final Vector p0, p1, p2; // Start, Control, End points
    private final double speed;
    private final boolean bounce;
    private final World world;
    private final boolean collisionEnabled;

    private double t = 0.0;
    private int direction = 1;

    // --- PERFORMANCE: Reusable objects to avoid creating new ones every tick ---
    private final Location currentLocation;
    private final Vector term1 = new Vector();
    private final Vector term2 = new Vector();
    private final Vector term3 = new Vector();

    public CurveParticleTask(Location start, Location control, Location end, Particle particle, double speed, boolean bounce, Object options, ConditionManager conditionManager, boolean collisionEnabled) {
        this.world = start.getWorld();
        this.p0 = start.toVector();
        this.p1 = control.toVector();
        this.p2 = end.toVector();
        this.particle = particle;
        this.speed = speed;
        this.bounce = bounce;
        this.options = options;
        this.conditionManager = conditionManager;
        this.currentLocation = start.clone();
        this.collisionEnabled = collisionEnabled;
    }

    @Override
    public void tick() {
        if (!conditionManager.allConditionsMet(currentLocation) || !PerformanceManager.isPlayerNearby(currentLocation)) {
            return;
        }

        t += speed * direction;

        if (t >= 1.0) {
            t = 1.0;
            if (bounce) {
                direction = -1;
            } else {
                t = 0.0;
            }
        } else if (t <= 0.0) {
            t = 0.0;
            direction = 1;
        }

        // --- PERFORMANCE: Reuse vectors for calculation ---
        double oneMinusT = 1.0 - t;
        term1.copy(p0).multiply(oneMinusT * oneMinusT);
        term2.copy(p1).multiply(2 * oneMinusT * t);
        term3.copy(p2).multiply(t * t);

        Vector pointOnCurve = term1.add(term2).add(term3);
        currentLocation.setX(pointOnCurve.getX());
        currentLocation.setY(pointOnCurve.getY());
        currentLocation.setZ(pointOnCurve.getZ());


        if (collisionEnabled && CollisionManager.isColliding(currentLocation)) {
            return;
        }
        world.spawnParticle(particle, currentLocation, 1, 0, 0, 0, 0, options);
    }

    @Override
    public Location getCurrentLocation() {
        return currentLocation;
    }

    @Override
    public boolean shouldCollide() {
        return collisionEnabled;
    }

    @Override
    public String getDebugInfo() {
        StringBuilder info = new StringBuilder();
        info.append(ChatColor.AQUA).append("Type: ").append(ChatColor.WHITE).append("ANIMATED").append("\n");
        info.append(ChatColor.AQUA).append("Shape: ").append(ChatColor.WHITE).append("CURVE").append("\n");
        info.append(ChatColor.AQUA).append("Current Location: ").append(ChatColor.WHITE).append(String.format("%.2f, %.2f, %.2f", currentLocation.getX(), currentLocation.getY(), currentLocation.getZ())).append("\n");
        info.append(ChatColor.AQUA).append("Speed: ").append(ChatColor.WHITE).append(speed).append("\n");
        info.append(ChatColor.DARK_AQUA).append("--- Status ---").append("\n");
        info.append(ChatColor.AQUA).append("Player Nearby: ").append(formatBoolean(PerformanceManager.isPlayerNearby(currentLocation))).append("\n");
        info.append(ChatColor.AQUA).append("Conditions Met: ").append(formatBoolean(conditionManager.allConditionsMet(currentLocation))).append("\n");
        info.append(ChatColor.AQUA).append("Collision Enabled: ").append(formatBoolean(collisionEnabled));
        return info.toString();
    }

    private String formatBoolean(boolean value) {
        return value ? ChatColor.GREEN + "true" : ChatColor.RED + "false";
    }
}
