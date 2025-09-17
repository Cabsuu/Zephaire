package com.jerae.zephaire.particles.util;

import com.jerae.zephaire.Zephaire;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.logging.Level;

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
        if (particle == null) { // This indicates a VISUAL_ITEM
            String materialName = optionsSection.getString("material", "STONE").toUpperCase();
            try {
                Material material = Material.valueOf(materialName);
                return new ItemStack(material);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid material '" + materialName + "' in particle options for '" + optionsSection.getParent().getName() + "'. Using STONE instead.");
                return new ItemStack(Material.STONE);
            }
        }
        if (particle == Particle.DUST) {
            Color color = hexToColor(optionsSection.getString("color", "FFFFFF"));
            float size = (float) optionsSection.getDouble("size", 1.0);
            return new Particle.DustOptions(color, size);
        } else if (particle == Particle.DUST_COLOR_TRANSITION) {
            Color from = hexToColor(optionsSection.getString("from-color", "FFFFFF"));
            Color to = hexToColor(optionsSection.getString("to-color", "000000"));
            float size = (float) optionsSection.getDouble("size", 1.0);
            return new Particle.DustTransition(from, to, size);
        } else if (particle == Particle.ITEM) {
            String materialName = optionsSection.getString("material", "STONE").toUpperCase();
            try {
                Material material = Material.valueOf(materialName);
                return new ItemStack(material);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid material '" + materialName + "' in particle options for '" + optionsSection.getParent().getName() + "'. Using STONE instead.");
                return new ItemStack(Material.STONE);
            }
        } else if (particle == Particle.BLOCK ||
                particle == Particle.BLOCK_CRUMBLE ||
                particle == Particle.BLOCK_MARKER ||
                particle == Particle.DUST_PILLAR ||
                particle == Particle.FALLING_DUST) {
            String materialName = optionsSection.getString("material", "STONE").toUpperCase();
            try {
                Material material = Material.valueOf(materialName);
                if (!material.isBlock()) {
                    plugin.getLogger().warning("Invalid material '" + materialName + "' in particle options for '" + optionsSection.getParent().getName() + "'. Material must be a block. Using STONE instead.");
                    return Material.STONE.createBlockData();
                }
                return material.createBlockData();
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid material '" + materialName + "' in particle options for '" + optionsSection.getParent().getName() + "'. Using STONE instead.");
                return Material.STONE.createBlockData();
            }
        } else if (particle == Particle.ENTITY_EFFECT) {
            // ENTITY_EFFECT uses a Color object for its data
            return hexToColor(optionsSection.getString("color", "FFFFFF"));
        }
        return null;
    }

    /**
     * Formats a boolean value into a colored string for debug output.
     * @param value The boolean value.
     * @return A green "true" or red "false" string.
     */
    public static String formatBoolean(boolean value) {
        return value ? ChatColor.GREEN + "true" : ChatColor.RED + "false";
    }
}

