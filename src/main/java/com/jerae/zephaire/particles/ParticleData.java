package com.jerae.zephaire.particles;

import com.jerae.zephaire.particles.conditions.ConditionManager;
import org.bukkit.Location;
import org.bukkit.Particle;

public class ParticleData {
    private final Location location;
    private final Particle particle;
    private final Object particleOptions;
    private final int count;
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;
    private final double extra;
    private final boolean force;
    private final long initialDelay;
    private final long period;
    private final ConditionManager conditionManager;

    public ParticleData(Location location, Particle particle, int count, double offsetX, double offsetY, double offsetZ, double extra, boolean force, long initialDelay, long period, Object particleOptions, ConditionManager conditionManager) {
        this.location = location;
        this.particle = particle;
        this.count = count;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.extra = extra;
        this.force = force;
        this.initialDelay = initialDelay;
        this.period = period;
        this.particleOptions = particleOptions;
        this.conditionManager = conditionManager;
    }

    // Getters
    public Location getLocation() { return location; }
    public Particle getParticle() { return particle; }
    public int getCount() { return count; }
    public double getOffsetX() { return offsetX; }
    public double getOffsetY() { return offsetY; }
    public double getOffsetZ() { return offsetZ; }
    public double getExtra() { return extra; }
    public boolean isForce() { return force; }
    public long getInitialDelay() { return initialDelay; }
    public long getPeriod() { return period; }
    public Object getParticleOptions() { return particleOptions; }
    public ConditionManager getConditionManager() { return conditionManager; }
}
