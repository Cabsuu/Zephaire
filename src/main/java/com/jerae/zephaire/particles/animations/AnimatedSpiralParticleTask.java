package com.jerae.zephaire.particles.animations;

import com.jerae.zephaire.particles.ParticleScheduler;
import com.jerae.zephaire.particles.ParticleSpawnData;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.managers.CollisionManager;
import com.jerae.zephaire.particles.managers.PerformanceManager;
import com.jerae.zephaire.particles.util.ParticleUtils;
import com.jerae.zephaire.particles.util.VectorUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class AnimatedSpiralParticleTask implements AnimatedParticle {

    private final Location base;
    private final Particle particle;
    private final double startRadius;
    private final double endRadius;
    private final double height;
    private final double speed;
    private final double verticalSpeed;
    private final int period;
    private final Object options;
    private final double pitch;
    private final double yaw;
    private final boolean bounce;
    private final ConditionManager conditionManager;
    private final boolean collisionEnabled;
    private final int despawnTimer;
    private final boolean hasGravity;
    private final int loopDelay;

    private double angle;
    private double currentYOffset;
    private int verticalDirection = 1;
    private int tickCounter = 0;
    private int loopDelayCounter = 0;

    private final Location currentLocation;
    private final Vector particleVector = new Vector();
    private final Vector rotatedVector = new Vector();


    public AnimatedSpiralParticleTask(Location base, Particle particle, double startRadius, double endRadius, double height, double speed, double verticalSpeed, int period, double startAngle, Object options, double pitch, double yaw, boolean bounce, ConditionManager conditionManager, boolean collisionEnabled, int despawnTimer, boolean hasGravity, int loopDelay) {
        this.base = base;
        this.particle = particle;
        this.startRadius = startRadius;
        this.endRadius = endRadius;
        this.height = height;
        this.speed = speed;
        this.verticalSpeed = verticalSpeed;
        this.period = Math.max(1, period);
        this.options = options;
        this.pitch = pitch;
        this.yaw = yaw;
        this.bounce = bounce;
        this.conditionManager = conditionManager;
        this.collisionEnabled = collisionEnabled;
        this.angle = startAngle;
        this.currentLocation = base.clone();
        this.currentYOffset = 0;
        this.despawnTimer = despawnTimer;
        this.hasGravity = hasGravity;
        this.loopDelay = loopDelay;
    }

    @Override
    public void tick() {
        if (!conditionManager.allConditionsMet(base)) {
            return;
        }

        if (loopDelayCounter > 0) {
            loopDelayCounter--;
            return;
        }

        angle += speed;
        currentYOffset += verticalSpeed * verticalDirection;

        if (bounce) {
            if (currentYOffset >= height) {
                currentYOffset = height;
                verticalDirection = -1;
                loopDelayCounter = loopDelay;
            } else if (currentYOffset <= 0) {
                currentYOffset = 0;
                verticalDirection = 1;
                loopDelayCounter = loopDelay;
            }
        } else {
            if (currentYOffset >= height) {
                currentYOffset = 0;
                loopDelayCounter = loopDelay;
            }
        }

        double currentRadius = startRadius + (endRadius - startRadius) * (currentYOffset / height);
        double xOffset = currentRadius * Math.cos(angle);
        double zOffset = currentRadius * Math.sin(angle);
        double yOffset = currentYOffset;

        particleVector.setX(xOffset).setY(yOffset).setZ(zOffset);
        VectorUtils.rotateVector(particleVector, pitch, yaw, rotatedVector);

        currentLocation.setX(base.getX() + rotatedVector.getX());
        currentLocation.setY(base.getY() + rotatedVector.getY());
        currentLocation.setZ(base.getZ() + rotatedVector.getZ());

        tickCounter++;
        if (tickCounter >= period) {
            tickCounter = 0;

            if (collisionEnabled && CollisionManager.isColliding(currentLocation)) {
                return;
            }
            if (particle == null && options instanceof ItemStack) {
                ParticleScheduler.queueParticle(new ParticleSpawnData(currentLocation, (ItemStack) options, despawnTimer, hasGravity));
            } else if (particle != null) {
                if (particle == Particle.SHRIEK && options instanceof Integer) {
                    ParticleScheduler.queueParticle(new ParticleSpawnData(particle, currentLocation, (Integer) options));
                } else if (particle == Particle.VIBRATION && options instanceof org.bukkit.Vibration) {
                    ParticleScheduler.queueParticle(new ParticleSpawnData(particle, currentLocation, (org.bukkit.Vibration) options));
                } else if (particle == Particle.SCULK_CHARGE && options instanceof Float) {
                    ParticleScheduler.queueParticle(new ParticleSpawnData(particle, currentLocation, (Float) options));
                } else if (particle == Particle.TRAIL && options instanceof Integer) {
                    ParticleScheduler.queueParticle(new ParticleSpawnData(particle, currentLocation, (Integer) options, hasGravity));
                } else {
                    ParticleScheduler.queueParticle(new ParticleSpawnData(particle, currentLocation, 1, 0, 0, 0, 0, options));
                }
            }
        }
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
    public int getLoopDelayCounter() {
        return loopDelayCounter;
    }

    @Override
    public String getDebugInfo() {
        return "AnimatedSpiral";
    }
}
