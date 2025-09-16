package com.jerae.zephaire.particles;

import com.jerae.zephaire.Zephaire;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * An abstract base class for all particle tasks, both static and animated.
 * It now handles its own scheduling.
 */
public abstract class AbstractParticleTask extends BukkitRunnable {

    private final Zephaire plugin;
    private final long period;
    private BukkitTask bukkitTask;

    public AbstractParticleTask(Zephaire plugin, long period) {
        this.plugin = plugin;
        this.period = period;
    }

    /**
     * Starts the particle task using the Bukkit scheduler.
     */
    public void start() {
        if (bukkitTask == null) {
            this.bukkitTask = this.runTaskTimer(plugin, 0L, period);
        }
    }

    /**
     * Stops the particle task.
     */
    public void stop() {
        if (bukkitTask != null) {
            try {
                this.bukkitTask.cancel();
            } catch (IllegalStateException ignored) {
                // Task may have already been cancelled.
            }
            bukkitTask = null;
        }
    }
}
