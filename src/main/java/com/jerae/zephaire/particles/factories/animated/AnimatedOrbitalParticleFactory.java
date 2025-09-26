package com.jerae.zephaire.particles.factories.animated;

import com.jerae.zephaire.particles.animations.AnimatedOrbitalParticleTask;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.util.ConfigValidator;
import com.jerae.zephaire.particles.util.ParticleUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.animations.AnimatedParticle;
import org.bukkit.configuration.ConfigurationSection;

public class AnimatedOrbitalParticleFactory extends AbstractAnimatedParticleFactory {
    @Override
    protected AnimatedParticle createParticleTask(Zephaire plugin, ConfigurationSection section, ConditionManager manager, World world, int loopDelay) {
        Location center = parseLocation(world, section, "center");
        if (center == null) {
            return null;
        }

        Particle particle = ConfigValidator.getParticleType(section, "particle", "REDSTONE");
        int orbitingParticles = section.getInt("orbiting-particles", 5);
        double radius = section.getDouble("radius", 2.0);
        double speed = section.getDouble("speed", 0.1);
        Object options = ParticleUtils.parseParticleOptions(particle, section.getConfigurationSection("options"));

        return new AnimatedOrbitalParticleTask(center, particle, orbitingParticles, radius, speed, options, manager);
    }
}
