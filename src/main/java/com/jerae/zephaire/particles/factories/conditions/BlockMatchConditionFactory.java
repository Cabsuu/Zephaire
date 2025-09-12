package com.jerae.zephaire.particles.factories.conditions;

import com.jerae.zephaire.particles.managers.FactoryManager;
import com.jerae.zephaire.particles.conditions.BlockMatchCondition;
import com.jerae.zephaire.particles.conditions.ParticleCondition;
import com.jerae.zephaire.particles.util.ParticleUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Map;

public class BlockMatchConditionFactory implements ConditionFactory {
    @Override
    public ParticleCondition create(Map<?, ?> configMap, World defaultWorld, String particlePath, FactoryManager factoryManager) {
        try {
            Map<?, ?> locationMap = (Map<?, ?>) configMap.get("location");
            Location loc = ParticleUtils.parseLocation(defaultWorld, locationMap);
            Material material = Material.valueOf(((String) configMap.get("material")).toUpperCase());
            boolean triggerOnce = configMap.containsKey("trigger-once") && (boolean) configMap.get("trigger-once");
            return new BlockMatchCondition(loc, material, triggerOnce);
        } catch (Exception e) {
            // Optionally log an error here if parsing fails for this specific condition
            return null;
        }
    }
}
