package com.jerae.zephaire.particles.animations.entity;

import com.jerae.zephaire.particles.ParticleScheduler;
import com.jerae.zephaire.particles.ParticleSpawnData;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.data.EntityTarget;
import com.jerae.zephaire.particles.data.SpawnBehavior;
import com.jerae.zephaire.particles.managers.CollisionManager;
import com.jerae.zephaire.particles.util.VectorUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class EntitySpiralParticleTask implements EntityParticleTask {
    private final String effectName;
    private final ConditionManager conditionManager;
    private final boolean collisionEnabled;
    private final Vector offset;
    private final EntityTarget target;
    private final int period;
    private final SpawnBehavior spawnBehavior;
    private final int loopDelay;
    private final boolean debug;
    private final boolean inheritEntityVelocity;
    private int tickCounter = 0;
    private int loopDelayCounter = 0;

    private final Particle particle;
    private final double startRadius;
    private final double endRadius;
    private final double height;
    private final double speed;
    private final double verticalSpeed;
    private final Object options;
    private final double pitch;
    private final double yaw;
    private final boolean bounce;
    private final int despawnTimer;
    private final boolean hasGravity;
    private final int duration;
    private int ticksLived = 0;

    private double angle;
    private double currentYOffset;
    private int verticalDirection = 1;
    private Location deathLocation;

    private final Vector particleVector = new Vector();
    private final Vector rotatedVector = new Vector();

    public EntitySpiralParticleTask(String effectName, Particle particle, double startRadius, double endRadius, double height, double speed, double verticalSpeed, Object options, double pitch, double yaw, boolean bounce, ConditionManager manager, boolean collisionEnabled, Vector offset, EntityTarget target, int period, SpawnBehavior spawnBehavior, int despawnTimer, boolean hasGravity, int loopDelay, boolean debug, boolean inheritEntityVelocity, int duration) {
        this.effectName = effectName;
        this.conditionManager = manager;
        this.collisionEnabled = collisionEnabled;
        this.offset = offset;
        this.target = target;
        this.period = period;
        this.spawnBehavior = spawnBehavior;
        this.loopDelay = loopDelay;
        this.debug = debug;
        this.inheritEntityVelocity = inheritEntityVelocity;
        this.particle = particle;
        this.startRadius = startRadius;
        this.endRadius = endRadius;
        this.height = height;
        this.speed = speed;
        this.verticalSpeed = verticalSpeed;
        this.options = options;
        this.pitch = pitch;
        this.yaw = yaw;
        this.bounce = bounce;
        this.despawnTimer = despawnTimer;
        this.hasGravity = hasGravity;
        this.duration = duration;
    }

    @Override
    public void tick(Entity entity) {
        if (isDone()) {
            return;
        }
        ticksLived++;

        Location currentLocation;
        if (entity != null && entity.isValid()) {
            currentLocation = entity.getLocation();
        } else if (deathLocation != null) {
            currentLocation = deathLocation;
        } else {
            return;
        }

        if (!conditionManager.allConditionsMet(currentLocation)) {
            return;
        }

        tickCounter++;
        if (tickCounter < period) {
            return;
        }
        tickCounter = 0;

        angle += speed;
        currentYOffset += verticalSpeed * verticalDirection;

        if (bounce) {
            if (currentYOffset >= height) {
                currentYOffset = height;
                verticalDirection = -1;
            } else if (currentYOffset <= 0) {
                currentYOffset = 0;
                verticalDirection = 1;
            }
        } else {
            if (currentYOffset >= height) {
                currentYOffset = 0;
            }
        }

        double currentRadius = startRadius + (endRadius - startRadius) * (currentYOffset / height);
        double xOffset = currentRadius * Math.cos(angle);
        double zOffset = currentRadius * Math.sin(angle);
        double yOffset = currentYOffset;

        particleVector.setX(xOffset).setY(yOffset).setZ(zOffset);
        VectorUtils.rotateVector(particleVector, pitch, yaw, rotatedVector);

        Location spawnLoc = currentLocation.clone().add(offset).add(rotatedVector);

        if (collisionEnabled && CollisionManager.isColliding(spawnLoc)) {
            return;
        }

        Vector entityVelocity = (entity != null && entity.isValid()) ? entity.getVelocity() : new Vector();
        Vector finalVelocity = inheritEntityVelocity ? entityVelocity : new Vector();

        if (particle == null && options instanceof ItemStack) {
            ParticleScheduler.queueParticle(new ParticleSpawnData(spawnLoc, (ItemStack) options, despawnTimer, hasGravity, finalVelocity));
        } else if (particle != null) {
            if (particle == Particle.SHRIEK && options instanceof Integer) {
                ParticleScheduler.queueParticle(new ParticleSpawnData(particle, spawnLoc, (Integer) options));
            } else if (particle == Particle.VIBRATION && options instanceof org.bukkit.Vibration) {
                ParticleScheduler.queueParticle(new ParticleSpawnData(particle, spawnLoc, (org.bukkit.Vibration) options));
            } else {
                ParticleScheduler.queueParticle(new ParticleSpawnData(particle, spawnLoc, 1, 0, 0, 0, 0, options));
            }
        }
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
    public EntityTarget getTarget() {
        return target;
    }

    @Override
    public ConditionManager getConditionManager() {
        return conditionManager;
    }

    @Override
    public EntityParticleTask newInstance() {
        return new EntitySpiralParticleTask(effectName, particle, startRadius, endRadius, height, speed, verticalSpeed, options, pitch, yaw, bounce, conditionManager, collisionEnabled, offset, target, period, spawnBehavior, despawnTimer, hasGravity, loopDelay, debug, inheritEntityVelocity, duration);
    }

    @Override
    public String getDebugInfo() {
        return "EntitySpiralParticleTask";
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
    public void setDeathLocation(Location location) {
        this.deathLocation = location;
    }
}