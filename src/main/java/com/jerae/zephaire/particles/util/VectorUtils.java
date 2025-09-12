package com.jerae.zephaire.particles.util;

import org.bukkit.util.Vector;

public final class VectorUtils {

    /**
     * Rotates a vector around the X (pitch) and Y (yaw) axes.
     *
     * @param v The vector to rotate.
     * @param pitch The rotation in degrees around the X-axis.
     * @param yaw The rotation in degrees around the Y-axis.
     * @return A new, rotated vector.
     */
    public static Vector rotateVector(Vector v, double pitch, double yaw) {
        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);

        // Apply Yaw rotation (around Y-axis)
        double cosYaw = Math.cos(yawRad);
        double sinYaw = Math.sin(yawRad);
        double tempX = v.getX() * cosYaw - v.getZ() * sinYaw;
        double tempZ = v.getX() * sinYaw + v.getZ() * cosYaw;

        // Apply Pitch rotation (around X-axis) on the yaw-rotated vector
        double cosPitch = Math.cos(pitchRad);
        double sinPitch = Math.sin(pitchRad);
        double finalY = v.getY() * cosPitch - tempZ * sinPitch;
        double finalZ = v.getY() * sinPitch + tempZ * cosPitch;

        return new Vector(tempX, finalY, finalZ);
    }
}

