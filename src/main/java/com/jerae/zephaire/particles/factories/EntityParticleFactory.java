package com.jerae.zephaire.particles.factories;

import com.jerae.zephaire.particles.animations.entity.EntityParticleTask;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.data.EntityTarget;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Interface for a factory that creates entity-targeted particle effects.
 */
public interface EntityParticleFactory {
    /**
     * Creates an EntityParticleTask based on the configuration.
     *
     * @param effectName The name of the effect from the config.
     * @param section The ConfigurationSection for the specific particle effect.
     * @param target The parsed entity target data.
     * @param manager The ConditionManager for this effect (currently unused, for future expansion).
     * @return An EntityParticleTask instance.
     */
    EntityParticleTask create(String effectName, ConfigurationSection section, EntityTarget target, ConditionManager manager);
}
