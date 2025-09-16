package com.jerae.zephaire.particles.animations;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.AbstractParticleTask;
import com.jerae.zephaire.particles.util.ParticleDrawingUtils;
import org.bukkit.Location;

/**
 * An animated particle task that draws a circle.
 */
public class CircleParticleTask extends AbstractParticleTask {

    private final AnimatedParticle animatedParticle;
    private final Location center;
    private final double radius;
    private final int particleCount;
    private final double speed;
    private double angle = 0;

    public CircleParticleTask(Zephaire plugin, AnimatedParticle animatedParticle, Location center, double radius, int particleCount, double speed, long period) {
        super(plugin, period);
        this.animatedParticle = animatedParticle;
        this.center = center;
        this.radius = radius;
        this.particleCount = particleCount;
        this.speed = speed;
    }

    @Override
    public void run() {
        ParticleDrawingUtils.drawCircle(
                animatedParticle,
                center,
                radius,
                particleCount,
                angle,
                0, // Pitch
                0  // Yaw
        );
        angle += speed;
    }
}

