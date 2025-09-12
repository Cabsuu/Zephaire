package com.jerae.zephaire.particles.factories;

import com.jerae.zephaire.particles.animations.AnimatedParticle;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Interface for a factory that creates animated particle effects.
 */
public interface AnimatedParticleFactory {
    /**
     * Creates an AnimatedParticle based on the configuration.
     *
     * @param section The ConfigurationSection for the specific particle effect.
     * @param manager The ConditionManager for this effect.
     * @return An AnimatedParticle instance.
     */
    AnimatedParticle create(ConfigurationSection section, ConditionManager manager);
}
