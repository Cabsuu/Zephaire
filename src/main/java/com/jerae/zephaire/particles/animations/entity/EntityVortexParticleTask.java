package com.jerae.zephaire.particles.animations.entity;

import com.jerae.zephaire.particles.ParticleScheduler;
import com.jerae.zephaire.particles.ParticleSpawnData;
import com.jerae.zephaire.particles.managers.CollisionManager;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.data.EntityTarget;
import com.jerae.zephaire.particles.managers.PerformanceManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class EntityVortexParticleTask implements EntityParticleTask {
    private final String effectName;
    private final Particle particle;
    private final Object options;
    private final ConditionManager conditionManager;
    private final boolean collisionEnabled;
    private final Vector offset;
    private final EntityTarget target;

    private final double radius;
    private final double height;
    private final double speed;
    private final int particleCount;

    private final List<Location> particles = new ArrayList<>();
    private final List<Vector> velocities = new ArrayList<>();

    private final Vector toCenter = new Vector();
    private final Vector rotational = new Vector();


    public EntityVortexParticleTask(String effectName, Particle particle, double radius, double height, double speed, int particleCount, Object options, ConditionManager conditionManager, boolean collisionEnabled, Vector offset, EntityTarget target) {
        this.effectName = effectName;
        this.particle = particle;
        this.radius = radius;
        this.height = height;
        this.speed = speed;
        this.particleCount = particleCount;
        this.options = options;
        this.conditionManager = conditionManager;
        this.collisionEnabled = collisionEnabled;
        this.offset = offset;
        this.target = target;
    }

    @Override
    public EntityParticleTask newInstance() {
        return new EntityVortexParticleTask(effectName, particle, radius, height, speed, particleCount, options, conditionManager, collisionEnabled, offset, target);
    }

    @Override
    public EntityTarget getTarget() {
        return target;
    }

    @Override
    public void tick(Entity entity) {
        Location center = entity.getLocation().add(offset);

        // Initialize particles if this is the first tick for this instance
        if (particles.isEmpty()) {
            for (int i = 0; i < particleCount; i++) {
                particles.add(getRandomLocationInVortex(center));
                velocities.add(new Vector(0, 0, 0));
            }
        }

        for (int i = 0; i < particleCount; i++) {
            Location p = particles.get(i);
            Vector v = velocities.get(i);

            toCenter.setX(center.getX() - p.getX());
            toCenter.setY(0);
            toCenter.setZ(center.getZ() - p.getZ());
            double distanceToCenter = toCenter.length();

            v.add(toCenter.normalize().multiply(distanceToCenter * 0.01));
            v.setY(v.getY() + (radius - distanceToCenter) * 0.01);

            rotational.setX(-toCenter.getZ());
            rotational.setY(0);
            rotational.setZ(toCenter.getX());
            rotational.normalize().multiply(speed);
            v.add(rotational);

            p.add(v.multiply(0.8));

            if (p.getY() > center.getY() + height || distanceToCenter > radius) {
                setRandomLocationInVortex(p, center);
                v.zero();
            }

            if (collisionEnabled && CollisionManager.isColliding(p)) {
                continue;
            }
            ParticleScheduler.queueParticle(new ParticleSpawnData(particle, p, 1, 0, 0, 0, 0, options));
        }
    }

    private void setRandomLocationInVortex(Location locToSet, Location center) {
        double angle = ThreadLocalRandom.current().nextDouble(0, 2 * Math.PI);
        double r = ThreadLocalRandom.current().nextDouble(0, radius);
        double x = center.getX() + r * Math.cos(angle);
        double z = center.getZ() + r * Math.sin(angle);
        double y = center.getY() + ThreadLocalRandom.current().nextDouble(0, height * 0.2);
        locToSet.setX(x);
        locToSet.setY(y);
        locToSet.setZ(z);
    }

    private Location getRandomLocationInVortex(Location center) {
        double angle = ThreadLocalRandom.current().nextDouble(0, 2 * Math.PI);
        double r = ThreadLocalRandom.current().nextDouble(0, radius);
        double x = center.getX() + r * Math.cos(angle);
        double z = center.getZ() + r * Math.sin(angle);
        double y = center.getY() + ThreadLocalRandom.current().nextDouble(0, height * 0.2);
        return new Location(center.getWorld(), x, y, z);
    }

    @Override
    public boolean shouldCollide() {
        return collisionEnabled;
    }

    @Override
    public String getEffectName() {
        return effectName;
    }

    @Override
    public String getDebugInfo() {
        return ChatColor.AQUA + "Type: " + ChatColor.WHITE + "ENTITY_ANIMATED" + "\n" +
                ChatColor.AQUA + "Shape: " + ChatColor.WHITE + "VORTEX" + "\n" +
                ChatColor.AQUA + "Radius: " + ChatColor.WHITE + radius + "\n" +
                ChatColor.AQUA + "Height: " + ChatColor.WHITE + height + "\n" +
                ChatColor.AQUA + "Speed: " + ChatColor.WHITE + speed + "\n" +
                ChatColor.AQUA + "Target: " + ChatColor.WHITE + target.getTargetType().name() +
                (target.getName() != null ? " (" + target.getName() + ")" : "") +
                (target.getEntityType() != null ? " (" + target.getEntityType().name() + ")" : "");
    }
}

