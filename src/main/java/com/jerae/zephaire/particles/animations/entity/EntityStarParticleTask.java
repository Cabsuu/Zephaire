package com.jerae.zephaire.particles.animations.entity;

import com.jerae.zephaire.particles.ParticleScheduler;
import com.jerae.zephaire.particles.ParticleSpawnData;
import com.jerae.zephaire.particles.managers.CollisionManager;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.data.EntityTarget;
import com.jerae.zephaire.particles.data.SpawnBehavior;
import com.jerae.zephaire.particles.util.ParticleDrawingUtils;
import com.jerae.zephaire.particles.util.VectorUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class EntityStarParticleTask implements EntityParticleTask {

    private final String effectName;
    private final Particle particle;
    private final int points;
    private final double outerRadius;
    private final double innerRadius;
    private final double speed;
    private final double density;
    private final Object options;
    private final double pitch;
    private final double yaw;
    private final ConditionManager conditionManager;
    private final boolean collisionEnabled;
    private final Vector offset;
    private final EntityTarget target;
    private final int period;
    private final double height;
    private final double verticalSpeed;
    private final boolean bounce;
    private final SpawnBehavior spawnBehavior;
    private final int despawnTimer;
    private final boolean hasGravity;
    private final int loopDelay;

    private double rotationAngle = 0;
    private int tickCounter = 0;
    private double currentYOffset = 0;
    private int verticalDirection = 1;
    private int loopDelayCounter = 0;

    // --- PERFORMANCE: Reusable objects to avoid creating new ones every tick ---
    private final Vector[] vertices;
    private final Vector reusableVertex = new Vector();
    private final Vector lineDirection = new Vector();
    private final Vector currentLinePoint = new Vector();
    private final Location particleLoc;

    public EntityStarParticleTask(String effectName, Particle particle, int points, double outerRadius, double innerRadius, double speed, double density, Object options, double pitch, double yaw, ConditionManager conditionManager, boolean collisionEnabled, Vector offset, EntityTarget target, int period, double height, double verticalSpeed, boolean bounce, SpawnBehavior spawnBehavior, int despawnTimer, boolean hasGravity, int loopDelay) {
        this.effectName = effectName;
        this.particle = particle;
        this.points = Math.max(2, points);
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
        this.speed = speed;
        this.density = density;
        this.options = options;
        this.pitch = pitch;
        this.yaw = yaw;
        this.conditionManager = conditionManager;
        this.collisionEnabled = collisionEnabled;
        this.offset = offset;
        this.target = target;
        this.period = Math.max(1, period);
        this.height = height;
        this.verticalSpeed = verticalSpeed;
        this.bounce = bounce;
        this.spawnBehavior = spawnBehavior;
        this.despawnTimer = despawnTimer;
        this.hasGravity = hasGravity;
        this.loopDelay = loopDelay;
        this.vertices = new Vector[this.points * 2];
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = new Vector();
        }
        this.particleLoc = new Location(null, 0, 0, 0);
    }

    @Override
    public EntityParticleTask newInstance() {
        return new EntityStarParticleTask(effectName, particle, points, outerRadius, innerRadius, speed, density, options, pitch, yaw, conditionManager, collisionEnabled, offset, target, period, height, verticalSpeed, bounce, this.spawnBehavior, despawnTimer, hasGravity, loopDelay);
    }

    @Override
    public EntityTarget getTarget() {
        return target;
    }

    @Override
    public void tick(Entity entity) {
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
        particleLoc.setWorld(entity.getWorld());

        rotationAngle += speed;

        if (height != 0) {
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
                } else if (currentYOffset < 0) {
                    currentYOffset = 0;
                    loopDelayCounter = loopDelay;
                }
            }
        }

        drawStar(center);
    }

    private void drawStar(Location center) {
        int totalVertices = points * 2;

        // Calculate all the vertices of the star with the vertical offset
        for (int i = 0; i < totalVertices; i++) {
            double angle = rotationAngle + (i * Math.PI / points);
            double radius = (i % 2 == 0) ? outerRadius : innerRadius;
            reusableVertex.setX(Math.cos(angle) * radius).setY(currentYOffset).setZ(Math.sin(angle) * radius);
            VectorUtils.rotateVector(reusableVertex, pitch, yaw, vertices[i]);
        }

        // Draw lines between the vertices
        for (int i = 0; i < totalVertices; i++) {
            Vector start = vertices[i];
            Vector end = vertices[(i + 1) % totalVertices];
            drawParticleLine(center, start, end);
        }
    }

    private void drawParticleLine(Location center, Vector start, Vector end) {
        lineDirection.copy(end).subtract(start);
        double length = lineDirection.length();
        lineDirection.normalize();

        for (double d = 0; d < length; d += (1.0 / density)) {
            currentLinePoint.copy(lineDirection).multiply(d).add(start);
            particleLoc.setX(center.getX() + currentLinePoint.getX());
            particleLoc.setY(center.getY() + currentLinePoint.getY());
            particleLoc.setZ(center.getZ() + currentLinePoint.getZ());

            if (collisionEnabled && CollisionManager.isColliding(particleLoc)) {
                continue;
            }
            if (particle == null && options instanceof ItemStack) {
                ParticleScheduler.queueParticle(new ParticleSpawnData(particleLoc, (ItemStack) options, despawnTimer, hasGravity));
            } else if (particle != null) {
                ParticleScheduler.queueParticle(new ParticleSpawnData(particle, particleLoc, 1, 0, 0, 0, 0, options));
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
                ChatColor.AQUA + "Shape: " + ChatColor.WHITE + "STAR" + "\n" +
                ChatColor.AQUA + "Radii: " + ChatColor.WHITE + String.format("Outer:%.1f, Inner:%.1f", outerRadius, innerRadius) + "\n" +
                ChatColor.AQUA + "Rotation Speed: " + ChatColor.WHITE + speed + "\n" +
                ChatColor.AQUA + "Height: " + ChatColor.WHITE + height + "\n" +
                ChatColor.AQUA + "Target: " + ChatColor.WHITE + target.getTargetType().name() + targetNameInfo +
                (target.getEntityType() != null ? " (" + target.getEntityType().name() + ")" : "");
    }
}
