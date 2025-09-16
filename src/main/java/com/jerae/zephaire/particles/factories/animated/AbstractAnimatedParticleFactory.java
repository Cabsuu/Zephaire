package com.jerae.zephaire.particles.factories.animated;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.ParticleSpawnData;
import com.jerae.zephaire.particles.animations.visual.IParticleRenderer;
import com.jerae.zephaire.particles.animations.visual.StandardParticleRenderer;
import com.jerae.zephaire.particles.animations.visual.VisualItemRenderer;
import com.jerae.zephaire.particles.factories.AbstractParticleFactory;
import com.jerae.zephaire.particles.util.ParticleUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.logging.Logger;

/**
 * Abstract factory for creating animated particle effects.
 * This class now handles the logic for creating the appropriate renderer (standard particle or visual item).
 */
public abstract class AbstractAnimatedParticleFactory extends AbstractParticleFactory {

    public AbstractAnimatedParticleFactory(Zephaire plugin) {
        super(plugin);
    }

    /**
     * Creates an IParticleRenderer based on the configuration.
     *
     * @param config The ConfigurationSection for the particle effect.
     * @return An IParticleRenderer instance.
     */
    protected IParticleRenderer createRenderer(ConfigurationSection config) {
        String type = config.getString("type", "FLAME").toUpperCase();
        Logger logger = plugin.getLogger();

        if (type.equals("VISUAL_ITEM")) {
            String materialName = config.getString("options.material");
            if (materialName == null) {
                logger.warning("Particle '" + config.getName() + "' is of type VISUAL_ITEM but has no material specified. Skipping.");
                return null;
            }

            try {
                Material material = Material.valueOf(materialName.toUpperCase());
                int despawnTimer = config.getInt("options.despawn-timer", 100);
                return new VisualItemRenderer(plugin, material, despawnTimer);
            } catch (IllegalArgumentException e) {
                logger.warning("Invalid material '" + materialName + "' for VISUAL_ITEM particle '" + config.getName() + "'. Skipping.");
                return null;
            }
        } else {
            // Default to standard particle rendering
            ParticleSpawnData spawnData = ParticleUtils.createParticleSpawnData(config);
            if (spawnData == null) {
                logger.warning("Could not create spawn data for particle '" + config.getName() + "'. Skipping.");
                return null;
            }
            return new StandardParticleRenderer(spawnData);
        }
    }
}
