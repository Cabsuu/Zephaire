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
import com.jerae.zephaire.particles.data.ParticleCreationData;
import java.util.HashMap;
import java.util.Map;

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
    private final Map<String, ParticleCreationData> creationDataCache = new HashMap<>();
    private final WorldProvider worldProvider;

    public ParticleConfigLoader(Zephaire plugin, FactoryManager factoryManager, ParticleManager particleManager, EntityParticleManager entityParticleManager) {
        this(plugin, factoryManager, particleManager, entityParticleManager, Bukkit::getWorld);
    }

    public ParticleConfigLoader(Zephaire plugin, FactoryManager factoryManager, ParticleManager particleManager, EntityParticleManager entityParticleManager, WorldProvider worldProvider) {
        this.plugin = plugin;
        this.staticLoader = new StaticParticleLoader(plugin, factoryManager, particleManager);
        this.animatedLoader = new AnimatedParticleLoader(plugin, factoryManager, particleManager);
        this.entityLoader = new EntityParticleLoader(plugin, factoryManager, entityParticleManager);
        this.conditionParser = new ConditionParser(plugin, factoryManager);
        this.worldProvider = worldProvider;
    }

    public void clearCache() {
        creationDataCache.clear();
    }

    /**
     * Loads all static, animated, and entity-targeted particles from the configuration files.
     */
    public void loadParticles() {
        if (plugin.isStaticParticlesEnabled()) {
            loadParticleSection("static-particles");
        }
        if (plugin.isAnimatedParticlesEnabled()) {
            loadParticleSection("animated-particles");
        }
        if (plugin.isEntityParticlesEnabled()) {
            loadEntityParticleSection();
        }
    }

    private void loadParticleSection(String configSectionName) {
        ConfigurationSection section = getSection(configSectionName);
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
            String cacheKey = "entity-particles." + key;
            ParticleCreationData data = creationDataCache.get(cacheKey);

            if (data == null) {
                ConfigurationSection particleConfig = section.getConfigurationSection(key);
                if (particleConfig == null) {
                    plugin.getLogger().warning("Invalid configuration for entity particle key '" + key + "'. Skipping.");
                    continue;
                }
                try {
                    String particlePath = "entity-particles." + key;
                    ConditionManager manager = conditionParser.parse(particleConfig, null, particlePath);
                    data = new ParticleCreationData(key, null, particleConfig, manager, false);
                    creationDataCache.put(cacheKey, data);
                } catch (Exception e) {
                    plugin.getLogger().log(Level.SEVERE, "An unexpected error occurred while loading entity particle '" + key + "'.", e);
                    continue;
                }
            }
            entityLoader.load(data.key(), data.config(), data.condManager());
        }
    }


    private ConfigurationSection getSection(String configSectionName) {
        return switch (configSectionName) {
            case "animated-particles" -> plugin.getAnimatedParticlesConfig().getConfigurationSection(configSectionName);
            case "static-particles" -> plugin.getStaticParticlesConfig().getConfigurationSection(configSectionName);
            case "entity-particles" -> plugin.getEntityParticlesConfig().getConfigurationSection(configSectionName);
            default -> null;
        };
    }

    public boolean loadSingleParticle(String key) {
        ConfigurationSection staticSection = getSection("static-particles");
        if (staticSection != null && staticSection.isConfigurationSection(key)) {
            loadParticleFromSection(staticSection, key, false);
            plugin.getParticleManager().startAnimationManager();
            return true;
        }

        ConfigurationSection animatedSection = getSection("animated-particles");
        if (animatedSection != null && animatedSection.isConfigurationSection(key)) {
            loadParticleFromSection(animatedSection, key, true);
            plugin.getParticleManager().startAnimationManager();
            return true;
        }

        return false;
    }

    private void loadParticleFromSection(ConfigurationSection section, String key, boolean isAnimated) {
        String cacheKey = section.getName() + "." + key;
        ParticleCreationData data = creationDataCache.get(cacheKey);

        if (data == null) {
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

                World world = worldProvider.getWorld(particleConfig.getString("world", "world"));
                if (world == null) {
                    plugin.getLogger().warning("Invalid world for particle '" + key + "'. Skipping.");
                    return;
                }

                String particlePath = section.getName() + "." + key;
                ConditionManager manager = conditionParser.parse(particleConfig, world, particlePath);

                data = new ParticleCreationData(key, shape, particleConfig, manager, isAnimated);
                creationDataCache.put(cacheKey, data);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "An unexpected error occurred while loading particle '" + key + "' in '" + section.getName() + "'.", e);
                return;
            }
        }

        if (data.isAnimated()) {
            animatedLoader.load(data.key(), data.shape(), data.config(), data.condManager());
        } else {
            staticLoader.load(data.key(), data.shape(), data.config(), data.condManager());
        }
    }
}