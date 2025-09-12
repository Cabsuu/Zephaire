package com.jerae.zephaire.particles.factories.decorators;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.animations.AnimatedParticle;
import com.jerae.zephaire.particles.animations.decorator.MultiColorTransitionDecorator;
import com.jerae.zephaire.particles.util.ConfigValidator;
import com.jerae.zephaire.particles.util.ParticleUtils;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class MultiColorTransitionDecoratorFactory implements DecoratorFactory {
    @Override
    public AnimatedParticle create(AnimatedParticle wrappedParticle, ConfigurationSection section) {
        try {
            float size = (float) ConfigValidator.getPositiveDouble(section, "size", 1.0);
            int transitionTime = ConfigValidator.getPositiveInt(section, "transition-time", 20);
            List<Color> colors = section.getStringList("colors").stream()
                    .map(ParticleUtils::hexToColor).collect(Collectors.toList());

            if (colors.size() < 2) {
                JavaPlugin.getPlugin(Zephaire.class).getLogger().log(Level.WARNING, "Multi-color-transition in '" + section.getCurrentPath() + "' requires at least 2 colors. Skipping decorator.");
                return wrappedParticle;
            }

            return new MultiColorTransitionDecorator(wrappedParticle, colors, size, transitionTime);
        } catch (Exception e) {
            JavaPlugin.getPlugin(Zephaire.class).getLogger().log(Level.SEVERE, "An unexpected error occurred while parsing a multi-color-transition decorator in '" + section.getCurrentPath() + "'.", e);
            return wrappedParticle;
        }
    }
}
