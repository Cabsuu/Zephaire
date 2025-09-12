package com.jerae.zephaire.particles;

/**
 * An interface for any particle effect that can provide debug information.
 */
public interface Debuggable {
    /**
     * Gathers and formats the current state of the particle effect for debugging.
     * @return A string containing detailed debug information.
     */
    String getDebugInfo();
}
