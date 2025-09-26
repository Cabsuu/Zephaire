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
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class EntityVortexParticleTask implements EntityParticleTask {

    private final String effectName;
    private final Particle particle;
    private final double radius;
    private final double height;
    private final double speed;
    private final int particleCount;
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
    private final boolean inheritEntityVelocity;
    private final int duration;
    private int ticksLived = 0;

    private final List<Location> particles = new ArrayList<>();
    private final List<Vector> velocities = new ArrayList<>();
    private int tickCounter = 0;
    private int loopDelayCounter = 0;
    private Location lastLocation;

    // --- PERFORMANCE: Reusable objects for vector calculations ---
    private final Vector toCenter = new Vector();
    private final Vector rotational = new Vector();


    public EntityVortexParticleTask(String effectName, Particle particle, double radius, double height, double speed, int particleCount, Object options, ConditionManager conditionManager, boolean collisionEnabled, Vector offset, EntityTarget target, int period, SpawnBehavior spawnBehavior, int despawnTimer, boolean hasGravity, int loopDelay, boolean inheritEntityVelocity, int duration) {
        this.effectName = effectName;
        this.particle = particle;
        this.radius = radius;
        this.height = height;
        this.speed = speed;
        this.particleCount = particleCount;
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
        this.inheritEntityVelocity = inheritEntityVelocity;
        this.duration = duration;
    }

    @Override
    public EntityParticleTask newInstance() {
        return new EntityVortexParticleTask(
                effectName, particle, radius, height, speed, particleCount, options,
                conditionManager, collisionEnabled, offset, target, period, spawnBehavior,
                despawnTimer, hasGravity, loopDelay, inheritEntityVelocity, duration
        );
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
        World world = entity.getWorld();

        if (particles.isEmpty()) {
            for (int i = 0; i < particleCount; i++) {
                particles.add(getRandomLocationInVortex(center, world));
                velocities.add(new Vector(0, 0, 0));
            }
        }

        for (int i = 0; i < particleCount; i++) {
            Location p = particles.get(i);
            Vector v = velocities.get(i);

            toCenter.setX(center.getX() - p.getX());
            toCenter.setY(0);
            toCenter.setZ(center.getZ() - p.getZ());

            if (toCenter.lengthSquared() < 0.0001) {
                particles.set(i, getRandomLocationInVortex(center, world));
                v.zero();
                continue;
            }

            double distanceToCenter = toCenter.length();
            v.add(toCenter.normalize().multiply(distanceToCenter * 0.01));
            v.setY(v.getY() + (radius - distanceToCenter) * 0.01);

            rotational.setX(-toCenter.getZ()).setY(0).setZ(toCenter.getX());
            v.add(rotational.normalize().multiply(speed));

            p.add(v.multiply(0.8));

            double newDistanceToCenter = Math.sqrt(Math.pow(center.getX() - p.getX(), 2) + Math.pow(center.getZ() - p.getZ(), 2));

            if (p.getY() > center.getY() + height || newDistanceToCenter > radius) {
                particles.set(i, getRandomLocationInVortex(center, world));
                v.zero();
                loopDelayCounter = loopDelay;
            }

            if (collisionEnabled && CollisionManager.isColliding(p)) continue;

            Vector totalVelocity = v.clone();
            if (inheritEntityVelocity) {
                totalVelocity.add(entity.getVelocity());
            }
            if (particle == null && options instanceof ItemStack) {
                ParticleScheduler.queueParticle(new ParticleSpawnData(p, (ItemStack) options, despawnTimer, hasGravity, totalVelocity));
            } else if (particle != null) {
                if (particle == Particle.SHRIEK && options instanceof Integer) {
                    ParticleScheduler.queueParticle(new ParticleSpawnData(particle, p, (Integer) options));
                } else if (particle == Particle.VIBRATION && options instanceof org.bukkit.Vibration) {
                    ParticleScheduler.queueParticle(new ParticleSpawnData(particle, p, (org.bukkit.Vibration) options));
                } else if (particle == Particle.SCULK_CHARGE && options instanceof Float) {
                    ParticleScheduler.queueParticle(new ParticleSpawnData(particle, p, (Float) options));
                } else if (particle == Particle.TRAIL && options instanceof Integer) {
                    ParticleScheduler.queueParticle(new ParticleSpawnData(particle, p, (Integer) options, hasGravity));
                } else {
                    ParticleScheduler.queueParticle(new ParticleSpawnData(particle, p, 1, 0, 0, 0, 0, options));
                }
            }
        }
    }


    private Location getRandomLocationInVortex(Location center, World world) {
        double angle = ThreadLocalRandom.current().nextDouble(0, 2 * Math.PI);
        double r = ThreadLocalRandom.current().nextDouble(0, radius);
        double x = center.getX() + r * Math.cos(angle);
        double z = center.getZ() + r * Math.sin(angle);
        double y = center.getY() + ThreadLocalRandom.current().nextDouble(0, height * 0.2);
        return new Location(world, x, y, z);
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
        String targetNameInfo = (target.getNames() != null && !target.getNames().isEmpty()) ?
                " (" + String.join(", ", target.getNames()) + ")" : "";

        return ChatColor.AQUA + "Type: " + ChatColor.WHITE + "ENTITY_ANIMATED" + "\n" +
                ChatColor.AQUA + "Shape: " + ChatColor.WHITE + "VORTEX" + "\n" +
                ChatColor.AQUA + "Radius: " + ChatColor.WHITE + radius + "\n" +
                ChatColor.AQUA + "Height: " + ChatColor.WHITE + height + "\n" +
                ChatColor.AQUA + "Speed: " + ChatColor.WHITE + speed + "\n" +
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
