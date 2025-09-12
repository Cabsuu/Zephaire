package com.jerae.zephaire.particles.factories.conditions;

import com.jerae.zephaire.particles.managers.FactoryManager;
import com.jerae.zephaire.particles.conditions.ParticleCondition;
import com.jerae.zephaire.particles.conditions.TimeCondition;
import com.jerae.zephaire.particles.util.ConfigValidator;
import org.bukkit.World;

import java.util.Map;

public class TimeConditionFactory implements ConditionFactory {
    @Override
    public ParticleCondition create(Map<?, ?> configMap, World defaultWorld, String particlePath, FactoryManager factoryManager) {
        long from = ConfigValidator.getLong(configMap, "from", 0L, particlePath);
        long to = ConfigValidator.getLong(configMap, "to", 12000L, particlePath);
        return new TimeCondition(from, to);
    }
}
