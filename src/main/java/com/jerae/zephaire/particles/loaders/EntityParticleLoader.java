package com.jerae.zephaire.particles.loaders;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.managers.EntityParticleManager;
import com.jerae.zephaire.particles.managers.FactoryManager;
import com.jerae.zephaire.particles.animations.entity.EntityParticleTask;
import com.jerae.zephaire.particles.data.EntityTarget;
import com.jerae.zephaire.particles.factories.EntityParticleFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.Optional;

/**
 * Handles the loading and instantiation of entity-targeted particle effects.
 */
public class EntityParticleLoader {

    private final Zephaire plugin;
    private final FactoryManager factoryManager;
    private final EntityParticleManager entityParticleManager;

    public EntityParticleLoader(Zephaire plugin, FactoryManager factoryManager, EntityParticleManager entityParticleManager) {
        this.plugin = plugin;
        this.factoryManager = factoryManager;
        this.entityParticleManager = entityParticleManager;
    }

    public void load(String key, ConfigurationSection config) {
        String shape = config.getString("shape", "").toUpperCase();
        if (shape.isEmpty()) {
            plugin.getLogger().warning("Missing 'shape' for entity particle '" + key + "'. Skipping.");
            return;
        }

        Optional<EntityParticleFactory> factoryOpt = factoryManager.getEntityFactory(shape);
        if (factoryOpt.isEmpty()) {
            plugin.getLogger().warning("Unknown shape '" + shape + "' for entity particle '" + key + "'. Skipping.");
            return;
        }

        EntityTarget target = parseEntityTarget(config.getConfigurationSection("target"));
        if (target == null) {
            plugin.getLogger().warning("Invalid or missing 'target' section for entity particle '" + key + "'. Skipping.");
            return;
        }

        // Conditions for entity particles are currently not supported but could be added here.
        EntityParticleTask task = factoryOpt.get().create(key, config, target, null);

        if (task != null) {
            entityParticleManager.addEffectTemplate(key, task);
        }
    }

    private EntityTarget parseEntityTarget(ConfigurationSection targetSection) {
        if (targetSection == null) return null;

        String typeStr = targetSection.getString("type", "").toUpperCase();
        String name = targetSection.getString("name");
        Vector offset = new Vector(
                targetSection.getDouble("offset.x", 0),
                targetSection.getDouble("offset.y", 0),
                targetSection.getDouble("offset.z", 0)
        );

        EntityTarget.TargetType targetType;
        EntityType entityType = null;

        switch (typeStr) {
            case "ALL_PLAYERS":
                targetType = EntityTarget.TargetType.ALL_PLAYERS;
                break;
            case "ALL_HOSTILE_MOBS":
                targetType = EntityTarget.TargetType.ALL_HOSTILE_MOBS;
                break;
            case "PLAYER":
                targetType = name != null ? EntityTarget.TargetType.SPECIFIC_PLAYER : EntityTarget.TargetType.SPECIFIC_TYPE;
                entityType = EntityType.PLAYER;
                break;
            default:
                try {
                    entityType = EntityType.valueOf(typeStr);
                    targetType = EntityTarget.TargetType.SPECIFIC_TYPE;
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid entity type '" + typeStr + "' in target section.");
                    return null;
                }
        }

        return new EntityTarget(targetType, entityType, name, offset);
    }
}
