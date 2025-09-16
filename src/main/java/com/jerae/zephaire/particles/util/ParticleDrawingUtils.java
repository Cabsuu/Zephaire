package com.jerae.zephaire.particles.util;

import com.jerae.zephaire.particles.animations.AnimatedParticle;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * Utility class for drawing particle shapes.
 */
public class ParticleDrawingUtils {

    /**
     * Draws a circle of particles.
     *
     * @param animatedParticle The particle object to render.
     * @param center           The center of the circle.
     * @param radius           The radius of the circle.
     * @param particleCount    The number of particles to draw.
     * @param rotationAngle    The current rotation angle of the circle.
     * @param pitch            The pitch (x-axis rotation) in degrees.
     * @param yaw              The yaw (y-axis rotation) in degrees.
     */
    public static void drawCircle(AnimatedParticle animatedParticle, Location center, double radius, int particleCount, double rotationAngle, double pitch, double yaw) {
        if (center.getWorld() == null) return;

        for (int i = 0; i < particleCount; i++) {
            double angle = 2 * Math.PI * i / particleCount + rotationAngle;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;

            Vector particleVec = new Vector(x, 0, z);

            // Using Bukkit's built-in vector rotation methods.
            // These methods require the angle in radians, so we convert from degrees.
            particleVec.rotateAroundX(Math.toRadians(pitch));
            // We negate the yaw to match the behavior of your original VectorUtils class.
            particleVec.rotateAroundY(Math.toRadians(-yaw));

            Location particleLoc = center.clone().add(particleVec);
            animatedParticle.render(particleLoc);
        }
    }
}

