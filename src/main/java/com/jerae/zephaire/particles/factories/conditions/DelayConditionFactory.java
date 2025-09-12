package com.jerae.zephaire.particles.factories.conditions;

import com.jerae.zephaire.particles.managers.FactoryManager;
import com.jerae.zephaire.particles.conditions.DelayConditionDecorator;
import com.jerae.zephaire.particles.conditions.ParticleCondition;
import org.bukkit.World;

import java.util.Map;
import java.util.Optional;

public class DelayConditionFactory implements ConditionFactory {
    @Override
    public ParticleCondition create(Map<?, ?> configMap, World defaultWorld, String particlePath, FactoryManager factoryManager) {
        try {
            long delay = ((Number) configMap.get("delay")).longValue();

            // Get the nested condition's configuration
            Map<?, ?> nestedConfig = (Map<?, ?>) configMap.get("condition");
            if (nestedConfig == null) return null;

            String nestedType = ((String) nestedConfig.get("type")).toUpperCase();

            // Find the factory for the nested condition using the provided manager
            Optional<ConditionFactory> nestedFactoryOpt = factoryManager.getConditionFactory(nestedType);
            if (nestedFactoryOpt.isEmpty()) return null;

            // Create the nested condition that we are going to wrap
            ParticleCondition wrappedCondition = nestedFactoryOpt.get().create(nestedConfig, defaultWorld, particlePath, factoryManager);
            if (wrappedCondition == null) return null;

            // Return the new decorator that wraps the nested condition
            return new DelayConditionDecorator(wrappedCondition, delay);
        } catch (Exception e) {
            return null;
        }
    }
}
