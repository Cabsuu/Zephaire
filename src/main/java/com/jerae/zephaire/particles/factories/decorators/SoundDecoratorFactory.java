package com.jerae.zephaire.particles.factories.decorators;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.animations.AnimatedParticle;
import com.jerae.zephaire.particles.animations.SoundDecorator;
import com.jerae.zephaire.particles.util.ConfigValidator;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class SoundDecoratorFactory implements DecoratorFactory {
    @Override
    public AnimatedParticle create(AnimatedParticle wrappedParticle, ConfigurationSection section) {
        try {
            Sound sound = ConfigValidator.getSound(section, "sound", "block.note_block.pling");

            if (sound == null) {
                JavaPlugin.getPlugin(Zephaire.class).getLogger().log(Level.WARNING, "Invalid sound name provided in '" + section.getCurrentPath() + "'. Skipping sound decorator.");
                return wrappedParticle;
            }

            float volume = (float) ConfigValidator.getPositiveDouble(section, "volume", 1.0);
            float pitch = (float) ConfigValidator.getPositiveDouble(section, "pitch", 1.0);
            int period = ConfigValidator.getPositiveInt(section, "period", 20);

            return new SoundDecorator(wrappedParticle, sound, volume, pitch, period);
        } catch (Exception e) {
            JavaPlugin.getPlugin(Zephaire.class).getLogger().log(Level.SEVERE, "An unexpected error occurred while parsing a sound decorator in '" + section.getCurrentPath() + "'.", e);
            return wrappedParticle;
        }
    }
}

