package com.jerae.zephaire.particles.factories.animated;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.animations.AnimatedParticle;
import com.jerae.zephaire.particles.animations.CurveParticleTask;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class CurveParticleFactory extends AbstractAnimatedParticleFactory {
    @Override
    protected AnimatedParticle createParticleTask(Zephaire plugin, ConfigurationSection section, ConditionManager manager, World world, int loopDelay) {
        Location start = parseLocation(world, section, "start");
        Location control = parseLocation(world, section, "control");
        Location end = parseLocation(world, section, "end");

        if (start == null || control == null || end == null) {
            return null;
        }

        Particle particle = parseParticle(section);
        double speed = section.getDouble("speed", 0.02);
        boolean bounce = section.getBoolean("bounce", false);
        Object options = parseOptions(particle, section);
        boolean collisionEnabled = parseCollision(section);
        int despawnTimer = section.getInt("despawn-timer", 100);
        boolean hasGravity = section.getBoolean("options.gravity", false);

        return new CurveParticleTask(start, control, end, particle, speed, bounce, options, manager, collisionEnabled, despawnTimer, hasGravity, loopDelay);
    }
}
