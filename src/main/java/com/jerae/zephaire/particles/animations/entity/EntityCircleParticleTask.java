package com.jerae.zephaire.particles.animations.entity;

import com.jerae.zephaire.particles.ParticleScheduler;
import com.jerae.zephaire.particles.ParticleSpawnData;
import com.jerae.zephaire.particles.managers.CollisionManager;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.data.EntityTarget;
import com.jerae.zephaire.particles.managers.PerformanceManager;
import com.jerae.zephaire.particles.util.ParticleUtils;
import com.jerae.zephaire.particles.util.VectorUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class EntityCircleParticleTask implements EntityParticleTask {
    private final String effectName;
    private final Particle particle;
    private final double radius;
    private final double speed;
    private final int particleCount;
    private final Object options;
    private final double pitch;
    private final double yaw;
    private final ConditionManager conditionManager;
    private final boolean collisionEnabled;
    private final Vector offset;
    private final EntityTarget target;

    private double angle = 0;

    // --- PERFORMANCE: Reusable objects to avoid creating new ones every tick ---
    private final Location spawnLocation;
    private final Vector relativePos;
    private final Vector rotatedPos = new Vector();


    public EntityCircleParticleTask(String effectName, Particle particle, double radius, double speed, int particleCount, Object options, double pitch, double yaw, ConditionManager conditionManager, boolean collisionEnabled, Vector offset, EntityTarget target) {
        this.effectName = effectName;
        this.particle = particle;
        this.radius = radius;
        this.speed = speed;
        this.particleCount = Math.max(1, particleCount);
        this.options = options;
        this.pitch = pitch;
        this.yaw = yaw;
        this.conditionManager = conditionManager;
        this.collisionEnabled = collisionEnabled;
        this.offset = offset;
        this.target = target;
        this.spawnLocation = new Location(null, 0, 0, 0); // World will be set dynamically
        this.relativePos = new Vector();
    }

    @Override
    public EntityParticleTask newInstance() {
        // Return a new instance with the same configuration but reset state (angle will be 0 by default).
        return new EntityCircleParticleTask(
                this.effectName,
                this.particle,
                this.radius,
                this.speed,
                this.particleCount,
                this.options,
                this.pitch,
                this.yaw,
                this.conditionManager,
                this.collisionEnabled,
                this.offset,
                this.target
        );
    }

    @Override
    public EntityTarget getTarget() {
        return target;
    }

    @Override
    public void tick(Entity entity) {
        Location center = entity.getLocation().add(offset);
        spawnLocation.setWorld(entity.getWorld()); // Ensure world is correct

        // In the future, conditions could be checked against the entity or location
        // if (conditionManager != null && !conditionManager.allConditionsMet(center)) {
        //     return;
        // }

        angle += speed;

        for (int i = 0; i < particleCount; i++) {
            double particleAngle = angle + (2 * Math.PI * i) / particleCount;

            relativePos.setX(radius * Math.cos(particleAngle));
            relativePos.setY(0);
            relativePos.setZ(radius * Math.sin(particleAngle));

            VectorUtils.rotateVector(relativePos, pitch, yaw, rotatedPos);

            spawnLocation.setX(center.getX() + rotatedPos.getX());
            spawnLocation.setY(center.getY() + rotatedPos.getY());
            spawnLocation.setZ(center.getZ() + rotatedPos.getZ());

            if (collisionEnabled && CollisionManager.isColliding(spawnLocation)) {
                continue;
            }

            ParticleScheduler.queueParticle(new ParticleSpawnData(particle, spawnLocation, 1, 0, 0, 0, 0, options));
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
    public String getDebugInfo() {
        return ChatColor.AQUA + "Type: " + ChatColor.WHITE + "ENTITY_ANIMATED" + "\n" +
                ChatColor.AQUA + "Shape: " + ChatColor.WHITE + "CIRCLE" + "\n" +
                ChatColor.AQUA + "Radius: " + ChatColor.WHITE + radius + "\n" +
                ChatColor.AQUA + "Speed: " + ChatColor.WHITE + speed + "\n" +
                ChatColor.AQUA + "Target: " + ChatColor.WHITE + target.getTargetType().name() +
                (target.getName() != null ? " (" + target.getName() + ")" : "") +
                (target.getEntityType() != null ? " (" + target.getEntityType().name() + ")" : "");
    }
}
