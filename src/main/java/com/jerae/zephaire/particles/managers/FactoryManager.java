package com.jerae.zephaire.particles.managers;

import com.jerae.zephaire.particles.factories.AnimatedParticleFactory;
import com.jerae.zephaire.particles.factories.EntityParticleFactory;
import com.jerae.zephaire.particles.factories.StaticParticleFactory;
import com.jerae.zephaire.particles.factories.animated.*;
import com.jerae.zephaire.particles.factories.conditions.*;
import com.jerae.zephaire.particles.factories.conditions.AnvilConditionFactory;
import com.jerae.zephaire.particles.factories.conditions.EnchantConditionFactory;
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
import com.jerae.zephaire.particles.factories.statics.*;

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
        staticFactories.put("STATIC_CIRCLE", new StaticCircleFactory());
        staticFactories.put("STATIC_CUBOID", new StaticCuboidFactory());
        staticFactories.put("STATIC_LINE", new StaticLineFactory());
        staticFactories.put("STATIC_REGION", new StaticRegionFactory());
        staticFactories.put("STATIC_STAR", new StaticStarFactory());
        staticFactories.put("STATIC_CURVE", new StaticCurveFactory());
        staticFactories.put("RANDOM_BURST_REGION", new RandomBurstRegionFactory());
        staticFactories.put("STATIC_PYRAMID", new StaticPyramidFactory());
        staticFactories.put("STATIC_CONE", new StaticConeFactory());
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

