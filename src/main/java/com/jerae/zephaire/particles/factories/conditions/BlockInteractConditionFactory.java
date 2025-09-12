package com.jerae.zephaire.particles.factories.conditions;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.FactoryManager;
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

        boolean isToggle = ConfigValidator.getBoolean(configMap, "toggle", false, particlePath);

        // --- Declare variables for all possible settings ---
        boolean triggerOnce = false;
        long repeatDuration = 0;
        Material requiredItem = null;
        Material activationItem = null;
        Material deactivationItem = null;

        // --- Parse settings based on the mode ---
        if (isToggle) {
            // --- TOGGLE MODE ---
            activationItem = parseMaterial(configMap, "activation-item", particlePath, true);
            deactivationItem = parseMaterial(configMap, "deactivation-item", particlePath, true);
            if (activationItem == null || deactivationItem == null) {
                JavaPlugin.getPlugin(Zephaire.class).getLogger().log(Level.WARNING, "Toggle interaction in '" + particlePath + "' requires both 'activation-item' and 'deactivation-item'.");
                return null;
            }
        } else {
            // --- TRIGGER MODE ---
            triggerOnce = ConfigValidator.getBoolean(configMap, "trigger-once", false, particlePath);
            // This was the line causing the error. It now correctly calls the 4-argument version.
            repeatDuration = ConfigValidator.getPositiveInt(configMap, "repeat-duration", 0, particlePath);
            requiredItem = parseMaterial(configMap, "required-item", particlePath, false); // Not required for trigger
        }

        BlockInteractCondition condition = new BlockInteractCondition(loc, material, isToggle, triggerOnce, repeatDuration, requiredItem, activationItem, deactivationItem);
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
