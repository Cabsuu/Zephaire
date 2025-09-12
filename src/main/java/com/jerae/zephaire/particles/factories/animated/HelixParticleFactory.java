package com.jerae.zephaire.particles.factories.animated;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.managers.CollisionManager;
import com.jerae.zephaire.particles.animations.AnimatedParticle;
import com.jerae.zephaire.particles.animations.HelixParticleTask;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.factories.AnimatedParticleFactory;
import com.jerae.zephaire.particles.util.ConfigValidator;
import com.jerae.zephaire.particles.util.ParticleUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class HelixParticleFactory implements AnimatedParticleFactory {
    @Override
    public AnimatedParticle create(ConfigurationSection section, ConditionManager manager) {
        World world = Bukkit.getWorld(section.getString("world", "world"));
        if (world == null) return null;

        if (!section.isConfigurationSection("base")) {
            JavaPlugin.getPlugin(Zephaire.class).getLogger().log(Level.WARNING, "Particle '" + section.getName() + "' is missing required 'base' location section. Skipping.");
            return null;
        }

        Location base = ParticleUtils.parseLocation(world, section.getConfigurationSection("base"));
        // --- VALIDATION: Use ConfigValidator for safe parsing ---
        Particle particle = ConfigValidator.getParticleType(section, "type", "FLAME");
        double radius = ConfigValidator.getPositiveDouble(section, "radius", 1.0);
        double height = ConfigValidator.getPositiveDouble(section, "height", 5.0);
        int period = ConfigValidator.getPositiveInt(section, "period", 1);

        double speed = section.getDouble("speed", 0.1);
        double verticalSpeed = section.getDouble("vertical-speed", 0.1);
        double startAngle = section.getDouble("start-angle", 0.0);
        double pitch = section.getDouble("pitch", 0.0);
        double yaw = section.getDouble("yaw", 0.0);
        boolean bounce = section.getBoolean("bounce", false);
        Object options = ParticleUtils.parseParticleOptions(particle, section.getConfigurationSection("options"));
        boolean collisionEnabled = CollisionManager.shouldCollide(section);

        return new HelixParticleTask(base, particle, radius, height, speed, verticalSpeed, period, startAngle, options, pitch, yaw, bounce, manager, collisionEnabled);
    }
}

