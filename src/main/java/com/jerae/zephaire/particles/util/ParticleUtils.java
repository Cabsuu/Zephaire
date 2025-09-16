package com.jerae.zephaire.particles.util;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.ParticleSpawnData;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

public class ParticleUtils {

    /**
     * Creates a ParticleSpawnData object from a configuration section.
     * This method parses particle types and their specific options (like color or material).
     *
     * @param config The configuration section for the particle.
     * @return A new ParticleSpawnData object, or null if the particle type is invalid.
     */
    public static ParticleSpawnData createParticleSpawnData(ConfigurationSection config) {
        String particleName = config.getString("type", "FLAME").toUpperCase();
        Particle particle;

        try {
            particle = Particle.valueOf(particleName);
        } catch (IllegalArgumentException e) {
            Zephaire.getPlugin(Zephaire.class).getLogger().warning("Invalid particle type: '" + particleName + "' in config section: " + config.getName());
            return null;
        }

        int count = config.getInt("count", 1);
        double speed = config.getDouble("speed", 0);
        Object data = null;

        ConfigurationSection options = config.getConfigurationSection("options");
        if (options != null) {
            if (particle.getDataType().equals(Particle.DustOptions.class)) {
                String colorStr = options.getString("color", "#FFFFFF");
                float size = (float) options.getDouble("size", 1.0);
                Color color = hexToColor(colorStr);
                data = new Particle.DustOptions(color, size);
            } else if (particle.getDataType().equals(Particle.DustTransition.class)) {
                String fromColorStr = options.getString("from-color", "#FFFFFF");
                String toColorStr = options.getString("to-color", "#000000");
                float size = (float) options.getDouble("size", 1.0);
                Color fromColor = hexToColor(fromColorStr);
                Color toColor = hexToColor(toColorStr);
                data = new Particle.DustTransition(fromColor, toColor, size);
            } else if (particle.getDataType().equals(ItemStack.class)) {
                String materialName = options.getString("material");
                if (materialName != null) {
                    try {
                        Material material = Material.valueOf(materialName.toUpperCase());
                        data = new ItemStack(material);
                    } catch (IllegalArgumentException e) {
                        Zephaire.getPlugin(Zephaire.class).getLogger().warning("Invalid material for ITEM particle: '" + materialName + "' in config section: " + config.getName());
                    }
                }
            } else if (particle.getDataType().equals(org.bukkit.block.data.BlockData.class)) {
                String materialName = options.getString("material");
                if (materialName != null) {
                    try {
                        Material material = Material.valueOf(materialName.toUpperCase());
                        if (material.isBlock()) {
                            data = material.createBlockData();
                        } else {
                            Zephaire.getPlugin(Zephaire.class).getLogger().warning("Material '" + materialName + "' is not a block for BLOCK particle in config section: " + config.getName());
                        }
                    } catch (IllegalArgumentException e) {
                        Zephaire.getPlugin(Zephaire.class).getLogger().warning("Invalid material for BLOCK particle: '" + materialName + "' in config section: " + config.getName());
                    }
                }
            }
        }

        return new ParticleSpawnData(particle, count, speed, data);
    }

    /**
     * Converts a hex color string (e.g., "#FF0000") to a Bukkit Color object.
     *
     * @param hex The hex color string.
     * @return The corresponding Bukkit Color object, or white if invalid.
     */
    private static Color hexToColor(String hex) {
        hex = hex.replace("#", "");
        try {
            int r = Integer.valueOf(hex.substring(0, 2), 16);
            int g = Integer.valueOf(hex.substring(2, 4), 16);
            int b = Integer.valueOf(hex.substring(4, 6), 16);
            return Color.fromRGB(r, g, b);
        } catch (Exception e) {
            return Color.WHITE;
        }
    }
}

