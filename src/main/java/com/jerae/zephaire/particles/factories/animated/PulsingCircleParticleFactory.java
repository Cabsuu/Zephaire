package com.jerae.zephaire.particles.factories.animated;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.CollisionManager;
import com.jerae.zephaire.particles.animations.AnimatedParticle;
import com.jerae.zephaire.particles.animations.PulsingCircleParticleTask;
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

public class PulsingCircleParticleFactory implements AnimatedParticleFactory {
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
        double maxRadius = ConfigValidator.getPositiveDouble(section, "max-radius", 3.0);
        int particleCount = ConfigValidator.getPositiveInt(section, "particle-count", 50);
        int period = ConfigValidator.getPositiveInt(section, "period", 1);

        double pulseSpeed = section.getDouble("pulse-speed", 0.1);
        double pitch = section.getDouble("pitch", 0.0);
        double yaw = section.getDouble("yaw", 0.0);
        boolean expand = section.getBoolean("expand", false);
        Object options = ParticleUtils.parseParticleOptions(particle, section.getConfigurationSection("options"));
        boolean collisionEnabled = CollisionManager.shouldCollide(section);

        return new PulsingCircleParticleTask(center, particle, maxRadius, pulseSpeed, particleCount, pitch, yaw, expand, options, manager, period, collisionEnabled);
    }
}

