package com.jerae.zephaire.particles.animations.entity;

import com.jerae.zephaire.particles.animations.LoopDelay;
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
    private final LoopDelay loopDelay;

    private final List<Location> particles = new ArrayList<>();
    private final List<Vector> velocities = new ArrayList<>();
    private int tickCounter = 0;

    // --- PERFORMANCE: Reusable objects for vector calculations ---
    private final Vector toCenter = new Vector();
    private final Vector rotational = new Vector();


    public EntityVortexParticleTask(String effectName, Particle particle, double radius, double height, double speed, int particleCount, Object options, ConditionManager conditionManager, boolean collisionEnabled, Vector offset, EntityTarget target, int period, SpawnBehavior spawnBehavior, int despawnTimer, boolean hasGravity, LoopDelay loopDelay) {
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
    }

    @Override
    public EntityParticleTask newInstance() {
        return new EntityVortexParticleTask(
                effectName, particle, radius, height, speed, particleCount, options,
                conditionManager, collisionEnabled, offset, target, period, spawnBehavior,
                despawnTimer, hasGravity, loopDelay
        );
    }

    @Override
    public EntityTarget getTarget() {
        return target;
    }

    @Override
    public void tick(Entity entity) {
        if (loopDelay.isWaiting()) {
            return;
        }

        boolean isMoving = entity.getVelocity().setY(0).lengthSquared() > 0.01;
        switch (spawnBehavior) {
            case STANDING_STILL:
                if (isMoving) return;
                break;
            case MOVING:
                if (!isMoving) return;
                break;
            case ALWAYS:
                break;
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
            }

            if (collisionEnabled && CollisionManager.isColliding(p)) continue;

            if (particle == null && options instanceof ItemStack) {
                ParticleScheduler.queueParticle(new ParticleSpawnData(p, (ItemStack) options, despawnTimer, hasGravity));
            } else if (particle != null) {
                ParticleScheduler.queueParticle(new ParticleSpawnData(particle, p, 1, 0, 0, 0, 0, options));
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
    public void tick() {
        // This task is ticked with an entity context, so this method is not used.
    }

    @Override
    public Location getCurrentLocation() {
        // Not applicable for entity particles in the same way as static ones.
        return null;
    }

    @Override
    public boolean isLoopComplete() {
        return false;
    }

    @Override
    public LoopDelay getLoopDelay() {
        return loopDelay;
    }
}
