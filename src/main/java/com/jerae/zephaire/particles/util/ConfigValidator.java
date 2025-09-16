package com.jerae.zephaire.particles.util;

import com.jerae.zephaire.Zephaire;
import org.bukkit.configuration.ConfigurationSection;

public class ConfigValidator {

    /**
     * Validates that a configuration section contains all the required keys.
     *
     * @param plugin The main plugin instance, used for logging.
     * @param config The configuration section to check.
     * @param requiredKeys A list of keys that must be present.
     * @return true if all keys are present, false otherwise.
     */
    public static boolean validateKeys(Zephaire plugin, ConfigurationSection config, String... requiredKeys) {
        for (String key : requiredKeys) {
            if (!config.contains(key, true)) { // Use deep search
                plugin.getLogger().warning("Missing required key '" + key + "' in config section: " + config.getName());
                return false;
            }
        }
        return true;
    }
}

