package com.jerae.zephaire.particles.animations.visual;

import com.jerae.zephaire.particles.ParticleSpawnData;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Renders standard Bukkit particles using world.spawnParticle.
 */
public class StandardParticleRenderer implements IParticleRenderer {

    private final ParticleSpawnData spawnData;

    public StandardParticleRenderer(ParticleSpawnData spawnData) {
        this.spawnData = spawnData;
    }

    @Override
    public void render(Location location) {
        World world = location.getWorld();
        if (world == null) return;

        world.spawnParticle(
                spawnData.getParticle(),
                location,
                spawnData.getCount(),
                0, 0, 0, // Offsets are handled by the shape logic for animated particles
                spawnData.getSpeed(),
                spawnData.getData()
        );
    }
}
