package com.jerae.zephaire.particles;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.animations.AnimatedParticle;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.conditions.ParticleCondition;
import com.jerae.zephaire.particles.factories.AnimatedParticleFactory;
import com.jerae.zephaire.particles.factories.StaticParticleFactory;
import com.jerae.zephaire.particles.factories.conditions.ConditionFactory;
import com.jerae.zephaire.particles.factories.decorators.DecoratorFactory;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Handles loading and parsing particle configurations from the plugin's config file.
 */
public class ParticleConfigLoader {

    private final Zephaire plugin;
    private final FactoryManager factoryManager;
    private final ParticleManager particleManager;

    public ParticleConfigLoader(Zephaire plugin, FactoryManager factoryManager, ParticleManager particleManager) {
        this.plugin = plugin;
        this.factoryManager = factoryManager;
        this.particleManager = particleManager;
    }

    /**
     * Loads all static and animated particles from the configuration file.
     */
    public void loadParticles() {
        loadParticleSection("static-particles");
        loadParticleSection("animated-particles");
    }

    private void loadParticleSection(String configSectionName) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection(configSectionName);
        if (section == null) {
            return;
        }

        boolean isAnimated = "animated-particles".equals(configSectionName);

        for (String key : section.getKeys(false)) {
            // On a full load/reload, check against the persistent disabled list
            if (plugin.getDataManager().isParticleDisabled(key)) {
                continue;
            }
            loadParticleFromSection(section, key, isAnimated);
        }
    }

    public boolean loadSingleParticle(String key) {
        ConfigurationSection staticSection = plugin.getConfig().getConfigurationSection("static-particles");
        if (staticSection != null && staticSection.isConfigurationSection(key)) {
            loadParticleFromSection(staticSection, key, false);
            // If the loaded particle was animated, we need to restart the manager if it wasn't running
            particleManager.startAnimationManager();
            return true;
        }

        ConfigurationSection animatedSection = plugin.getConfig().getConfigurationSection("animated-particles");
        if (animatedSection != null && animatedSection.isConfigurationSection(key)) {
            loadParticleFromSection(animatedSection, key, true);
            particleManager.startAnimationManager();
            return true;
        }

        return false;
    }

    private void loadParticleFromSection(ConfigurationSection section, String key, boolean isAnimated) {
        ConfigurationSection particleConfig = section.getConfigurationSection(key);
        if (particleConfig == null) {
            plugin.getLogger().warning("Invalid configuration for particle key '" + key + "' in '" + section.getName() + "'. Skipping.");
            return;
        }

        try {
            String shape = particleConfig.getString("shape", isAnimated ? "" : "POINT").toUpperCase();
            if (isAnimated && shape.isEmpty()) {
                plugin.getLogger().warning("Missing 'shape' for animated particle '" + key + "'. Skipping.");
                return;
            }

            World world = Bukkit.getWorld(particleConfig.getString("world", "world"));
            if (world == null) {
                plugin.getLogger().warning("Invalid world for particle '" + key + "'. Skipping.");
                return;
            }

            String particlePath = section.getName() + "." + key;
            ConditionManager manager = parseConditions(particleConfig, world, particlePath);

            if (isAnimated) {
                loadAnimatedParticle(key, shape, particleConfig, manager);
            } else {
                loadStaticParticle(key, shape, particleConfig, manager);
            }

        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "An unexpected error occurred while loading particle '" + key + "' in '" + section.getName() + "'.", e);
        }
    }


    private void loadAnimatedParticle(String key, String shape, ConfigurationSection config, ConditionManager condManager) {
        Optional<AnimatedParticleFactory> factoryOpt = factoryManager.getAnimatedFactory(shape);

        if (factoryOpt.isEmpty()) {
            plugin.getLogger().warning("Unknown animated shape: '" + shape + "' for key '" + key + "'. Skipping.");
            return;
        }

        AnimatedParticle baseParticle = factoryOpt.get().create(config, condManager);
        if (baseParticle != null) {
            AnimatedParticle decoratedParticle = applyDecorators(baseParticle, config, key);
            particleManager.addParticle(key, decoratedParticle);
        }
    }

    private void loadStaticParticle(String key, String shape, ConfigurationSection config, ConditionManager condManager) {
        Optional<StaticParticleFactory> factoryOpt = factoryManager.getStaticFactory(shape);

        if (factoryOpt.isEmpty()) {
            plugin.getLogger().warning("Unknown static shape: '" + shape + "' for key '" + key + "'. Skipping.");
            return;
        }

        BukkitRunnable task = factoryOpt.get().create(config, condManager);
        if (task instanceof Debuggable) {
            long period = config.getLong("period", shape.equals("POINT") ? 20L : 1L);
            task.runTaskTimerAsynchronously(plugin, 0L, period);
            particleManager.addParticle(key, (Debuggable) task);
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
            if(decoratorFactoryOpt.isPresent()) {
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


    private ConditionManager parseConditions(ConfigurationSection section, World defaultWorld, String particlePath) {
        List<ParticleCondition> conditions = new ArrayList<>();
        List<Map<?, ?>> conditionList = section.getMapList("conditions");

        for (Map<?, ?> conditionMap : conditionList) {
            try {
                String type = ((String) conditionMap.get("type")).toUpperCase();
                Optional<ConditionFactory> factoryOpt = factoryManager.getConditionFactory(type);
                if (factoryOpt.isPresent()) {
                    ParticleCondition condition = factoryOpt.get().create(conditionMap, defaultWorld, particlePath, factoryManager);
                    if (condition != null) {
                        conditions.add(condition);
                    } else {
                        plugin.getLogger().warning("Failed to create condition of type '" + type + "' in '" + particlePath + "'. Check its parameters.");
                    }
                } else {
                    plugin.getLogger().warning("Unknown condition type '" + type + "' in '" + particlePath + "'.");
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Invalid format for a condition in '" + particlePath + "'. Please check the configuration.", e);
            }
        }
        return new ConditionManager(conditions);
    }
}
