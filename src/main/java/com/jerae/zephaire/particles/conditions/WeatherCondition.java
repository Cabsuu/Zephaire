package com.jerae.zephaire.particles.conditions;

import org.bukkit.Location;
import org.bukkit.World;

public class WeatherCondition extends CachedParticleCondition {

    public enum WeatherType {
        RAINING,
        THUNDERING,
        CLEAR
    }

    private final WeatherType requiredWeather;

    public WeatherCondition(WeatherType requiredWeather) {
        super(20L); // Cache weather checks for 20 ticks (1 second)
        this.requiredWeather = requiredWeather;
    }

    @Override
    protected boolean queryCondition(Location location) {
        World world = location.getWorld();
        if (world == null) {
            return false;
        }

        switch (requiredWeather) {
            case RAINING:
                return world.hasStorm();
            case THUNDERING:
                return world.isThundering();
            case CLEAR:
                return !world.hasStorm() && !world.isThundering();
            default:
                return false;
        }
    }
}
