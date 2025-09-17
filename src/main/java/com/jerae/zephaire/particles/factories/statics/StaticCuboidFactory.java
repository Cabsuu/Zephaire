package com.jerae.zephaire.particles.factories.statics;

import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.statics.StaticCuboidParticleTask;
import com.jerae.zephaire.particles.util.ConfigValidator;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

public class StaticCuboidFactory extends AbstractStaticParticleFactory {
    @Override
    protected BukkitRunnable createParticleTask(ConfigurationSection section, ConditionManager manager, World world) {
        Location center = parseLocation(world, section, "center");
        if (center == null) {
            return null;
        }

        Particle particle = parseParticle(section);
        double size = section.getDouble("size", -1.0);
        double width, height, depth;

        if (size > 0) {
            width = height = depth = size;
        } else {
            width = ConfigValidator.getPositiveDouble(section, "width", 1.0);
            height = ConfigValidator.getPositiveDouble(section, "height", 1.0);
            depth = ConfigValidator.getPositiveDouble(section, "depth", 1.0);
        }

        double density = ConfigValidator.getPositiveDouble(section, "density", 5.0);
        double pitch = section.getDouble("pitch", 0.0);
        double yaw = section.getDouble("yaw", 0.0);
        Object options = parseOptions(particle, section);
        boolean collisionEnabled = parseCollision(section);
        int despawnTimer = section.getInt("despawn-timer", 100);
        boolean hasGravity = section.getBoolean("options.gravity", false);

        return new StaticCuboidParticleTask(center, particle, width, height, depth, density, options, pitch, yaw, manager, collisionEnabled, despawnTimer, hasGravity);
    }
}
