package com.jerae.zephaire.particles.factories.conditions;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.managers.FactoryManager;
import com.jerae.zephaire.particles.conditions.ParticleCondition;
import com.jerae.zephaire.particles.conditions.WeatherCondition;
import com.jerae.zephaire.particles.util.ConfigValidator;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.logging.Level;

public class WeatherConditionFactory implements ConditionFactory {
    @Override
    public ParticleCondition create(Map<?, ?> configMap, World defaultWorld, String particlePath, FactoryManager factoryManager) {
        String weatherTypeStr = ConfigValidator.getString(configMap, "is", "CLEAR", particlePath);
        try {
            WeatherCondition.WeatherType type = WeatherCondition.WeatherType.valueOf(weatherTypeStr.toUpperCase());
            return new WeatherCondition(type);
        } catch (IllegalArgumentException e) {
            JavaPlugin.getPlugin(Zephaire.class).getLogger().log(Level.WARNING, "Invalid weather type '" + weatherTypeStr + "' in condition for '" + particlePath + "'. Using CLEAR as default.");
            return new WeatherCondition(WeatherCondition.WeatherType.CLEAR);
        }
    }
}
