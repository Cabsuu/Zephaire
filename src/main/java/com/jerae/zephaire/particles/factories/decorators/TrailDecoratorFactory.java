package com.jerae.zephaire.particles.factories.decorators;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.animations.AnimatedParticle;
import com.jerae.zephaire.particles.animations.TrailDecorator;
import com.jerae.zephaire.particles.util.ConfigValidator;
import com.jerae.zephaire.particles.util.ParticleUtils;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class TrailDecoratorFactory implements DecoratorFactory {
    @Override
    public AnimatedParticle create(AnimatedParticle wrappedParticle, ConfigurationSection section) {
        try {
            Particle trailParticle = ConfigValidator.getParticleType(section, "trail-particle-type", "SMOKE");
            int trailLength = ConfigValidator.getPositiveInt(section, "trail-length", 10);
            int period = ConfigValidator.getPositiveInt(section, "period", 2);

            Object options = ParticleUtils.parseParticleOptions(trailParticle, section.getConfigurationSection("options"));

            return new TrailDecorator(wrappedParticle, trailParticle, options, trailLength, period);
        } catch (Exception e) {
            JavaPlugin.getPlugin(Zephaire.class).getLogger().log(Level.SEVERE, "An unexpected error occurred while parsing a trail decorator in '" + section.getCurrentPath() + "'.", e);
            return wrappedParticle;
        }
    }
}
