package com.jerae.zephaire.particles.factories.decorators;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.animations.AnimatedParticle;
import com.jerae.zephaire.particles.animations.decorator.VelocityDecorator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.logging.Level;

public class VelocityDecoratorFactory implements DecoratorFactory {
    @Override
    public AnimatedParticle create(AnimatedParticle wrappedParticle, ConfigurationSection section) {
        try {
            String type = section.getString("type", "gravity");
            Vector velocity = section.getVector("velocity", new Vector(0, 0, 0));
            double bounceFactor = section.getDouble("bounce-factor", 0.5);
            double spread = section.getDouble("spread", 0.1);

            Location groundLocation = null;
            if (section.isConfigurationSection("ground-location")) {
                ConfigurationSection locSection = section.getConfigurationSection("ground-location");
                String worldName = locSection.getString("world", "world");
                World world = Bukkit.getWorld(worldName);
                if (world != null) {
                    double x = locSection.getDouble("x");
                    double y = locSection.getDouble("y");
                    double z = locSection.getDouble("z");
                    groundLocation = new Location(world, x, y, z);
                }
            }

            return new VelocityDecorator(wrappedParticle, velocity, type, bounceFactor, groundLocation, spread);
        } catch (Exception e) {
            JavaPlugin.getPlugin(Zephaire.class).getLogger().log(Level.SEVERE, "An unexpected error occurred while parsing a velocity decorator in '" + section.getCurrentPath() + "'.", e);
            return wrappedParticle;
        }
    }
}
