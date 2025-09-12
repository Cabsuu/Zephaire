package com.jerae.zephaire.particles.factories.conditions;

import com.jerae.zephaire.particles.FactoryManager;
import com.jerae.zephaire.particles.conditions.ParticleCondition;
import org.bukkit.World;

import java.util.Map;
import java.util.Optional;

/**
 * An interface for a factory that creates ParticleCondition instances.
 */
public interface ConditionFactory {
    /**
     * Creates a ParticleCondition based on the provided configuration map.
     *
     * @param configMap    The map containing the configuration for this specific condition.
     * @param defaultWorld The default world for the particle effect.
     * @param particlePath The configuration path of the parent particle, for logging.
     * @param factoryManager The manager for all factories, in case a condition needs to create another condition.
     * @return A new ParticleCondition instance, or null if parsing fails.
     */
    ParticleCondition create(Map<?, ?> configMap, World defaultWorld, String particlePath, FactoryManager factoryManager);
}
