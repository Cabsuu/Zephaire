package com.jerae.zephaire.particles.animations.entity;

import com.jerae.zephaire.particles.ParticleScheduler;
import com.jerae.zephaire.particles.ParticleSpawnData;
import com.jerae.zephaire.particles.managers.CollisionManager;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.data.EntityTarget;
import com.jerae.zephaire.particles.data.SpawnBehavior;
import com.jerae.zephaire.particles.util.VectorUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

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
    private final int period;
    private final SpawnBehavior spawnBehavior;
    private final int despawnTimer;
    private final boolean hasGravity;
    private final int loopDelay;

    private double angle = 0;
    private int tickCounter = 0;
    private int loopDelayCounter = 0;
    private Location lastLocation;

    // --- PERFORMANCE: Reusable objects to avoid creating new ones every tick ---
    private final Location spawnLocation;
    private final Vector relativePos;
    private final Vector rotatedPos = new Vector();
    private final boolean testMode;
    private final boolean inheritEntityVelocity;

    public EntityCircleParticleTask(String effectName, Particle particle, double radius, double speed, int particleCount, Object options, double pitch, double yaw, ConditionManager conditionManager, boolean collisionEnabled, Vector offset, EntityTarget target, int period, SpawnBehavior spawnBehavior, int despawnTimer, boolean hasGravity, int loopDelay, boolean testMode, boolean inheritEntityVelocity) {
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
        this.period = Math.max(1, period);
        this.spawnBehavior = spawnBehavior;
        this.despawnTimer = despawnTimer;
        this.hasGravity = hasGravity;
        this.loopDelay = loopDelay;
        this.spawnLocation = new Location(null, 0, 0, 0); // World will be set dynamically
        this.relativePos = new Vector();
        this.testMode = testMode;
        this.inheritEntityVelocity = inheritEntityVelocity;
    }

    @Override
    public EntityParticleTask newInstance() {
        return new EntityCircleParticleTask(
                this.effectName, this.particle, this.radius, this.speed,
                this.particleCount, this.options, this.pitch, this.yaw,
                this.conditionManager, this.collisionEnabled, this.offset, this.target, this.period, this.spawnBehavior, this.despawnTimer, this.hasGravity, this.loopDelay, this.testMode, this.inheritEntityVelocity
        );
    }

    @Override
    public EntityTarget getTarget() {
        return target;
    }

    @Override
    public void tick(Entity entity) {
        if (!conditionManager.allConditionsMet(entity.getLocation())) return;

        // --- Spawn Behavior ---
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

        if (testMode) {
            throw new RuntimeException("TestParticleSpawn");
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

        Location center = entity.getLocation().add(offset);
        spawnLocation.setWorld(entity.getWorld()); // Ensure world is correct

        angle += speed;

        if (angle >= 2 * Math.PI) {
            angle = 0;
            loopDelayCounter = loopDelay;
        }

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

            if (particle == null && options instanceof ItemStack) {
                Vector velocity = inheritEntityVelocity ? entity.getVelocity() : new Vector(0, 0, 0);
                ParticleScheduler.queueParticle(new ParticleSpawnData(spawnLocation, (ItemStack) options, despawnTimer, hasGravity, velocity));
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
                ChatColor.AQUA + "Shape: " + ChatColor.WHITE + "CIRCLE" + "\n" +
                ChatColor.AQUA + "Radius: " + ChatColor.WHITE + radius + "\n" +
                ChatColor.AQUA + "Speed: " + ChatColor.WHITE + speed + "\n" +
                ChatColor.AQUA + "Target: " + ChatColor.WHITE + target.getTargetType().name() + targetNameInfo +
                (target.getEntityType() != null ? " (" + target.getEntityType().name() + ")" : "");
    }

    @Override
    public ConditionManager getConditionManager() {
        return conditionManager;
    }
}
