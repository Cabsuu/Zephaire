package com.jerae.zephaire.particles.animations.entity;

import com.jerae.zephaire.particles.ParticleScheduler;
import com.jerae.zephaire.particles.ParticleSpawnData;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.data.EntityTarget;
import com.jerae.zephaire.particles.data.SpawnBehavior;
import com.jerae.zephaire.particles.managers.CollisionManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public class EntityPointParticleTask implements EntityParticleTask {
    private final String effectName;
    private final Particle particle;
    private final Object options;
    private final ConditionManager conditionManager;
    private final boolean collisionEnabled;
    private final Vector offset;
    private final EntityTarget target;
    private final int period;
    private final SpawnBehavior spawnBehavior;
    private final int despawnTimer;
    private final boolean hasGravity;
    private final int loopDelay;
    private final boolean debug;
    private final boolean inheritEntityVelocity;
    private final double spread;
    private final int particleCount;
    private final int duration;
    private int ticksLived = 0;

    private int tickCounter = 0;
    private int loopDelayCounter = 0;
    private Location lastLocation;

    public EntityPointParticleTask(String effectName, Particle particle, Object options, ConditionManager conditionManager, boolean collisionEnabled, Vector offset, EntityTarget target, int period, SpawnBehavior spawnBehavior, int despawnTimer, boolean hasGravity, int loopDelay, boolean debug, boolean inheritEntityVelocity, double spread, int particleCount, int duration) {
        this.effectName = effectName;
        this.particle = particle;
        this.options = options;
        this.conditionManager = conditionManager;
        this.collisionEnabled = collisionEnabled;
        this.offset = offset;
        this.target = target;
        this.period = Math.max(1, period);
        this.spawnBehavior = spawnBehavior;
        this.despawnTimer = despawnTimer;
        this.hasGravity = hasGravity;
        this.loopDelay = loopDelay;
        this.debug = debug;
        this.inheritEntityVelocity = inheritEntityVelocity;
        this.spread = spread;
        this.particleCount = particleCount;
        this.duration = duration;
    }

    @Override
    public EntityParticleTask newInstance() {
        return new EntityPointParticleTask(effectName, particle, options, conditionManager, collisionEnabled, offset, target, period, spawnBehavior, despawnTimer, hasGravity, loopDelay, debug, inheritEntityVelocity, spread, particleCount, duration);
    }

    @Override
    public EntityTarget getTarget() {
        return target;
    }

    @Override
    public void tick(Entity entity) {
        if (isDone()) {
            return;
        }
        ticksLived++;

        if (!conditionManager.allConditionsMet(entity.getLocation())) return;
        Location currentLocation = entity.getLocation();

        boolean isMovingHorizontally;
        if (lastLocation == null || currentLocation == null || !lastLocation.getWorld().equals(currentLocation.getWorld())) {
            isMovingHorizontally = entity.getVelocity().setY(0).lengthSquared() > 0.001;
        } else {
            isMovingHorizontally = lastLocation.getX() != currentLocation.getX() || lastLocation.getZ() != currentLocation.getZ();
        }

        if (currentLocation != null) {
            this.lastLocation = currentLocation.clone();
        }

        boolean isOnGround = entity.isOnGround();

        switch (spawnBehavior) {
            case STANDING_STILL:
                if (isMovingHorizontally || !isOnGround) return;
                break;
            case MOVING:
                if (!isMovingHorizontally && isOnGround) return;
                break;
            case ALWAYS:
                break;
        }

        if (loopDelayCounter > 0) {
            loopDelayCounter--;
            return;
        }

        tickCounter++;
        if (tickCounter < period) {
            return;
        }
        tickCounter = 0;

        Location spawnLocation = entity.getLocation().add(offset);

        if (collisionEnabled && CollisionManager.isColliding(spawnLocation)) {
            return;
        }

        if (particle == null && options instanceof ItemStack) {
            if (spread > 0) {
                for (int i = 0; i < particleCount; i++) {
                    Vector randomVelocity = new Vector(
                            ThreadLocalRandom.current().nextDouble(-1, 1),
                            ThreadLocalRandom.current().nextDouble(-1, 1),
                            ThreadLocalRandom.current().nextDouble(-1, 1)
                    ).normalize().multiply(spread);
                    Vector finalVelocity = inheritEntityVelocity ? entity.getVelocity().clone().add(randomVelocity) : randomVelocity;
                    ParticleScheduler.queueParticle(new ParticleSpawnData(spawnLocation, (ItemStack) options, despawnTimer, hasGravity, finalVelocity));
                }
            } else {
                Vector velocity = inheritEntityVelocity ? entity.getVelocity() : new Vector(0, 0, 0);
                ParticleScheduler.queueParticle(new ParticleSpawnData(spawnLocation, (ItemStack) options, despawnTimer, hasGravity, velocity));
            }
        } else if (particle != null) {
            if (particle == Particle.SHRIEK && options instanceof Integer) {
                ParticleScheduler.queueParticle(new ParticleSpawnData(particle, spawnLocation, (Integer) options));
            } else if (particle == Particle.VIBRATION && options instanceof org.bukkit.Vibration) {
                ParticleScheduler.queueParticle(new ParticleSpawnData(particle, spawnLocation, (org.bukkit.Vibration) options));
            } else if (particle == Particle.SCULK_CHARGE && options instanceof Float) {
                ParticleScheduler.queueParticle(new ParticleSpawnData(particle, spawnLocation, (Float) options));
            } else if (particle == Particle.TRAIL && options instanceof Integer) {
                ParticleScheduler.queueParticle(new ParticleSpawnData(particle, spawnLocation, (Integer) options, hasGravity));
            } else {
                ParticleScheduler.queueParticle(new ParticleSpawnData(particle, spawnLocation, 1, 0, 0, 0, 0, options));
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
    public String getDebugInfo() {
        String targetNameInfo = "";
        if (target.getNames() != null && !target.getNames().isEmpty()) {
            targetNameInfo = " (" + String.join(", ", target.getNames()) + ")";
        }

        return ChatColor.AQUA + "Type: " + ChatColor.WHITE + "ENTITY_ANIMATED" + "\n" +
                ChatColor.AQUA + "Shape: " + ChatColor.WHITE + "POINT" + "\n" +
                ChatColor.AQUA + "Target: " + ChatColor.WHITE + target.getTargetType().name() + targetNameInfo +
                (target.getEntityType() != null ? " (" + target.getEntityType().name() + ")" : "");
    }

    @Override
    public ConditionManager getConditionManager() {
        return conditionManager;
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
}
