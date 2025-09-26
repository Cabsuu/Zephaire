package com.jerae.zephaire.particles.animations.entity;

import com.jerae.zephaire.particles.ParticleScheduler;
import com.jerae.zephaire.particles.ParticleSpawnData;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.data.EntityTarget;
import com.jerae.zephaire.particles.data.SpawnBehavior;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class OrbitalParticleTask implements EntityParticleTask {

    private final String effectName;
    private final Particle particle;
    private final int orbitingParticles;
    private final double radius;
    private final double speed;
    private final Object options;
    private final ConditionManager conditionManager;
    private final Vector offset;
    private final EntityTarget target;
    private final int period;
    private final SpawnBehavior spawnBehavior;
    private final int duration;
    private int ticksLived = 0;
    private double angle = 0;

    public OrbitalParticleTask(String effectName, Particle particle, int orbitingParticles, double radius, double speed, Object options, ConditionManager conditionManager, Vector offset, EntityTarget target, int period, SpawnBehavior spawnBehavior, int duration) {
        this.effectName = effectName;
        this.particle = particle;
        this.orbitingParticles = orbitingParticles;
        this.radius = radius;
        this.speed = speed;
        this.options = options;
        this.conditionManager = conditionManager;
        this.offset = offset;
        this.target = target;
        this.period = period;
        this.spawnBehavior = spawnBehavior;
        this.duration = duration;
    }

    @Override
    public void tick(Entity entity) {
        if (isDone()) {
            return;
        }
        ticksLived++;

        if (!conditionManager.allConditionsMet(entity.getLocation())) {
            return;
        }

        angle += speed;

        Location center = entity.getLocation().add(offset);

        for (int i = 0; i < orbitingParticles; i++) {
            double particleAngle = angle + (2 * Math.PI * i) / orbitingParticles;
            double x = center.getX() + radius * Math.cos(particleAngle);
            double z = center.getZ() + radius * Math.sin(particleAngle);
            Location spawnLoc = new Location(center.getWorld(), x, center.getY(), z);

            ParticleScheduler.queueParticle(new ParticleSpawnData(particle, spawnLoc, 1, 0, 0, 0, 0, options));
        }
    }

    @Override
    public boolean shouldCollide() {
        return false;
    }

    @Override
    public String getEffectName() {
        return effectName;
    }

    @Override
    public EntityTarget getTarget() {
        return target;
    }

    @Override
    public ConditionManager getConditionManager() {
        return conditionManager;
    }

    @Override
    public EntityParticleTask newInstance() {
        return new OrbitalParticleTask(effectName, particle, orbitingParticles, radius, speed, options, conditionManager, offset, target, period, spawnBehavior, duration);
    }

    @Override
    public boolean isDone() {
        return duration != -1 && ticksLived >= duration;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public SpawnBehavior getSpawnBehavior() {
        return spawnBehavior;
    }

    @Override
    public String getDebugInfo() {
        return "OrbitalParticleTask";
    }
}
