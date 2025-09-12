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
import org.bukkit.util.Vector;

public class WaveParticleTask implements AnimatedParticle {

    private final Location base;
    private final Particle particle;
    private final double amplitude;
    private final double length;
    private final double speed;
    private final int period;
    private final Object options;
    private final double pitch;
    private final double yaw;
    private final ConditionManager conditionManager;
    private final boolean collisionEnabled;

    private double progress = 0;
    private int tickCounter = 0;

    // --- PERFORMANCE: Reusable objects to avoid creating new ones every tick ---
    private final Location currentLocation;
    private final Vector particleVector = new Vector();
    private final Vector rotatedVector = new Vector();


    public WaveParticleTask(Location base, Particle particle, double amplitude, double length, double speed, int period, Object options, double pitch, double yaw, ConditionManager conditionManager, boolean collisionEnabled) {
        this.base = base;
        this.particle = particle;
        this.amplitude = amplitude;
        this.length = length;
        this.speed = speed;
        this.period = Math.max(1, period);
        this.options = options;
        this.pitch = pitch;
        this.yaw = yaw;
        this.conditionManager = conditionManager;
        this.collisionEnabled = collisionEnabled;
        this.currentLocation = base.clone();
    }

    @Override
    public void tick() {
        if (!conditionManager.allConditionsMet(base)) {
            return;
        }

        progress += speed;

        if (progress > length) {
            progress = 0;
        }

        double angle = (progress / length) * 2 * Math.PI;
        double xOffset = progress;
        double yOffset = amplitude * Math.sin(angle);

        // --- PERFORMANCE: Reuse the particleVector object ---
        particleVector.setX(xOffset).setY(yOffset).setZ(0);
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
            ParticleScheduler.queueParticle(new ParticleSpawnData(particle, currentLocation, 1, 0, 0, 0, 0, options));
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
        info.append(ChatColor.AQUA).append("Shape: ").append(ChatColor.WHITE).append("WAVE").append("\n");
        info.append(ChatColor.AQUA).append("Base: ").append(ChatColor.WHITE).append(String.format("%.2f, %.2f, %.2f", base.getX(), base.getY(), base.getZ())).append("\n");
        info.append(ChatColor.AQUA).append("Current Location: ").append(ChatColor.WHITE).append(String.format("%.2f, %.2f, %.2f", currentLocation.getX(), currentLocation.getY(), currentLocation.getZ())).append("\n");
        info.append(ChatColor.AQUA).append("Amplitude: ").append(ChatColor.WHITE).append(amplitude).append("\n");
        info.append(ChatColor.AQUA).append("Length: ").append(ChatColor.WHITE).append(length).append("\n");
        info.append(ChatColor.AQUA).append("Speed: ").append(ChatColor.WHITE).append(speed).append("\n");
        info.append(ChatColor.DARK_AQUA).append("--- Status ---").append("\n");
        info.append(ChatColor.AQUA).append("Player Nearby: ").append(ParticleUtils.formatBoolean(PerformanceManager.isPlayerNearby(base))).append("\n");
        info.append(ChatColor.AQUA).append("Conditions Met: ").append(ParticleUtils.formatBoolean(conditionManager.allConditionsMet(base))).append("\n");
        info.append(ChatColor.AQUA).append("Collision Enabled: ").append(ParticleUtils.formatBoolean(collisionEnabled));
        return info.toString();
    }
}
