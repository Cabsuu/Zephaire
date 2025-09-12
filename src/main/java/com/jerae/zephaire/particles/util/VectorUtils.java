package com.jerae.zephaire.particles.util;

import org.bukkit.util.Vector;

public final class VectorUtils {

    /**
     * Rotates a vector around the X (pitch) and Y (yaw) axes.
     * <p>
     * Note: This method creates a new Vector object. For performance-critical code,
     * use the overloaded version that accepts a result Vector.
     *
     * @param v The vector to rotate.
     * @param pitch The rotation in degrees around the X-axis.
     * @param yaw The rotation in degrees around the Y-axis.
     * @return A new, rotated vector.
     */
    public static Vector rotateVector(Vector v, double pitch, double yaw) {
        // Maintain original behavior by creating a new Vector for the result.
        return rotateVector(v, pitch, yaw, new Vector());
    }

    /**
     * Rotates a vector around the X (pitch) and Y (yaw) axes, storing the result in a provided Vector object.
     * This is more memory-efficient as it avoids creating a new Vector object.
     *
     * @param v The vector to rotate.
     * @param pitch The rotation in degrees around the X-axis.
     * @param yaw The rotation in degrees around the Y-axis.
     * @param result The Vector object to store the result in.
     * @return The same result Vector object with the rotated values.
     */
    public static Vector rotateVector(Vector v, double pitch, double yaw, Vector result) {
        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);

        double cosYaw = Math.cos(yawRad);
        double sinYaw = Math.sin(yawRad);
        double cosPitch = Math.cos(pitchRad);
        double sinPitch = Math.sin(pitchRad);

        // Store original vector components
        double x = v.getX();
        double y = v.getY();
        double z = v.getZ();

        // Apply Yaw rotation (around Y-axis)
        double tempX = x * cosYaw - z * sinYaw;
        double tempZ = x * sinYaw + z * cosYaw;

        // Apply Pitch rotation (around X-axis) on the yaw-rotated vector
        double finalY = y * cosPitch - tempZ * sinPitch;
        double finalZ = y * sinPitch + tempZ * cosPitch;

        // Set the result vector's components
        result.setX(tempX);
        result.setY(finalY);
        result.setZ(finalZ);

        return result;
    }
}
