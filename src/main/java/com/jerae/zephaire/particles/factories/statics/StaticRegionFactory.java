package com.jerae.zephaire.particles.factories.statics;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.CollisionManager;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.factories.StaticParticleFactory;
import com.jerae.zephaire.particles.statics.StaticRegionParticleTask;
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

public class StaticRegionFactory implements StaticParticleFactory {
    @Override
    public BukkitRunnable create(ConfigurationSection section, ConditionManager manager) {
        World world = Bukkit.getWorld(section.getString("world", "world"));
        if (world == null) return null;

        if (!section.isConfigurationSection("corner1") || !section.isConfigurationSection("corner2")) {
            JavaPlugin.getPlugin(Zephaire.class).getLogger().log(Level.WARNING, "Particle '" + section.getName() + "' is missing required 'corner1' or 'corner2' section. Skipping.");
            return null;
        }

        Location corner1 = ParticleUtils.parseLocation(world, section.getConfigurationSection("corner1"));
        Location corner2 = ParticleUtils.parseLocation(world, section.getConfigurationSection("corner2"));
        // --- VALIDATION: Use ConfigValidator for safe parsing ---
        Particle particle = ConfigValidator.getParticleType(section, "type", "FLAME");
        int particleCount = ConfigValidator.getPositiveInt(section, "particle-count", 50);

        Object options = ParticleUtils.parseParticleOptions(particle, section.getConfigurationSection("options"));
        boolean collisionEnabled = CollisionManager.shouldCollide(section);

        return new StaticRegionParticleTask(corner1, corner2, particle, particleCount, options, manager, collisionEnabled);
    }
}

