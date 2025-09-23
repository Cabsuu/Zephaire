package com.jerae.zephaire.particles.factories.conditions;

import com.jerae.zephaire.particles.conditions.ElytraCondition;
import com.jerae.zephaire.particles.conditions.ParticleCondition;
import com.jerae.zephaire.particles.managers.FactoryManager;
import org.bukkit.World;

import java.util.Map;

public class ElytraConditionFactory implements ConditionFactory {
    @Override
    public ParticleCondition create(Map<?, ?> configMap, World defaultWorld, String particlePath, FactoryManager factoryManager) {
        return new ElytraCondition();
    }
}
