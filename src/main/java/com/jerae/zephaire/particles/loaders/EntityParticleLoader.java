package com.jerae.zephaire.particles.loaders;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.data.EntityTarget;
import com.jerae.zephaire.particles.data.SpawnBehavior;
import com.jerae.zephaire.particles.factories.EntityParticleFactory;
import com.jerae.zephaire.particles.managers.EntityParticleManager;
import com.jerae.zephaire.particles.managers.FactoryManager;
import com.jerae.zephaire.particles.animations.entity.EntityParticleTask;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

        Vector offset = new Vector(
                config.getDouble("offset-x", 0),
                config.getDouble("offset-y", 0),
                config.getDouble("offset-z", 0)
        );
        int period = config.getInt("period", 1);

        SpawnBehavior spawnBehavior = SpawnBehavior.ALWAYS;
        if (config.contains("spawn-condition")) {
            try {
                spawnBehavior = SpawnBehavior.valueOf(config.getString("spawn-condition", "ALWAYS").toUpperCase());
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid spawn-condition in '" + key + "'. Using ALWAYS.");
            }
        }

        EntityParticleTask task = factoryOpt.get().create(key, config, target, null, offset, period, spawnBehavior);

        if (task != null) {
            entityParticleManager.addEffectTemplate(key, task);
        }
    }

    private EntityTarget parseEntityTarget(ConfigurationSection targetSection) {
        if (targetSection == null) return null;

        String typeStr = targetSection.getString("type", "").toUpperCase();
        List<String> names = null;
        if (targetSection.isList("names")) {
            names = targetSection.getStringList("names");
        } else if (targetSection.isString("name")) {
            names = Collections.singletonList(targetSection.getString("name"));
        }

        String tag = targetSection.getString("tag");

        EntityTarget.TargetType targetType;
        EntityType entityType = null;

        switch (typeStr) {
            case "ALL_HOSTILE_MOBS":
                targetType = EntityTarget.TargetType.ALL_HOSTILE_MOBS;
                break;
            case "PLAYER":
                targetType = (names != null && !names.isEmpty()) ? EntityTarget.TargetType.SPECIFIC_PLAYERS : EntityTarget.TargetType.SPECIFIC_TYPE;
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

        return new EntityTarget(targetType, entityType, names, tag);
    }
}

