package com.jerae.zephaire.particles.factories.statics;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.AbstractParticleTask;
import com.jerae.zephaire.particles.factories.AbstractParticleFactory;
import org.bukkit.configuration.ConfigurationSection;

public abstract class AbstractStaticParticleFactory extends AbstractParticleFactory {

    public AbstractStaticParticleFactory(Zephaire plugin) {
        super(plugin);
    }

    /**
     * Creates a static particle task from a configuration section.
     * Subclasses will implement this to define specific shapes and behaviors.
     *
     * @param config The configuration section for the particle.
     * @return An AbstractParticleTask to render the static effect, or null if invalid.
     */
    public abstract AbstractParticleTask createParticleTask(ConfigurationSection config);

    /**
     * Note: This method is inherited from AbstractParticleFactory but is not used
     * for static particles, as they do not use the AnimatedParticle wrapper.
     * We return null to fulfill the abstract contract.
     */
    @Override
    public com.jerae.zephaire.particles.animations.AnimatedParticle createParticle(ConfigurationSection config) {
        // Static factories do not create AnimatedParticle objects.
        return null;
    }
}
