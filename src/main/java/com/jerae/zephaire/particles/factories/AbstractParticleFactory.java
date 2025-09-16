package com.jerae.zephaire.particles.factories;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.animations.AnimatedParticle;
import org.bukkit.configuration.ConfigurationSection;

/**
 * An abstract base for all particle factories.
 * It holds a reference to the main plugin instance.
 */
public abstract class AbstractParticleFactory {

    protected final Zephaire plugin;

    public AbstractParticleFactory(Zephaire plugin) {
        this.plugin = plugin;
    }

    public abstract AnimatedParticle createParticle(ConfigurationSection config);
}
