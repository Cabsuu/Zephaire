package com.jerae.zephaire.particles.factories.conditions;

import com.jerae.zephaire.particles.FactoryManager;
import com.jerae.zephaire.particles.conditions.ParticleCondition;
import com.jerae.zephaire.particles.conditions.TimerCondition;
import com.jerae.zephaire.particles.util.ConfigValidator;
import org.bukkit.World;

import java.util.Map;

public class TimerConditionFactory implements ConditionFactory {
    @Override
    public ParticleCondition create(Map<?, ?> configMap, World defaultWorld, String particlePath, FactoryManager factoryManager) {
        long activeDuration = ConfigValidator.getLong(configMap, "active-duration", 40L, particlePath);
        long cooldownDuration = ConfigValidator.getLong(configMap, "cooldown-duration", 60L, particlePath);
        return new TimerCondition(activeDuration, cooldownDuration);
    }
}
