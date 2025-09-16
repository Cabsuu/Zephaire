package com.jerae.zephaire.particles;

import org.bukkit.Particle;

public class ParticleSpawnData {
    private final Particle particle;
    private final int count;
    private final double speed;
    private final Object data;

    public ParticleSpawnData(Particle particle, int count, double speed, Object data) {
        this.particle = particle;
        this.count = count;
        this.speed = speed;
        this.data = data;
    }

    public Particle getParticle() {
        return particle;
    }

    public int getCount() {
        return count;
    }

    public double getSpeed() {
        return speed;
    }

    public Object getData() {
        return data;
    }
}
