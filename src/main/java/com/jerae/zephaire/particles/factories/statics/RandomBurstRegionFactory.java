package com.jerae.zephaire.particles.factories.statics;

import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.statics.RandomBurstRegionParticleTask;
import com.jerae.zephaire.particles.util.ConfigValidator;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

public class RandomBurstRegionFactory extends AbstractStaticParticleFactory {
    @Override
    protected BukkitRunnable createParticleTask(ConfigurationSection section, ConditionManager manager, World world) {
        Location corner1 = parseLocation(world, section, "corner1");
        Location corner2 = parseLocation(world, section, "corner2");

        if (corner1 == null || corner2 == null) {
            return null;
        }

        Particle particle = parseParticle(section);
        long activeDuration = ConfigValidator.getPositiveInt(section, "active-duration", 100);
        long cooldownDuration = ConfigValidator.getPositiveInt(section, "cooldown-duration", 60);
        double burstRadius = ConfigValidator.getPositiveDouble(section, "burst-radius", 1.0);
        int spawnRate = ConfigValidator.getPositiveInt(section, "spawn-rate", 5);
        long spawnPeriod = ConfigValidator.getPositiveInt(section, "spawn-period", 1);
        Object options = parseOptions(particle, section);
        boolean collisionEnabled = parseCollision(section);
        int despawnTimer = section.getInt("despawn-timer", 100);

        return new RandomBurstRegionParticleTask(corner1, corner2, particle, options, manager, activeDuration, cooldownDuration, burstRadius, spawnRate, spawnPeriod, collisionEnabled, despawnTimer);
    }
}
