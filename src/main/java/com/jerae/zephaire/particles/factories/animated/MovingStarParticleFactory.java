package com.jerae.zephaire.particles.factories.animated;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.animations.AnimatedParticle;
import com.jerae.zephaire.particles.animations.MovingStarParticleTask;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.util.ConfigValidator;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

public class MovingStarParticleFactory extends AbstractAnimatedParticleFactory {
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
        double density = ConfigValidator.getPositiveDouble(section, "density", 10.0);
        double speed = section.getDouble("speed", 0.02);
        double pitch = section.getDouble("pitch", 0.0);
        double yaw = section.getDouble("yaw", 0.0);
        Object options = parseOptions(particle, section);
        boolean collisionEnabled = parseCollision(section);
        int despawnTimer = section.getInt("despawn-timer", 100);
        boolean hasGravity = section.getBoolean("options.gravity", false);
        int period = section.getInt("period", 1);

        double height = section.getDouble("height", 0.0);
        double verticalSpeed = section.getDouble("vertical-speed", 0.1);
        boolean bounce = section.getBoolean("bounce", false);

        ConfigurationSection velocitySection = section.getConfigurationSection("velocity");
        Vector velocity = new Vector(0, 0, 0);
        if (velocitySection != null) {
            velocity.setX(velocitySection.getDouble("x", 0.0));
            velocity.setY(velocitySection.getDouble("y", 0.0));
            velocity.setZ(velocitySection.getDouble("z", 0.0));
        }

        return new MovingStarParticleTask(center, particle, points, outerRadius, innerRadius, speed, density, options, pitch, yaw, manager, velocity, collisionEnabled, height, verticalSpeed, bounce, despawnTimer, hasGravity, loopDelay, period);
    }
}