package com.jerae.zephaire.particles.factories.statics;

import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.statics.StaticRegionParticleTask;
import com.jerae.zephaire.particles.util.ConfigValidator;
import com.jerae.zephaire.regions.Region;
import com.jerae.zephaire.regions.RegionManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class StaticRegionFactory extends AbstractStaticParticleFactory {
    private final RegionManager regionManager;

    public StaticRegionFactory(RegionManager regionManager) {
        this.regionManager = regionManager;
    }

    @Override
    protected BukkitRunnable createParticleTask(ConfigurationSection section, ConditionManager manager, World world) {
        Location corner1 = null;
        Location corner2 = null;
        World regionWorld = world;

        if (section.isString("region")) {
            String regionName = section.getString("region");
            Region region = regionManager.getRegionByName(regionName);
            if (region != null) {
                regionWorld = region.getWorld();
                Vector min = region.getMin();
                Vector max = region.getMax();
                corner1 = new Location(regionWorld, min.getX(), min.getY(), min.getZ());
                corner2 = new Location(regionWorld, max.getX(), max.getY(), max.getZ());
            }
        } else {
            corner1 = parseLocation(world, section, "corner1");
            corner2 = parseLocation(world, section, "corner2");
        }


        if (corner1 == null || corner2 == null) {
            return null;
        }

        Particle particle = parseParticle(section);
        int particleCount = ConfigValidator.getPositiveInt(section, "particle-count", 50);
        Object options = parseOptions(particle, section);
        boolean collisionEnabled = parseCollision(section);
        int despawnTimer = section.getInt("despawn-timer", 100);
        boolean hasGravity = section.getBoolean("options.gravity", false);

        return new StaticRegionParticleTask(corner1, corner2, particle, particleCount, options, manager, collisionEnabled, despawnTimer, hasGravity);
    }
}
