package com.jerae.zephaire.particles.factories.conditions;

import com.jerae.zephaire.particles.ParticleRegistry;
import com.jerae.zephaire.particles.conditions.AnvilCondition;
import com.jerae.zephaire.particles.conditions.ParticleCondition;
import com.jerae.zephaire.particles.managers.FactoryManager;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public class AnvilConditionFactory implements ConditionFactory {
    @Override
    public ParticleCondition create(Map<?, ?> settings, World world, String particleName, FactoryManager factoryManager) {
        AnvilCondition condition = new AnvilCondition();
        ParticleRegistry.registerAnvilCondition(condition);
        return condition;
    }
}
