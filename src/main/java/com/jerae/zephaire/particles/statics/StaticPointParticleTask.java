package com.jerae.zephaire.particles.statics;

import com.jerae.zephaire.particles.Debuggable;
import com.jerae.zephaire.particles.ParticleScheduler;
import com.jerae.zephaire.particles.ParticleSpawnData;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.managers.CollisionManager;
import com.jerae.zephaire.particles.managers.PerformanceManager;
import com.jerae.zephaire.particles.util.ParticleUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

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
    private final int despawnTimer;
    private final boolean hasGravity;
    private final double spread;

    public StaticPointParticleTask(Location location, Particle particle, int count, double offsetX, double offsetY, double offsetZ, double speed, Object particleOptions, ConditionManager conditionManager, boolean collisionEnabled, int despawnTimer, boolean hasGravity, double spread) {
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
        this.despawnTimer = despawnTimer;
        this.hasGravity = hasGravity;
        this.spread = spread;
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

        if (particle == null && particleOptions instanceof ItemStack) {
            if (spread > 0) {
                for (int i = 0; i < count; i++) {
                    Vector randomVelocity = new Vector(
                            ThreadLocalRandom.current().nextDouble(-1, 1),
                            ThreadLocalRandom.current().nextDouble(-1, 1),
                            ThreadLocalRandom.current().nextDouble(-1, 1)
                    ).normalize().multiply(spread);
                    ParticleScheduler.queueParticle(new ParticleSpawnData(location, (ItemStack) particleOptions, despawnTimer, hasGravity, randomVelocity));
                }
            } else {
                ParticleScheduler.queueParticle(new ParticleSpawnData(location, (ItemStack) particleOptions, despawnTimer, hasGravity));
            }
        } else if (particle != null) {
            if (particle == Particle.SHRIEK && particleOptions instanceof Integer) {
                ParticleScheduler.queueParticle(new ParticleSpawnData(particle, location, (Integer) particleOptions));
            } else if (particle == Particle.VIBRATION && particleOptions instanceof org.bukkit.Vibration) {
                ParticleScheduler.queueParticle(new ParticleSpawnData(particle, location, (org.bukkit.Vibration) particleOptions));
            } else if (particle == Particle.SCULK_CHARGE && particleOptions instanceof Float) {
                ParticleScheduler.queueParticle(new ParticleSpawnData(particle, location, (Float) particleOptions));
            } else if (particle == Particle.TRAIL && particleOptions instanceof Integer) {
                ParticleScheduler.queueParticle(new ParticleSpawnData(particle, location, (Integer) particleOptions, false));
            } else {
                ParticleScheduler.queueParticle(new ParticleSpawnData(
                        particle, location, count,
                        offsetX, offsetY, offsetZ,
                        speed, particleOptions
                ));
            }
        }
    }

    @Override
    public String getDebugInfo() {
        StringBuilder info = new StringBuilder();
        info.append(ChatColor.AQUA).append("Type: ").append(ChatColor.WHITE).append("STATIC").append("\n");
        info.append(ChatColor.AQUA).append("Shape: ").append(ChatColor.WHITE).append("POINT").append("\n");
        info.append(ChatColor.AQUA).append("Location: ").append(ChatColor.WHITE).append(String.format("%.2f, %.2f, %.2f", location.getX(), location.getY(), location.getZ())).append("\n");
        if (particle != null) {
            info.append(ChatColor.AQUA).append("Particle: ").append(ChatColor.WHITE).append(particle.name()).append("\n");
        } else {
            info.append(ChatColor.AQUA).append("Particle: ").append(ChatColor.WHITE).append("VISUAL_ITEM").append("\n");
            if (particleOptions instanceof ItemStack) {
                info.append(ChatColor.AQUA).append("Material: ").append(ChatColor.WHITE).append(((ItemStack) particleOptions).getType().name()).append("\n");
            }
        }
        info.append(ChatColor.AQUA).append("Count: ").append(ChatColor.WHITE).append(count).append("\n");
        info.append(ChatColor.DARK_AQUA).append("--- Status ---").append("\n");
        info.append(ChatColor.AQUA).append("Player Nearby: ").append(ParticleUtils.formatBoolean(PerformanceManager.isPlayerNearby(location))).append("\n");
        info.append(ChatColor.AQUA).append("Conditions Met: ").append(ParticleUtils.formatBoolean(conditionManager.allConditionsMet(location))).append("\n");
        info.append(ChatColor.AQUA).append("Collision Enabled: ").append(ParticleUtils.formatBoolean(collisionEnabled));

        return info.toString();
    }
}
