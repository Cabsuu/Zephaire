package com.jerae.zephaire.particles.loaders;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.managers.FactoryManager;
import com.jerae.zephaire.particles.managers.ParticleManager;
import com.jerae.zephaire.particles.animations.AnimatedParticle;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.factories.AnimatedParticleFactory;
import com.jerae.zephaire.particles.factories.decorators.DecoratorFactory;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

/**
 * Handles the loading, decorating, and instantiation of animated particle effects.
 */
public class AnimatedParticleLoader {

    private final Zephaire plugin;
    private final FactoryManager factoryManager;
    private final ParticleManager particleManager;

    public AnimatedParticleLoader(Zephaire plugin, FactoryManager factoryManager, ParticleManager particleManager) {
        this.plugin = plugin;
        this.factoryManager = factoryManager;
        this.particleManager = particleManager;
    }

    public void load(String key, String shape, ConfigurationSection config, ConditionManager condManager) {
        Optional<AnimatedParticleFactory> factoryOpt = factoryManager.getAnimatedFactory(shape);

        if (factoryOpt.isEmpty()) {
            plugin.getLogger().warning("Unknown animated shape: '" + shape + "' for key '" + key + "'. Skipping.");
            return;
        }

        AnimatedParticle baseParticle = factoryOpt.get().create(plugin, config, condManager);
        if (baseParticle != null) {
            AnimatedParticle decoratedParticle = applyDecorators(baseParticle, config, key);
            particleManager.addParticle(key, decoratedParticle);
        }
    }

    private AnimatedParticle applyDecorators(AnimatedParticle baseParticle, ConfigurationSection config, String particleKey) {
        ConfigurationSection decoratorsSection = config.getConfigurationSection("decorators");
        if (decoratorsSection == null) {
            return baseParticle;
        }

        AnimatedParticle currentParticle = baseParticle;
        for (String decoratorKey : decoratorsSection.getKeys(false)) {
            Optional<DecoratorFactory> decoratorFactoryOpt = factoryManager.getDecoratorFactory(decoratorKey);
            if (decoratorFactoryOpt.isPresent()) {
                ConfigurationSection decoratorConfig = decoratorsSection.getConfigurationSection(decoratorKey);
                if (decoratorConfig != null) {
                    currentParticle = decoratorFactoryOpt.get().create(currentParticle, decoratorConfig);
                } else {
                    plugin.getLogger().warning("Invalid configuration for decorator '" + decoratorKey + "' for particle '" + particleKey + "'.");
                }
            } else {
                plugin.getLogger().warning("Unknown decorator '" + decoratorKey + "' for particle '" + particleKey + "'.");
            }
        }
        return currentParticle;
    }
}
