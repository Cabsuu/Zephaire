package com.jerae.zephaire.particles.factories.animated;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.CollisionManager;
import com.jerae.zephaire.particles.animations.AnimatedParticle;
import com.jerae.zephaire.particles.animations.CircleParticleTask;
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

public class CircleParticleFactory implements AnimatedParticleFactory {
    @Override
    public AnimatedParticle create(ConfigurationSection section, ConditionManager manager) {
        World world = Bukkit.getWorld(section.getString("world", "world"));
        if (world == null) return null;

        if (!section.isConfigurationSection("center")) {
            JavaPlugin.getPlugin(Zephaire.class).getLogger().log(Level.WARNING, "Particle '" + section.getName() + "' is missing required 'center' section. Skipping.");
            return null;
        }

        Location center = ParticleUtils.parseLocation(world, section.getConfigurationSection("center"));
        // --- VALIDATION: Use ConfigValidator for safe parsing ---
        Particle particle = ConfigValidator.getParticleType(section, "type", "FLAME");
        int particleCount = ConfigValidator.getPositiveInt(section, "particle-count", 20);
        double radius = ConfigValidator.getPositiveDouble(section, "radius", 1.0);

        double speed = section.getDouble("speed", 0.1);
        double pitch = section.getDouble("pitch", 0.0);
        double yaw = section.getDouble("yaw", 0.0);
        Object options = ParticleUtils.parseParticleOptions(particle, section.getConfigurationSection("options"));
        boolean collisionEnabled = CollisionManager.shouldCollide(section);

        return new CircleParticleTask(center, particle, radius, speed, particleCount, options, pitch, yaw, manager, collisionEnabled);
    }
}
