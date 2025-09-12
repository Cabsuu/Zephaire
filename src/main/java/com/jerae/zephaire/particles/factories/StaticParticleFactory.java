package com.jerae.zephaire.particles.factories;

import com.jerae.zephaire.particles.conditions.ConditionManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Interface for a factory that creates static particle effect tasks.
 */
public interface StaticParticleFactory {
    /**
     * Creates a BukkitRunnable for a static particle effect based on the configuration.
     *
     * @param section The ConfigurationSection for the specific particle effect.
     * @param manager The ConditionManager for this effect.
     * @return A BukkitRunnable that will run the static particle effect.
     */
    BukkitRunnable create(ConfigurationSection section, ConditionManager manager);
}
