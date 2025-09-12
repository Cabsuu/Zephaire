package com.jerae.zephaire.particles.animations;

import com.jerae.zephaire.particles.Debuggable;
import org.bukkit.Location;

public interface AnimatedParticle extends Debuggable {
    void tick();
    Location getCurrentLocation();
    boolean shouldCollide();
}
