package com.jerae.zephaire.particles.animations.visual;

import org.bukkit.Location;

/**
 * Interface for rendering a particle effect at a specific location.
 * This allows for different rendering strategies, such as standard particles or cosmetic items.
 */
public interface IParticleRenderer {
    /**
     * Renders a particle or effect at the given location.
     *
     * @param location The location to render the effect at.
     */
    void render(Location location);
}
