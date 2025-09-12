package com.jerae.zephaire.particles.factories.statics;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.managers.CollisionManager;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.factories.StaticParticleFactory;
import com.jerae.zephaire.particles.statics.StaticPointParticleTask;
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

public class StaticPointFactory implements StaticParticleFactory {
    @Override
    public BukkitRunnable create(ConfigurationSection section, ConditionManager manager) {
        World world = Bukkit.getWorld(section.getString("world", "world"));
        if (world == null) return null;

        if (!section.isConfigurationSection("location")) {
            JavaPlugin.getPlugin(Zephaire.class).getLogger().log(Level.WARNING, "Particle '" + section.getName() + "' is missing required 'location' section. Skipping.");
            return null;
        }

        Location loc = ParticleUtils.parseLocation(world, section.getConfigurationSection("location"));
        // --- VALIDATION: Use ConfigValidator for safe parsing ---
        Particle particle = ConfigValidator.getParticleType(section, "type", "FLAME");
        int count = ConfigValidator.getPositiveInt(section, "count", 1);

        double offsetX = section.getDouble("offset-x", 0.0);
        double offsetY = section.getDouble("offset-y", 0.0);
        double offsetZ = section.getDouble("offset-z", 0.0);
        double speed = section.getDouble("speed", 0.0);
        Object options = ParticleUtils.parseParticleOptions(particle, section.getConfigurationSection("options"));
        boolean collisionEnabled = CollisionManager.shouldCollide(section);

        return new StaticPointParticleTask(loc, particle, count, offsetX, offsetY, offsetZ, speed, options, manager, collisionEnabled);
    }
}
