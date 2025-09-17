package com.jerae.zephaire.particles.factories;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.util.ParticleUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public abstract class AbstractParticleFactory {

    protected World parseWorld(ConfigurationSection section) {
        String worldName = section.getString("world", "world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            JavaPlugin.getPlugin(Zephaire.class).getLogger().log(Level.WARNING, "Invalid world '" + worldName + "' for particle '" + section.getName() + "'. Skipping.");
        }
        return world;
    }

    protected Location parseLocation(World world, ConfigurationSection section, String key) {
        if (!section.isConfigurationSection(key)) {
            JavaPlugin.getPlugin(Zephaire.class).getLogger().log(Level.WARNING, "Particle '" + section.getName() + "' is missing required '" + key + "' section. Skipping.");
            return null;
        }
        return ParticleUtils.parseLocation(world, section.getConfigurationSection(key));
    }
}
