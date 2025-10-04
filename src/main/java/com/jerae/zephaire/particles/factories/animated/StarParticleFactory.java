package com.jerae.zephaire.particles.factories.animated;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.animations.AnimatedParticle;
import com.jerae.zephaire.particles.animations.StarParticleTask;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.util.ConfigValidator;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class StarParticleFactory extends AbstractAnimatedParticleFactory {
    @Override
    protected AnimatedParticle createParticleTask(Zephaire plugin, ConfigurationSection section, ConditionManager manager, World world, int loopDelay) {
        Location center = parseLocation(world, section, "center");
        if (center == null) {
            return null;
        }

        Particle particle = parseParticle(section);
        int points = ConfigValidator.getPositiveInt(section, "points", 5);
        double outerRadius = ConfigValidator.getPositiveDouble(section, "outer-radius", 3.0);
        double innerRadius = ConfigValidator.getPositiveDouble(section, "inner-radius", 1.5);
        double speed = section.getDouble("speed", 0.02);
        double density = ConfigValidator.getPositiveDouble(section, "density", 10.0);
        double pitch = section.getDouble("pitch", 0.0);
        double yaw = section.getDouble("yaw", 0.0);
        int period = section.getInt("period", 1);
        Object options = parseOptions(particle, section);
        boolean collisionEnabled = parseCollision(section);
        int despawnTimer = section.getInt("despawn-timer", 100);
        boolean hasGravity = section.getBoolean("options.gravity", false);

        return new StarParticleTask(center, particle, points, outerRadius, innerRadius, speed, density, options, pitch, yaw, manager, collisionEnabled, despawnTimer, hasGravity, loopDelay, period);
    }
}