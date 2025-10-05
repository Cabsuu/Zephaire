package com.jerae.zephaire.particles.factories.statics;

import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.statics.StaticPointParticleTask;
import com.jerae.zephaire.particles.util.ConfigValidator;
import com.jerae.zephaire.nms.NMSManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

public class StaticPointFactory extends AbstractStaticParticleFactory {
    @Override
    protected BukkitRunnable createParticleTask(ConfigurationSection section, ConditionManager manager, World world) {
        Location loc = parseLocation(world, section, "location");
        if (loc == null) {
            return null;
        }

        Particle particle = parseParticle(section);
        int count = ConfigValidator.getPositiveInt(section, "count", 1);
        double offsetX = section.getDouble("offset-x", 0.0);
        double offsetY = section.getDouble("offset-y", 0.0);
        double offsetZ = section.getDouble("offset-z", 0.0);
        double speed = section.getDouble("speed", 0.0);

        if (particle != null && NMSManager.isVersionAtLeast("1.21.9")) {
            String particleName = particle.name();
            if (particleName.equals("DRAGON_BREATH") || particleName.equals("INSTANT_EFFECT") || particleName.equals("EFFECT")) {
                if (section.contains("power")) {
                    speed = section.getDouble("power");
                }
            }
        }

        Object options = parseOptions(particle, section);
        boolean collisionEnabled = parseCollision(section);
        int despawnTimer = section.getInt("despawn-timer", 100);
        boolean hasGravity = section.getBoolean("options.gravity", false);
        double spread = section.getDouble("spread", 0.0);

        return new StaticPointParticleTask(loc, particle, count, offsetX, offsetY, offsetZ, speed, options, manager, collisionEnabled, despawnTimer, hasGravity, spread);
    }
}
