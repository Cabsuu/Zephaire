package com.jerae.zephaire.particles;

import org.bukkit.Location;
import org.bukkit.Sound;

public class SoundPlayData {
    public final Sound sound;
    public final Location location;
    public final float volume;
    public final float pitch;

    public SoundPlayData(Sound sound, Location location, float volume, float pitch) {
        this.sound = sound;
        // Clone location to ensure it's safe to use on the main thread later
        this.location = location.clone();
        this.volume = volume;
        this.pitch = pitch;
    }
}
