package com.jerae.zephaire.particles.factories.statics;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.managers.CollisionManager;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.factories.StaticParticleFactory;
import com.jerae.zephaire.particles.statics.StaticStarParticleTask;
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

public class StaticStarFactory implements StaticParticleFactory {
    @Override
    public BukkitRunnable create(ConfigurationSection section, ConditionManager manager) {
        World world = Bukkit.getWorld(section.getString("world", "world"));
        if (world == null) return null;

        if (!section.isConfigurationSection("center")) {
            JavaPlugin.getPlugin(Zephaire.class).getLogger().log(Level.WARNING, "Particle '" + section.getName() + "' is missing required 'center' section. Skipping.");
            return null;
        }

        Location center = ParticleUtils.parseLocation(world, section.getConfigurationSection("center"));
        // --- VALIDATION: Use ConfigValidator for safe parsing ---
        Particle particle = ConfigValidator.getParticleType(section, "type", "FLAME");
        int points = ConfigValidator.getPositiveInt(section, "points", 5);
        double outerRadius = ConfigValidator.getPositiveDouble(section, "outer-radius", 3.0);
        double innerRadius = ConfigValidator.getPositiveDouble(section, "inner-radius", 1.5);
        double density = ConfigValidator.getPositiveDouble(section, "density", 10.0);

        double pitch = section.getDouble("pitch", 0.0);
        double yaw = section.getDouble("yaw", 0.0);
        Object options = ParticleUtils.parseParticleOptions(particle, section.getConfigurationSection("options"));
        boolean collisionEnabled = CollisionManager.shouldCollide(section);

        return new StaticStarParticleTask(center, particle, points, outerRadius, innerRadius, density, options, pitch, yaw, manager, collisionEnabled);
    }
}

