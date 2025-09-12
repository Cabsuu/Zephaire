package com.jerae.zephaire.particles.util;

import com.jerae.zephaire.Zephaire;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

/**
 * A utility class for parsing particle-related data from configurations.
 */
public final class ParticleUtils {

    private static final Zephaire plugin = JavaPlugin.getPlugin(Zephaire.class);

    public static Location parseLocation(World world, ConfigurationSection section) {
        if (section == null) return new Location(world, 0, 0, 0);
        return new Location(world, section.getDouble("x"), section.getDouble("y"), section.getDouble("z"));
    }

    public static Location parseLocation(World defaultWorld, Map<?, ?> map) {
        if (map == null) return new Location(defaultWorld, 0, 0, 0);

        World world = defaultWorld;
        if (map.containsKey("world")) {
            World specifiedWorld = Bukkit.getWorld((String) map.get("world"));
            if (specifiedWorld != null) world = specifiedWorld;
        }

        double x = map.containsKey("x") ? ((Number) map.get("x")).doubleValue() : 0;
        double y = map.containsKey("y") ? ((Number) map.get("y")).doubleValue() : 0;
        double z = map.containsKey("z") ? ((Number) map.get("z")).doubleValue() : 0;

        return new Location(world, x, y, z);
    }

    public static Color hexToColor(String hex) {
        hex = hex.startsWith("#") ? hex.substring(1) : hex;
        try {
            int r = Integer.valueOf(hex.substring(0, 2), 16);
            int g = Integer.valueOf(hex.substring(2, 4), 16);
            int b = Integer.valueOf(hex.substring(4, 6), 16);
            return Color.fromRGB(r, g, b);
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            plugin.getLogger().warning("Invalid hex color format: '" + hex + "'. Using WHITE instead.");
            return Color.WHITE;
        }
    }

    public static Object parseParticleOptions(Particle particle, ConfigurationSection optionsSection) {
        if (optionsSection == null) return null;
        if (particle == Particle.DUST) {
            Color color = hexToColor(optionsSection.getString("color", "FFFFFF"));
            float size = (float) optionsSection.getDouble("size", 1.0);
            return new Particle.DustOptions(color, size);
        } else if (particle == Particle.DUST_COLOR_TRANSITION) {
            Color from = hexToColor(optionsSection.getString("from-color", "FFFFFF"));
            Color to = hexToColor(optionsSection.getString("to-color", "000000"));
            float size = (float) optionsSection.getDouble("size", 1.0);
            return new Particle.DustTransition(from, to, size);
        }
        return null;
    }
}
