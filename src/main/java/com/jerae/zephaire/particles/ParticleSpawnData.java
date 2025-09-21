package com.jerae.zephaire.particles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

/**
 * A simple data class to hold all the necessary information for spawning a particle or a visual item.
 * This is used to pass data from asynchronous calculation threads to a synchronous spawning task.
 */
public class ParticleSpawnData {
    public final ParticleType particleType;
    public final Particle particle;
    public final Location location;
    public final int count;
    public final double offsetX, offsetY, offsetZ;
    public final double speed;
    public final Object data;
    public final int despawnTimer;
    public final boolean hasGravity;
    public final org.bukkit.util.Vector velocity;
    public final int shriekDelay;
    public final org.bukkit.Vibration vibration;
    public final float sculkChargeRoll;


    /**
     * Constructor for standard Bukkit particles.
     */
    public ParticleSpawnData(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double speed, Object data) {
        this.particleType = ParticleType.BUKKIT;
        this.particle = particle;
        this.location = location.clone();
        this.count = count;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.speed = speed;
        this.data = data;
        this.despawnTimer = 0;
        this.hasGravity = false;
        this.velocity = null;
        this.shriekDelay = 0;
        this.vibration = null;
        this.sculkChargeRoll = 0;
    }

    /**
     * @deprecated Use the constructor with the hasGravity parameter instead.
     * Constructor for VISUAL_ITEM particles.
     * The `data` field will hold the ItemStack.
     */
    @Deprecated
    public ParticleSpawnData(Location location, ItemStack itemStack, int despawnTimer) {
        this(location, itemStack, despawnTimer, false, new org.bukkit.util.Vector(0, 0, 0));
    }

    /**
     * Constructor for VISUAL_ITEM particles.
     * The `data` field will hold the ItemStack.
     */
    public ParticleSpawnData(Location location, ItemStack itemStack, int despawnTimer, boolean hasGravity) {
        this(location, itemStack, despawnTimer, hasGravity, new org.bukkit.util.Vector(0, 0, 0));
    }

    /**
     * Constructor for VISUAL_ITEM particles.
     * The `data` field will hold the ItemStack.
     */
    public ParticleSpawnData(Location location, ItemStack itemStack, int despawnTimer, boolean hasGravity, org.bukkit.util.Vector velocity) {
        this.particleType = ParticleType.VISUAL_ITEM;
        this.particle = null; // Not a real Bukkit particle
        this.location = location.clone();
        this.count = 1;
        this.offsetX = 0;
        this.offsetY = 0;
        this.offsetZ = 0;
        this.speed = 0;
        this.data = itemStack;
        this.despawnTimer = despawnTimer;
        this.hasGravity = hasGravity;
        this.velocity = velocity;
        this.shriekDelay = 0;
        this.vibration = null;
        this.sculkChargeRoll = 0;
    }

    /**
     * Constructor for SCULK_CHARGE particles.
     */
    public ParticleSpawnData(Particle particle, Location location, float sculkChargeRoll) {
        this.particleType = ParticleType.BUKKIT;
        this.particle = particle;
        this.location = location.clone();
        this.count = 1;
        this.offsetX = 0;
        this.offsetY = 0;
        this.offsetZ = 0;
        this.speed = 0;
        this.data = null;
        this.despawnTimer = 0;
        this.hasGravity = false;
        this.velocity = null;
        this.shriekDelay = 0;
        this.vibration = null;
        this.sculkChargeRoll = sculkChargeRoll;
    }

    /**
     * Constructor for SHRIEK particles.
     */
    public ParticleSpawnData(Particle particle, Location location, int shriekDelay) {
        this.particleType = ParticleType.BUKKIT;
        this.particle = particle;
        this.location = location.clone();
        this.count = 1;
        this.offsetX = 0;
        this.offsetY = 0;
        this.offsetZ = 0;
        this.speed = 0;
        this.data = null;
        this.despawnTimer = 0;
        this.hasGravity = false;
        this.velocity = null;
        this.shriekDelay = shriekDelay;
        this.vibration = null;
        this.sculkChargeRoll = 0;
    }

    /**
     * Constructor for VIBRATION particles.
     */
    public ParticleSpawnData(Particle particle, Location location, org.bukkit.Vibration vibration) {
        this.particleType = ParticleType.BUKKIT;
        this.particle = particle;
        this.location = location.clone();
        this.count = 1;
        this.offsetX = 0;
        this.offsetY = 0;
        this.offsetZ = 0;
        this.speed = 0;
        this.data = null;
        this.despawnTimer = 0;
        this.hasGravity = false;
        this.velocity = null;
        this.shriekDelay = 0;
        this.vibration = vibration;
        this.sculkChargeRoll = 0;
    }
}

