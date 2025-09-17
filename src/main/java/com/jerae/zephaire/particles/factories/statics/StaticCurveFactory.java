package com.jerae.zephaire.particles.factories.statics;

import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.statics.StaticCurveParticleTask;
import com.jerae.zephaire.particles.util.ConfigValidator;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

public class StaticCurveFactory extends AbstractStaticParticleFactory {
    @Override
    protected BukkitRunnable createParticleTask(ConfigurationSection section, ConditionManager manager, World world) {
        Location start = parseLocation(world, section, "start");
        Location control = parseLocation(world, section, "control");
        Location end = parseLocation(world, section, "end");

        if (start == null || control == null || end == null) {
            return null;
        }

        Particle particle = parseParticle(section);
        double density = ConfigValidator.getPositiveDouble(section, "density", 5.0);
        Object options = parseOptions(particle, section);
        boolean collisionEnabled = parseCollision(section);
        int despawnTimer = section.getInt("despawn-timer", 100);
        boolean hasGravity = section.getBoolean("options.gravity", false);

        return new StaticCurveParticleTask(start, control, end, particle, density, options, manager, collisionEnabled, despawnTimer, hasGravity);
    }
}
