package com.jerae.zephaire.particles.animations;

import com.jerae.zephaire.particles.ParticleScheduler;
import com.jerae.zephaire.particles.ParticleSpawnData;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import org.bukkit.Location;
import org.bukkit.Particle;

public class AnimatedOrbitalParticleTask implements AnimatedParticle {

    private final Location center;
    private final Particle particle;
    private final int orbitingParticles;
    private final double radius;
    private final double speed;
    private final Object options;
    private final ConditionManager conditionManager;
    private final int period;
    private double angle = 0;
    private int tickCounter = 0;

    public AnimatedOrbitalParticleTask(Location center, Particle particle, int orbitingParticles, double radius, double speed, Object options, ConditionManager conditionManager, int period) {
        this.center = center;
        this.particle = particle;
        this.orbitingParticles = orbitingParticles;
        this.radius = radius;
        this.speed = speed;
        this.options = options;
        this.conditionManager = conditionManager;
        this.period = period;
    }

    @Override
    public void tick() {
        if (!conditionManager.allConditionsMet(center)) {
            return;
        }

        tickCounter++;
        if (tickCounter < period) {
            return;
        }
        tickCounter = 0;

        angle += speed;

        for (int i = 0; i < orbitingParticles; i++) {
            double particleAngle = angle + (2 * Math.PI * i) / orbitingParticles;
            double x = center.getX() + radius * Math.cos(particleAngle);
            double z = center.getZ() + radius * Math.sin(particleAngle);
            Location spawnLoc = new Location(center.getWorld(), x, center.getY(), z);

            ParticleScheduler.queueParticle(new ParticleSpawnData(particle, spawnLoc, 1, 0, 0, 0, 0, options));
        }
    }

    @Override
    public String getDebugInfo() {
        return "AnimatedOrbitalParticleTask";
    }

    @Override
    public Location getCurrentLocation() {
        return center;
    }

    @Override
    public boolean shouldCollide() {
        return false;
    }

    @Override
    public int getLoopDelayCounter() {
        return 0;
    }
}
