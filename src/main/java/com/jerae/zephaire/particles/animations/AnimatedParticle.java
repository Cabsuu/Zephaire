package com.jerae.zephaire.particles.animations;

import com.jerae.zephaire.particles.AbstractParticleTask;
import com.jerae.zephaire.particles.animations.visual.IParticleRenderer;
import org.bukkit.Location;

/**
 * Represents a single animated particle with its own task and rendering logic.
 * This class is now decoupled from the specific type of particle being rendered.
 */
public class AnimatedParticle {

    private AbstractParticleTask task;
    private final IParticleRenderer renderer;
    private boolean isCancelled = false;

    public AnimatedParticle(IParticleRenderer renderer) {
        this.renderer = renderer;
    }

    /**
     * Renders the particle effect at the given location using the assigned renderer.
     *
     * @param location The location to display the effect.
     */
    public void render(Location location) {
        if (isCancelled || location == null) return;
        renderer.render(location);
    }

    /**
     * Starts the particle's animation task.
     */
    public void start() {
        if (task != null) {
            task.start();
        }
    }

    /**
     * Stops the particle's animation task.
     */
    public void stop() {
        if (task != null) {
            task.stop();
        }
        isCancelled = true;
    }

    // --- GETTERS AND SETTERS ---

    public AbstractParticleTask getTask() {
        return task;
    }

    public void setTask(AbstractParticleTask task) {
        this.task = task;
    }

    public IParticleRenderer getRenderer() {
        return renderer;
    }
}

