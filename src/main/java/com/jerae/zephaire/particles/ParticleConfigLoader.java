package com.jerae.zephaire.particles;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.loaders.AnimatedParticleLoader;
import com.jerae.zephaire.particles.loaders.ConditionParser;
import com.jerae.zephaire.particles.loaders.EntityParticleLoader;
import com.jerae.zephaire.particles.loaders.StaticParticleLoader;
import com.jerae.zephaire.particles.managers.EntityParticleManager;
import com.jerae.zephaire.particles.managers.FactoryManager;
import com.jerae.zephaire.particles.managers.ParticleManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.logging.Level;

/**
 * Handles loading and parsing particle configurations from the plugin's config file.
 */
public class ParticleConfigLoader {

    private final Zephaire plugin;
    private final StaticParticleLoader staticLoader;
    private final AnimatedParticleLoader animatedLoader;
    private final EntityParticleLoader entityLoader;
    private final ConditionParser conditionParser;

    public ParticleConfigLoader(Zephaire plugin, FactoryManager factoryManager, ParticleManager particleManager, EntityParticleManager entityParticleManager) {
        this.plugin = plugin;
        this.staticLoader = new StaticParticleLoader(plugin, factoryManager, particleManager);
        this.animatedLoader = new AnimatedParticleLoader(plugin, factoryManager, particleManager);
        this.entityLoader = new EntityParticleLoader(plugin, factoryManager, entityParticleManager);
        this.conditionParser = new ConditionParser(plugin, factoryManager);
    }

    /**
     * Loads all static, animated, and entity-targeted particles from the configuration files.
     */
    public void loadParticles() {
        loadParticleSection("static-particles");
        loadParticleSection("animated-particles");
        loadEntityParticleSection();
    }

    private void loadParticleSection(String configSectionName) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection(configSectionName);
        if (section == null) {
            return;
        }

        boolean isAnimated = "animated-particles".equals(configSectionName);

        for (String key : section.getKeys(false)) {
            if (plugin.getDataManager().isParticleDisabled(key)) {
                continue;
            }
            loadParticleFromSection(section, key, isAnimated);
        }
    }

    private void loadEntityParticleSection() {
        // Use the dedicated entityParticlesConfig instead of the main config
        ConfigurationSection section = plugin.getEntityParticlesConfig().getConfigurationSection("entity-particles");
        if (section == null) {
            return;
        }

        plugin.getLogger().info("Loading entity particle templates...");
        for (String key : section.getKeys(false)) {
            ConfigurationSection particleConfig = section.getConfigurationSection(key);
            if (particleConfig == null) {
                plugin.getLogger().warning("Invalid configuration for entity particle key '" + key + "'. Skipping.");
                continue;
            }
            try {
                entityLoader.load(key, particleConfig);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "An unexpected error occurred while loading entity particle '" + key + "'.", e);
            }
        }
    }


    public boolean loadSingleParticle(String key) {
        ConfigurationSection staticSection = plugin.getConfig().getConfigurationSection("static-particles");
        if (staticSection != null && staticSection.isConfigurationSection(key)) {
            loadParticleFromSection(staticSection, key, false);
            plugin.getParticleManager().startAnimationManager();
            return true;
        }

        ConfigurationSection animatedSection = plugin.getConfig().getConfigurationSection("animated-particles");
        if (animatedSection != null && animatedSection.isConfigurationSection(key)) {
            loadParticleFromSection(animatedSection, key, true);
            plugin.getParticleManager().startAnimationManager();
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
            ConditionManager manager = conditionParser.parse(particleConfig, world, particlePath);

            if (isAnimated) {
                animatedLoader.load(key, shape, particleConfig, manager);
            } else {
                staticLoader.load(key, shape, particleConfig, manager);
            }

        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "An unexpected error occurred while loading particle '" + key + "' in '" + section.getName() + "'.", e);
        }
    }
}