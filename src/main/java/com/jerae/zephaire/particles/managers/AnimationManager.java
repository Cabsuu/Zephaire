package com.jerae.zephaire.particles.managers;

import com.jerae.zephaire.particles.animations.AnimatedParticle;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * A single, efficient scheduler that runs and updates all animated particle tasks.
 * It is generalized to work with any class that implements the AnimatedParticle interface.
 */
public class AnimationManager extends BukkitRunnable {

    private final List<AnimatedParticle> animatedParticles = new ArrayList<>();

    public void addAnimatedTask(AnimatedParticle task) {
        animatedParticles.add(task);
    }

    /**
     * Returns the list of animated particle tasks.
     * @return The list of animated particles.
     */
    public List<AnimatedParticle> getAnimatedParticles() {
        return animatedParticles;
    }

    @Override
    public void run() {
        // Loop through all registered tasks and call their tick() method.
        // The manager doesn't need to know if it's a circle, line, or any other shape.
        for (AnimatedParticle task : animatedParticles) {
            task.tick();
        }
    }
}