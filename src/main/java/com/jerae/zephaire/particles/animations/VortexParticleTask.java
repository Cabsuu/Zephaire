package com.jerae.zephaire.particles.animations;

import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.managers.CollisionManager;
import com.jerae.zephaire.particles.managers.PerformanceManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class VortexParticleTask implements AnimatedParticle {

    private final Location center;
    private final Particle particle;
    private final Object options;
    private final ConditionManager conditionManager;
    private final boolean collisionEnabled;
    private final World world;

    private final double radius;
    private final double height;
    private final double speed;
    private final int particleCount;

    private final List<Location> particles = new ArrayList<>();
    private final List<Vector> velocities = new ArrayList<>();

    public VortexParticleTask(Location center, Particle particle, double radius, double height, double speed, int particleCount, Object options, ConditionManager conditionManager, boolean collisionEnabled) {
        this.center = center;
        this.particle = particle;
        this.radius = radius;
        this.height = height;
        this.speed = speed;
        this.particleCount = particleCount;
        this.options = options;
        this.conditionManager = conditionManager;
        this.collisionEnabled = collisionEnabled;
        this.world = center.getWorld();

        // Initialize particles with random positions
        for (int i = 0; i < particleCount; i++) {
            particles.add(getRandomLocationInVortex());
            velocities.add(new Vector(0, 0, 0));
        }
    }

    @Override
    public void tick() {
        if (world == null || !PerformanceManager.isPlayerNearby(center) || !conditionManager.allConditionsMet(center)) {
            return;
        }

        for (int i = 0; i < particleCount; i++) {
            Location p = particles.get(i);
            Vector v = velocities.get(i);

            // Vector pointing from the particle to the vortex center line
            Vector toCenter = new Vector(center.getX() - p.getX(), 0, center.getZ() - p.getZ());
            double distanceToCenter = toCenter.length();

            // Gravity towards the center (stronger when further away)
            v.add(toCenter.normalize().multiply(distanceToCenter * 0.01));

            // Upward force (stronger at the center)
            v.setY(v.getY() + (radius - distanceToCenter) * 0.01);

            // Rotational force
            Vector rotational = new Vector(-toCenter.getZ(), 0, toCenter.getX()).normalize().multiply(speed);
            v.add(rotational);

            // Apply velocity and some damping
            p.add(v.multiply(0.8));

            // Reset particles that go too high or too far
            if (p.getY() > center.getY() + height || distanceToCenter > radius) {
                Location newLocation = getRandomLocationInVortex();
                p.setX(newLocation.getX());
                p.setY(newLocation.getY());
                p.setZ(newLocation.getZ());
                v.zero();
            }

            if (collisionEnabled && CollisionManager.isColliding(p)) {
                continue;
            }
            world.spawnParticle(particle, p, 1, 0, 0, 0, 0, options);
        }
    }

    private Location getRandomLocationInVortex() {
        double angle = ThreadLocalRandom.current().nextDouble(0, 2 * Math.PI);
        double r = ThreadLocalRandom.current().nextDouble(0, radius);
        double x = center.getX() + r * Math.cos(angle);
        double z = center.getZ() + r * Math.sin(angle);
        double y = center.getY() + ThreadLocalRandom.current().nextDouble(0, height * 0.2);
        return new Location(world, x, y, z);
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
        return ChatColor.AQUA + "Type: " + ChatColor.WHITE + "ANIMATED" + "\n" +
                ChatColor.AQUA + "Shape: " + ChatColor.WHITE + "VORTEX" + "\n" +
                ChatColor.AQUA + "Center: " + ChatColor.WHITE + String.format("%.2f, %.2f, %.2f", center.getX(), center.getY(), center.getZ()) + "\n" +
                ChatColor.AQUA + "Radius: " + ChatColor.WHITE + radius + "\n" +
                ChatColor.AQUA + "Height: " + ChatColor.WHITE + height + "\n" +
                ChatColor.AQUA + "Speed: " + ChatColor.WHITE + speed + "\n" +
                ChatColor.DARK_AQUA + "--- Status ---" + "\n" +
                ChatColor.AQUA + "Player Nearby: " + formatBoolean(PerformanceManager.isPlayerNearby(center)) + "\n" +
                ChatColor.AQUA + "Conditions Met: " + formatBoolean(conditionManager.allConditionsMet(center)) + "\n" +
                ChatColor.AQUA + "Collision Enabled: " + formatBoolean(collisionEnabled);
    }

    private String formatBoolean(boolean value) {
        return value ? ChatColor.GREEN + "true" : ChatColor.RED + "false";
    }
}
