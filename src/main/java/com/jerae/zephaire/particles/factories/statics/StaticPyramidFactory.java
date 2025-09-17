package com.jerae.zephaire.particles.factories.statics;

import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.statics.StaticPyramidParticleTask;
import com.jerae.zephaire.particles.util.ConfigValidator;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

public class StaticPyramidFactory extends AbstractStaticParticleFactory {
    @Override
    protected BukkitRunnable createParticleTask(ConfigurationSection section, ConditionManager manager, World world) {
        Location center = parseLocation(world, section, "center");
        if (center == null) {
            return null;
        }

        Particle particle = parseParticle(section);
        double baseSize = ConfigValidator.getPositiveDouble(section, "base-size", 2.0);
        double height = ConfigValidator.getPositiveDouble(section, "height", 3.0);
        int sides = ConfigValidator.getPositiveInt(section, "sides", 4);
        double density = ConfigValidator.getPositiveDouble(section, "density", 5.0);
        double pitch = section.getDouble("pitch", 0.0);
        double yaw = section.getDouble("yaw", 0.0);
        Object options = parseOptions(particle, section);
        boolean collisionEnabled = parseCollision(section);
        int despawnTimer = section.getInt("despawn-timer", 100);
        boolean hasGravity = section.getBoolean("options.gravity", false);

        return new StaticPyramidParticleTask(center, particle, baseSize, height, sides, density, options, pitch, yaw, manager, collisionEnabled, despawnTimer, hasGravity);
    }
}
