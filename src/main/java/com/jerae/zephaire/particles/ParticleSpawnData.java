package com.jerae.zephaire.particles;

import org.bukkit.Location;
import org.bukkit.Particle;

/**
 * A simple data class to hold all the necessary information for spawning a particle.
 * This is used to pass data from asynchronous calculation threads to a synchronous spawning task.
 */
public class ParticleSpawnData {
    public final Particle particle;
    public final Location location;
    public final int count;
    public final double offsetX, offsetY, offsetZ;
    public final double speed;
    public final Object data;

    public ParticleSpawnData(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double speed, Object data) {
        this.particle = particle;
        // Clone the location to ensure a snapshot of its state at the time of creation.
        this.location = location.clone();
        this.count = count;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.speed = speed;
        this.data = data;
    }
}
