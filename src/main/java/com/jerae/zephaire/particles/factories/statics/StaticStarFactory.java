package com.jerae.zephaire.particles.factories.statics;

import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.statics.StaticStarParticleTask;
import com.jerae.zephaire.particles.util.ConfigValidator;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class StaticStarFactory extends AbstractStaticParticleFactory {
    @Override
    protected BukkitRunnable createParticleTask(ConfigurationSection section, ConditionManager manager, World world) {
        Location center = parseLocation(world, section, "center");
        if (center == null) {
            return null;
        }

        Particle particle = parseParticle(section);
        int points = ConfigValidator.getPositiveInt(section, "points", 5);
        double outerRadius = ConfigValidator.getPositiveDouble(section, "outer-radius", 3.0);
        double innerRadius = ConfigValidator.getPositiveDouble(section, "inner-radius", 1.5);
        double density = ConfigValidator.getPositiveDouble(section, "density", 10.0);
        double pitch = section.getDouble("pitch", 0.0);
        double yaw = section.getDouble("yaw", 0.0);
        Object options = parseOptions(particle, section);
        boolean collisionEnabled = parseCollision(section);
        int despawnTimer = section.getInt("despawn-timer", 100);
        boolean hasGravity = section.getBoolean("options.gravity", false);

        ConfigurationSection rotationSection = section.getConfigurationSection("rotation");
        Vector rotation = new Vector(0,0,0);
        if (rotationSection != null) {
            rotation.setX(rotationSection.getDouble("x", 0));
            rotation.setY(rotationSection.getDouble("y", 0));
            rotation.setZ(rotationSection.getDouble("z", 0));
        }

        return new StaticStarParticleTask(center, particle, points, outerRadius, innerRadius, density, options, pitch, yaw, manager, collisionEnabled, despawnTimer, hasGravity, rotation);
    }
}