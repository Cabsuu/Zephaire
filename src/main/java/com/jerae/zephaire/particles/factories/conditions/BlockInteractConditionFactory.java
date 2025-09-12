package com.jerae.zephaire.particles.factories.conditions;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.managers.FactoryManager;
import com.jerae.zephaire.particles.ParticleRegistry;
import com.jerae.zephaire.particles.conditions.BlockInteractCondition;
import com.jerae.zephaire.particles.conditions.ParticleCondition;
import com.jerae.zephaire.particles.util.ConfigValidator;
import com.jerae.zephaire.particles.util.ParticleUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.logging.Level;

public class BlockInteractConditionFactory implements ConditionFactory {
    @Override
    public ParticleCondition create(Map<?, ?> configMap, World defaultWorld, String particlePath, FactoryManager factoryManager) {
        Object locationObj = configMap.get("location");
        if (!(locationObj instanceof Map)) {
            JavaPlugin.getPlugin(Zephaire.class).getLogger().log(Level.WARNING, "Missing 'location' for BLOCK_INTERACT in '" + particlePath + "'.");
            return null;
        }
        Location loc = ParticleUtils.parseLocation(defaultWorld, (Map<?, ?>) locationObj);

        Material material = parseMaterial(configMap, "material", particlePath, true);
        if (material == null) return null;

        boolean triggerOnce = ConfigValidator.getBoolean(configMap, "trigger-once", false, particlePath);
        long repeatDuration = ConfigValidator.getPositiveInt(configMap, "repeat-duration", 0, particlePath);
        Material requiredItem = parseMaterial(configMap, "required-item", particlePath, false); // Not required for trigger

        BlockInteractCondition condition = new BlockInteractCondition(loc, material, triggerOnce, repeatDuration, requiredItem);
        ParticleRegistry.registerBlockInteractCondition(condition);
        return condition;
    }

    private Material parseMaterial(Map<?, ?> map, String key, String path, boolean isRequired) {
        String matName = ConfigValidator.getString(map, key, null, path);
        if (matName == null) {
            if (isRequired) {
                JavaPlugin.getPlugin(Zephaire.class).getLogger().log(Level.WARNING, "Missing required material setting '" + key + "' in '" + path + "'.");
            }
            return null;
        }
        try {
            return Material.valueOf(matName.toUpperCase());
        } catch (IllegalArgumentException e) {
            JavaPlugin.getPlugin(Zephaire.class).getLogger().log(Level.WARNING, "Invalid material '" + matName + "' for '" + key + "' in '" + path + "'.");
            return null;
        }
    }
}
