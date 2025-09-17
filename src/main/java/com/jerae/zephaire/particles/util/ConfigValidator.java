package com.jerae.zephaire.particles.util;

import com.jerae.zephaire.Zephaire;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.logging.Level;

/**
 * A utility class to safely read and validate values from a ConfigurationSection.
 */
public final class ConfigValidator {

    private static final Zephaire plugin = JavaPlugin.getPlugin(Zephaire.class);

    // --- Original Methods for ConfigurationSection ---

    public static double getPositiveDouble(ConfigurationSection section, String key, double defaultValue) {
        double value = section.getDouble(key, defaultValue);
        if (value <= 0) {
            plugin.getLogger().log(Level.WARNING, "Invalid value for '" + key + "' in '" + section.getCurrentPath() + "'. Must be positive. Using default: " + defaultValue);
            return defaultValue;
        }
        return value;
    }

    public static int getPositiveInt(ConfigurationSection section, String key, int defaultValue) {
        int value = section.getInt(key, defaultValue);
        if (value <= 0) {
            plugin.getLogger().log(Level.WARNING, "Invalid value for '" + key + "' in '" + section.getCurrentPath() + "'. Must be positive. Using default: " + defaultValue);
            return defaultValue;
        }
        return value;
    }

    public static Particle getParticleType(ConfigurationSection section, String key, String defaultValue) {
        String particleName = section.getString(key, defaultValue).toUpperCase();
        if (particleName.equals("VISUAL_ITEM")) {
            return null; // This is our custom type
        }
        try {
            return Particle.valueOf(particleName);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().log(Level.WARNING, "Invalid particle type '" + particleName + "' in '" + section.getCurrentPath() + "'. Using default: " + defaultValue);
            return Particle.valueOf(defaultValue);
        }
    }

    public static Sound getSound(ConfigurationSection section, String key, String defaultValue) {
        String soundName = section.getString(key, defaultValue).toLowerCase();
        NamespacedKey soundKey = NamespacedKey.minecraft(soundName);
        Sound sound = Registry.SOUNDS.get(soundKey);

        if (sound == null) {
            plugin.getLogger().log(Level.WARNING, "Invalid sound name '" + soundName + "' in '" + section.getCurrentPath() + "'. Using default: " + defaultValue);
            sound = Registry.SOUNDS.get(NamespacedKey.minecraft(defaultValue));
        }
        return sound;
    }


    // --- NEW: Overloaded Methods to work with Maps from getMapList ---

    public static int getPositiveInt(Map<?, ?> map, String key, int defaultValue, String particlePath) {
        if (!map.containsKey(key) || !(map.get(key) instanceof Number)) {
            return defaultValue;
        }
        int value = ((Number) map.get(key)).intValue();
        if (value <= 0) {
            plugin.getLogger().log(Level.WARNING, "Invalid value for '" + key + "' in '" + particlePath + "'. Must be positive. Using default: " + defaultValue);
            return defaultValue;
        }
        return value;
    }

    public static long getLong(Map<?, ?> map, String key, long defaultValue, String particlePath) {
        if (!map.containsKey(key) || !(map.get(key) instanceof Number)) {
            return defaultValue;
        }
        return ((Number) map.get(key)).longValue();
    }

    public static boolean getBoolean(Map<?, ?> map, String key, boolean defaultValue, String particlePath) {
        if (!map.containsKey(key) || !(map.get(key) instanceof Boolean)) {
            return defaultValue;
        }
        return (boolean) map.get(key);
    }

    public static String getString(Map<?, ?> map, String key, String defaultValue, String particlePath) {
        if (!map.containsKey(key) || !(map.get(key) instanceof String)) {
            return defaultValue;
        }
        return (String) map.get(key);
    }
}
