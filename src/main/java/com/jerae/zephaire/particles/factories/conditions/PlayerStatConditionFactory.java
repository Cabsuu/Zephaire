package com.jerae.zephaire.particles.factories.conditions;

import com.jerae.zephaire.particles.conditions.ParticleCondition;
import com.jerae.zephaire.particles.conditions.PlayerStatCondition;
import com.jerae.zephaire.particles.managers.FactoryManager;
import org.bukkit.World;
import java.util.Map;

public class PlayerStatConditionFactory implements ConditionFactory {

    @Override
    public ParticleCondition create(Map<?, ?> configMap, World defaultWorld, String particlePath, FactoryManager factoryManager) {
        Object statObj = configMap.get("stat");
        Object comparisonObj = configMap.get("comparison");
        Object valueObj = configMap.get("value");

        if (statObj == null || comparisonObj == null || valueObj == null) {
            return null;
        }

        String statStr = String.valueOf(statObj).toUpperCase();
        String comparisonStr = String.valueOf(comparisonObj).toUpperCase();
        double value = 0.0;
        if (valueObj instanceof Number) {
            value = ((Number) valueObj).doubleValue();
        }

        PlayerStatCondition.Stat stat;
        try {
            stat = PlayerStatCondition.Stat.valueOf(statStr);
        } catch (IllegalArgumentException e) {
            return null;
        }

        PlayerStatCondition.Comparison comparison;
        try {
            comparison = PlayerStatCondition.Comparison.valueOf(comparisonStr);
        } catch (IllegalArgumentException e) {
            return null;
        }

        return new PlayerStatCondition(stat, comparison, value);
    }
}
