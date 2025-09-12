package com.jerae.zephaire.particles.factories.statics;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.managers.CollisionManager;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.factories.StaticParticleFactory;
import com.jerae.zephaire.particles.statics.StaticPyramidParticleTask;
import com.jerae.zephaire.particles.util.ConfigValidator;
import com.jerae.zephaire.particles.util.ParticleUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

public class StaticPyramidFactory implements StaticParticleFactory {
    @Override
    public BukkitRunnable create(ConfigurationSection section, ConditionManager manager) {
        World world = Bukkit.getWorld(section.getString("world", "world"));
        if (world == null) return null;

        if (!section.isConfigurationSection("center")) {
            JavaPlugin.getPlugin(Zephaire.class).getLogger().log(Level.WARNING, "Particle '" + section.getName() + "' is missing required 'center' section. Skipping.");
            return null;
        }

        Location center = ParticleUtils.parseLocation(world, section.getConfigurationSection("center"));
        Particle particle = ConfigValidator.getParticleType(section, "type", "FLAME");
        double baseSize = ConfigValidator.getPositiveDouble(section, "base-size", 2.0);
        double height = ConfigValidator.getPositiveDouble(section, "height", 3.0);
        int sides = ConfigValidator.getPositiveInt(section, "sides", 4);
        double density = ConfigValidator.getPositiveDouble(section, "density", 5.0);
        double pitch = section.getDouble("pitch", 0.0);
        double yaw = section.getDouble("yaw", 0.0);
        Object options = ParticleUtils.parseParticleOptions(particle, section.getConfigurationSection("options"));
        boolean collisionEnabled = CollisionManager.shouldCollide(section);

        return new StaticPyramidParticleTask(center, particle, baseSize, height, sides, density, options, pitch, yaw, manager, collisionEnabled);
    }
}
