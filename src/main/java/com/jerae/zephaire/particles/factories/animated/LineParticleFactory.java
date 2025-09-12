package com.jerae.zephaire.particles.factories.animated;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.managers.CollisionManager;
import com.jerae.zephaire.particles.animations.AnimatedParticle;
import com.jerae.zephaire.particles.animations.LineParticleTask;
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

public class LineParticleFactory implements AnimatedParticleFactory {
    @Override
    public AnimatedParticle create(ConfigurationSection section, ConditionManager manager) {
        World world = Bukkit.getWorld(section.getString("world", "world"));
        if (world == null) return null;

        if (!section.isConfigurationSection("start") || !section.isConfigurationSection("end")) {
            JavaPlugin.getPlugin(Zephaire.class).getLogger().log(Level.WARNING, "Particle '" + section.getName() + "' is missing required 'start' or 'end' section. Skipping.");
            return null;
        }

        Location start = ParticleUtils.parseLocation(world, section.getConfigurationSection("start"));
        Location end = ParticleUtils.parseLocation(world, section.getConfigurationSection("end"));
        // --- VALIDATION: Use ConfigValidator for safe parsing ---
        Particle particle = ConfigValidator.getParticleType(section, "type", "FLAME");
        int period = ConfigValidator.getPositiveInt(section, "period", 1);

        double speed = section.getDouble("speed", 0.05);
        boolean resetOnEnd = section.getBoolean("reset-on-end", false);
        Object options = ParticleUtils.parseParticleOptions(particle, section.getConfigurationSection("options"));
        boolean collisionEnabled = CollisionManager.shouldCollide(section);

        return new LineParticleTask(start, end, particle, speed, period, options, resetOnEnd, manager, collisionEnabled);
    }
}

