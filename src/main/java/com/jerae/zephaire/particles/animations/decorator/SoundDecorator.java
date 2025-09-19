package com.jerae.zephaire.particles.animations.decorator;

import com.jerae.zephaire.particles.Debuggable;
import com.jerae.zephaire.particles.SoundPlayData;
import com.jerae.zephaire.particles.ParticleScheduler;
import com.jerae.zephaire.particles.animations.AnimatedParticle;
import org.bukkit.Location;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.World;

public class SoundDecorator implements AnimatedParticle {

    private final AnimatedParticle wrappedParticle;
    private final Sound sound;
    private final float volume;
    private final float pitch;
    private final int period;
    private int tickCounter = 0;

    public SoundDecorator(AnimatedParticle wrappedParticle, Sound sound, float volume, float pitch, int period) {
        this.wrappedParticle = wrappedParticle;
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.period = Math.max(1, period);
    }

    @Override
    public void tick() {
        wrappedParticle.tick();

        tickCounter++;
        if (tickCounter >= period) {
            tickCounter = 0;
            Location loc = getCurrentLocation();
            if (loc != null) {
                ParticleScheduler.queueSound(new SoundPlayData(sound, loc, volume, pitch));
            }
        }
    }

    @Override
    public Location getCurrentLocation() {
        return wrappedParticle.getCurrentLocation();
    }

    @Override
    public boolean shouldCollide() {
        return wrappedParticle.shouldCollide();
    }

    @Override
    public int getLoopDelayCounter() {
        return wrappedParticle.getLoopDelayCounter();
    }

    @Override
    public String getDebugInfo() {
        // A decorator passes the debug call to the particle it's wrapping,
        // but adds its own information to the output.
        if (wrappedParticle instanceof Debuggable) {
            // --- UPDATED: Use the modern registry to get the sound key ---
            return ((Debuggable) wrappedParticle).getDebugInfo() + "\n"
                    + "--- Decorator: SOUND ---" + "\n"
                    + "Sound: " + Registry.SOUNDS.getKey(sound) + "\n"
                    + "Period: " + period;
        }
        return "--- Decorator: SOUND (Wrapped particle is not debuggable) ---";
    }
}
