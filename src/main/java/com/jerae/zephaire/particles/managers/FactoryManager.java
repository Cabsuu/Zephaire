package com.jerae.zephaire.particles.managers;

import com.jerae.zephaire.particles.factories.AnimatedParticleFactory;
import com.jerae.zephaire.particles.factories.EntityParticleFactory;
import com.jerae.zephaire.particles.factories.StaticParticleFactory;
import com.jerae.zephaire.particles.factories.animated.AnimatedSpiralParticleFactory;
import com.jerae.zephaire.particles.factories.animated.CircleParticleFactory;
import com.jerae.zephaire.particles.factories.animated.CurveParticleFactory;
import com.jerae.zephaire.particles.factories.animated.HelixParticleFactory;
import com.jerae.zephaire.particles.factories.animated.LineParticleFactory;
import com.jerae.zephaire.particles.factories.animated.MovingStarParticleFactory;
import com.jerae.zephaire.particles.factories.animated.PulsingCircleParticleFactory;
import com.jerae.zephaire.particles.factories.animated.StarParticleFactory;
import com.jerae.zephaire.particles.factories.animated.VortexParticleFactory;
import com.jerae.zephaire.particles.factories.animated.WaveParticleFactory;
import com.jerae.zephaire.particles.factories.conditions.AnvilConditionFactory;
import com.jerae.zephaire.particles.factories.conditions.BlockInteractConditionFactory;
import com.jerae.zephaire.particles.factories.conditions.BlockMatchConditionFactory;
import com.jerae.zephaire.particles.factories.conditions.ConditionFactory;
import com.jerae.zephaire.particles.factories.conditions.DelayConditionFactory;
import com.jerae.zephaire.particles.factories.conditions.ElytraConditionFactory;
import com.jerae.zephaire.particles.factories.conditions.EnchantConditionFactory;
import com.jerae.zephaire.particles.factories.conditions.PlayerStatConditionFactory;
import com.jerae.zephaire.particles.factories.conditions.TimeConditionFactory;
import com.jerae.zephaire.particles.factories.conditions.TimerConditionFactory;
import com.jerae.zephaire.particles.factories.conditions.WeatherConditionFactory;
import com.jerae.zephaire.particles.factories.decorators.DecoratorFactory;
import com.jerae.zephaire.particles.factories.decorators.MultiColorTransitionDecoratorFactory;
import com.jerae.zephaire.particles.factories.decorators.SoundDecoratorFactory;
import com.jerae.zephaire.particles.factories.decorators.TrailDecoratorFactory;
import com.jerae.zephaire.particles.factories.decorators.VelocityDecoratorFactory;
import com.jerae.zephaire.particles.factories.entity.EntityCircleParticleFactory;
import com.jerae.zephaire.particles.factories.entity.EntityPointParticleFactory;
import com.jerae.zephaire.particles.factories.entity.EntitySpiralParticleFactory;
import com.jerae.zephaire.particles.factories.entity.EntityStarParticleFactory;
import com.jerae.zephaire.particles.factories.entity.EntityVortexParticleFactory;
import com.jerae.zephaire.particles.factories.statics.StaticCircleFactory;
import com.jerae.zephaire.particles.factories.statics.StaticConeFactory;
import com.jerae.zephaire.particles.factories.statics.StaticCuboidFactory;
import com.jerae.zephaire.particles.factories.statics.StaticCurveFactory;
import com.jerae.zephaire.particles.factories.statics.StaticLineFactory;
import com.jerae.zephaire.particles.factories.statics.StaticPointFactory;
import com.jerae.zephaire.particles.factories.statics.RandomBurstRegionFactory;
import com.jerae.zephaire.particles.factories.statics.StaticPyramidFactory;
import com.jerae.zephaire.particles.factories.statics.StaticRegionFactory;
import com.jerae.zephaire.particles.factories.statics.StaticStarFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Manages the registration and retrieval of all particle effect factories.
 */
public class FactoryManager {

    private final Map<String, StaticParticleFactory> staticFactories = new HashMap<>();
    private final Map<String, AnimatedParticleFactory> animatedFactories = new HashMap<>();
    private final Map<String, ConditionFactory> conditionFactories = new HashMap<>();
    private final Map<String, DecoratorFactory> decoratorFactories = new HashMap<>();
    private final Map<String, EntityParticleFactory> entityFactories = new HashMap<>();

    public FactoryManager() {
        registerStaticFactories();
        registerAnimatedFactories();
        registerConditionFactories();
        registerDecoratorFactories();
        registerEntityFactories();
    }

    private void registerStaticFactories() {
        staticFactories.put("POINT", new StaticPointFactory());
        staticFactories.put("CIRCLE", new StaticCircleFactory());
        staticFactories.put("CUBOID", new StaticCuboidFactory());
        staticFactories.put("LINE", new StaticLineFactory());
        staticFactories.put("REGION", new StaticRegionFactory());
        staticFactories.put("STAR", new StaticStarFactory());
        staticFactories.put("CURVE", new StaticCurveFactory());
        staticFactories.put("BURST_REGION", new RandomBurstRegionFactory());
        staticFactories.put("PYRAMID", new StaticPyramidFactory());
        staticFactories.put("CONE", new StaticConeFactory());
    }

    private void registerAnimatedFactories() {
        animatedFactories.put("CIRCLE", new CircleParticleFactory());
        animatedFactories.put("HELIX", new HelixParticleFactory());
        animatedFactories.put("LINE", new LineParticleFactory());
        animatedFactories.put("STAR", new StarParticleFactory());
        animatedFactories.put("CURVE", new CurveParticleFactory());
        animatedFactories.put("PULSING_CIRCLE", new PulsingCircleParticleFactory());
        animatedFactories.put("WAVE", new WaveParticleFactory());
        animatedFactories.put("MOVING_STAR", new MovingStarParticleFactory());
        animatedFactories.put("VORTEX", new VortexParticleFactory());
        animatedFactories.put("SPIRAL", new AnimatedSpiralParticleFactory());
    }

    private void registerConditionFactories() {
        conditionFactories.put("WEATHER", new WeatherConditionFactory());
        conditionFactories.put("TIME", new TimeConditionFactory());
        conditionFactories.put("BLOCK_MATCH", new BlockMatchConditionFactory());
        conditionFactories.put("TIMER", new TimerConditionFactory());
        conditionFactories.put("DELAY", new DelayConditionFactory());
        conditionFactories.put("BLOCK_INTERACT", new BlockInteractConditionFactory());
        conditionFactories.put("ANVIL", new AnvilConditionFactory());
        conditionFactories.put("ENCHANT", new EnchantConditionFactory());
        conditionFactories.put("ELYTRA", new ElytraConditionFactory());
        conditionFactories.put("PLAYER_STAT", new PlayerStatConditionFactory());
    }

    private void registerDecoratorFactories() {
        decoratorFactories.put("multi-color-transition", new MultiColorTransitionDecoratorFactory());
        decoratorFactories.put("sound", new SoundDecoratorFactory());
        decoratorFactories.put("trail", new TrailDecoratorFactory());
        decoratorFactories.put("velocity", new VelocityDecoratorFactory());
    }

    private void registerEntityFactories() {
        entityFactories.put("CIRCLE", new EntityCircleParticleFactory());
        entityFactories.put("STAR", new EntityStarParticleFactory());
        entityFactories.put("VORTEX", new EntityVortexParticleFactory());
        entityFactories.put("POINT", new EntityPointParticleFactory());
        entityFactories.put("SPIRAL", new EntitySpiralParticleFactory());
    }

    public Optional<StaticParticleFactory> getStaticFactory(String shape) {
        return Optional.ofNullable(staticFactories.get(shape));
    }

    public Optional<AnimatedParticleFactory> getAnimatedFactory(String shape) {
        return Optional.ofNullable(animatedFactories.get(shape));
    }

    public Optional<ConditionFactory> getConditionFactory(String type) {
        return Optional.ofNullable(conditionFactories.get(type));
    }

    public Optional<DecoratorFactory> getDecoratorFactory(String key) {
        return Optional.ofNullable(decoratorFactories.get(key));
    }

    public Optional<EntityParticleFactory> getEntityFactory(String shape) {
        return Optional.ofNullable(entityFactories.get(shape));
    }
}

