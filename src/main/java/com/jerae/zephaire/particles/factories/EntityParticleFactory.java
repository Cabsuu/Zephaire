package com.jerae.zephaire.particles.factories;

import com.jerae.zephaire.particles.animations.entity.EntityParticleTask;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.data.EntityTarget;
import com.jerae.zephaire.particles.data.SpawnBehavior;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

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
     * @param offset The positional offset for the particle effect's appearance.
     * @param period The tick period for rendering the particle effect.
     * @param spawnBehavior The condition under which particles should spawn.
     * @param loopDelay The delay in ticks before the animation loops.
     * @param debug Whether to enable debug logging for this particle task.
     * @return An EntityParticleTask instance.
     */
    EntityParticleTask create(String effectName, ConfigurationSection section, EntityTarget target, ConditionManager manager, Vector offset, int period, SpawnBehavior spawnBehavior, int loopDelay, boolean debug);
}
