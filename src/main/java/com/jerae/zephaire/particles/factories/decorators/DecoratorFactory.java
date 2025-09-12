package com.jerae.zephaire.particles.factories.decorators;

import com.jerae.zephaire.particles.animations.AnimatedParticle;
import org.bukkit.configuration.ConfigurationSection;

/**
 * An interface for a factory that creates and wraps an AnimatedParticle with a decorator.
 */
public interface DecoratorFactory {
    /**
     * Creates a new decorated AnimatedParticle.
     *
     * @param wrappedParticle The base AnimatedParticle to wrap and add functionality to.
     * @param section         The ConfigurationSection for this specific decorator.
     * @return The new, decorated AnimatedParticle.
     */
    AnimatedParticle create(AnimatedParticle wrappedParticle, ConfigurationSection section);
}
