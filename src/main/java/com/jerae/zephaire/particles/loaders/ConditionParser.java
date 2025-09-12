package com.jerae.zephaire.particles.loaders;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.managers.FactoryManager;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.conditions.ParticleCondition;
import com.jerae.zephaire.particles.factories.conditions.ConditionFactory;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Handles parsing of condition sections from a configuration.
 */
public class ConditionParser {

    private final Zephaire plugin;
    private final FactoryManager factoryManager;

    public ConditionParser(Zephaire plugin, FactoryManager factoryManager) {
        this.plugin = plugin;
        this.factoryManager = factoryManager;
    }

    public ConditionManager parse(ConfigurationSection section, World defaultWorld, String particlePath) {
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
