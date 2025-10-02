package com.jerae.zephaire.particles.managers;

import com.jerae.zephaire.particles.factories.StaticParticleFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FactoryManagerTest {

    private FactoryManager factoryManager;

    @BeforeEach
    void setUp() {
        factoryManager = new FactoryManager();
    }

    @Test
    void testRegionFactoriesAreRegistered() {
        // Given
        String staticRegionFactoryName = "REGION";
        String randomBurstRegionFactoryName = "BURST_REGION";

        // When
        Optional<StaticParticleFactory> staticRegionFactory = factoryManager.getStaticFactory(staticRegionFactoryName);
        Optional<StaticParticleFactory> randomBurstRegionFactory = factoryManager.getStaticFactory(randomBurstRegionFactoryName);

        // Then
        assertTrue(staticRegionFactory.isPresent(), "REGION factory should be registered.");
        assertTrue(randomBurstRegionFactory.isPresent(), "BURST_REGION factory should be registered.");
    }
}