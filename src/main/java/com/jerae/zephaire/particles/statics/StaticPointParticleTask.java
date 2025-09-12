package com.jerae.zephaire.particles.statics;

import com.jerae.zephaire.particles.Debuggable;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.managers.CollisionManager;
import com.jerae.zephaire.particles.managers.PerformanceManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class StaticPointParticleTask extends BukkitRunnable implements Debuggable {

    private final Location location;
    private final Particle particle;
    private final int count;
    private final double offsetX, offsetY, offsetZ;
    private final double speed;
    private final Object particleOptions;
    private final ConditionManager conditionManager;
    private final boolean collisionEnabled;
    private final World world;

    public StaticPointParticleTask(Location location, Particle particle, int count, double offsetX, double offsetY, double offsetZ, double speed, Object particleOptions, ConditionManager conditionManager, boolean collisionEnabled) {
        this.location = location;
        this.particle = particle;
        this.count = count;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.speed = speed;
        this.particleOptions = particleOptions;
        this.conditionManager = conditionManager;
        this.collisionEnabled = collisionEnabled;
        this.world = location.getWorld();
    }

    @Override
    public void run() {
        if (world == null) {
            this.cancel(); // Stop the task if the world is gone
            return;
        }

        if (!PerformanceManager.isPlayerNearby(location) || !conditionManager.allConditionsMet(location)) {
            return;
        }

        if (collisionEnabled && CollisionManager.isColliding(location)) {
            return;
        }

        world.spawnParticle(
                particle, location, count,
                offsetX, offsetY, offsetZ,
                speed, particleOptions
        );
    }

    @Override
    public String getDebugInfo() {
        StringBuilder info = new StringBuilder();
        info.append(ChatColor.AQUA).append("Type: ").append(ChatColor.WHITE).append("STATIC").append("\n");
        info.append(ChatColor.AQUA).append("Shape: ").append(ChatColor.WHITE).append("POINT").append("\n");
        info.append(ChatColor.AQUA).append("Location: ").append(ChatColor.WHITE).append(String.format("%.2f, %.2f, %.2f", location.getX(), location.getY(), location.getZ())).append("\n");
        info.append(ChatColor.AQUA).append("Particle: ").append(ChatColor.WHITE).append(particle.name()).append("\n");
        info.append(ChatColor.AQUA).append("Count: ").append(ChatColor.WHITE).append(count).append("\n");
        info.append(ChatColor.DARK_AQUA).append("--- Status ---").append("\n");
        info.append(ChatColor.AQUA).append("Player Nearby: ").append(formatBoolean(PerformanceManager.isPlayerNearby(location))).append("\n");
        info.append(ChatColor.AQUA).append("Conditions Met: ").append(formatBoolean(conditionManager.allConditionsMet(location))).append("\n");
        info.append(ChatColor.AQUA).append("Collision Enabled: ").append(formatBoolean(collisionEnabled));

        return info.toString();
    }

    private String formatBoolean(boolean value) {
        return value ? ChatColor.GREEN + "true" : ChatColor.RED + "false";
    }
}
