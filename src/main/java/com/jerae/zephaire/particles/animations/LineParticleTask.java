package com.jerae.zephaire.particles.animations;

import com.jerae.zephaire.particles.ParticleScheduler;
import com.jerae.zephaire.particles.ParticleSpawnData;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.managers.CollisionManager;
import com.jerae.zephaire.particles.managers.PerformanceManager;
import com.jerae.zephaire.particles.util.ParticleUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class LineParticleTask implements AnimatedParticle {

    private final Location startPoint;
    private final Particle particle;
    private final double speed;
    private final int period;
    private final Object options;
    private final boolean resetOnEnd;
    private final ConditionManager conditionManager;
    private final Vector directionVector;
    private final boolean collisionEnabled;
    private final int despawnTimer;
    private final boolean hasGravity;
    private double currentProgress = 0.0;
    private int direction = 1;
    private int tickCounter = 0;
    private final Location currentLocation;

    public LineParticleTask(Location startPoint, Location endPoint, Particle particle, double speed, int period, Object options, boolean resetOnEnd, ConditionManager conditionManager, boolean collisionEnabled, int despawnTimer, boolean hasGravity) {
        this.startPoint = startPoint;
        this.particle = particle;
        this.speed = speed;
        this.period = Math.max(1, period);
        this.options = options;
        this.resetOnEnd = resetOnEnd;
        this.conditionManager = conditionManager;
        this.directionVector = endPoint.toVector().subtract(startPoint.toVector());
        this.currentLocation = startPoint.clone();
        this.collisionEnabled = collisionEnabled;
        this.despawnTimer = despawnTimer;
        this.hasGravity = hasGravity;
    }

    @Override
    public void tick() {
        if (!conditionManager.allConditionsMet(startPoint)) {
            return;
        }

        currentProgress += speed * direction;

        if (currentProgress >= 1.0) {
            currentProgress = 1.0;
            if (resetOnEnd) {
                currentProgress = 0.0;
            } else {
                direction = -1;
            }
        } else if (currentProgress <= 0.0) {
            currentProgress = 0.0;
            direction = 1;
        }

        double newX = startPoint.getX() + directionVector.getX() * currentProgress;
        double newY = startPoint.getY() + directionVector.getY() * currentProgress;
        double newZ = startPoint.getZ() + directionVector.getZ() * currentProgress;

        currentLocation.setX(newX);
        currentLocation.setY(newY);
        currentLocation.setZ(newZ);

        tickCounter++;
        if (tickCounter >= period) {
            tickCounter = 0;

            if (collisionEnabled && CollisionManager.isColliding(currentLocation)) {
                return;
            }
            if (particle == null && options instanceof ItemStack) {
                ParticleScheduler.queueParticle(new ParticleSpawnData(currentLocation, (ItemStack) options, despawnTimer, hasGravity));
            } else if (particle != null) {
                ParticleScheduler.queueParticle(new ParticleSpawnData(particle, currentLocation, 1, 0, 0, 0, 0, options));
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
    public String getDebugInfo() {
        StringBuilder info = new StringBuilder();
        info.append(ChatColor.AQUA).append("Type: ").append(ChatColor.WHITE).append("ANIMATED").append("\n");
        info.append(ChatColor.AQUA).append("Shape: ").append(ChatColor.WHITE).append("LINE").append("\n");
        info.append(ChatColor.AQUA).append("Start: ").append(ChatColor.WHITE).append(String.format("%.2f, %.2f, %.2f", startPoint.getX(), startPoint.getY(), startPoint.getZ())).append("\n");
        info.append(ChatColor.AQUA).append("Current Location: ").append(ChatColor.WHITE).append(String.format("%.2f, %.2f, %.2f", currentLocation.getX(), currentLocation.getY(), currentLocation.getZ())).append("\n");
        info.append(ChatColor.AQUA).append("Speed: ").append(ChatColor.WHITE).append(speed).append("\n");
        info.append(ChatColor.DARK_AQUA).append("--- Status ---").append("\n");
        info.append(ChatColor.AQUA).append("Player Nearby: ").append(ParticleUtils.formatBoolean(PerformanceManager.isPlayerNearby(startPoint))).append("\n");
        info.append(ChatColor.AQUA).append("Conditions Met: ").append(ParticleUtils.formatBoolean(conditionManager.allConditionsMet(startPoint))).append("\n");
        info.append(ChatColor.AQUA).append("Collision Enabled: ").append(ParticleUtils.formatBoolean(collisionEnabled));
        return info.toString();
    }
}
