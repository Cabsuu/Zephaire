package com.jerae.zephaire.particles.conditions;

import org.bukkit.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class DelayConditionDecoratorTest {

    @Mock
    private ParticleCondition wrappedCondition;

    @Mock
    private Location mockLocation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDelayAndContinuousActivation() {
        long delayTicks = 5;
        DelayConditionDecorator delayCondition = new DelayConditionDecorator(wrappedCondition, delayTicks);

        // Condition initially not met
        when(wrappedCondition.check(mockLocation)).thenReturn(false);
        assertFalse(delayCondition.check(mockLocation), "Should be false when wrapped is false");

        // Condition becomes met, delay starts
        when(wrappedCondition.check(mockLocation)).thenReturn(true);
        assertFalse(delayCondition.check(mockLocation), "Should be false on the first tick condition is met");

        // Simulate ticks during the delay period
        for (int i = 0; i < delayTicks - 1; i++) {
            assertFalse(delayCondition.check(mockLocation), "Should be false during delay period, tick " + i);
        }

        // Delay is over, should be true
        assertTrue(delayCondition.check(mockLocation), "Should be true immediately after delay");

        // Should remain true as long as the condition is met
        assertTrue(delayCondition.check(mockLocation), "Should remain true after the delay has passed");
        assertTrue(delayCondition.check(mockLocation), "Should still be true on subsequent ticks");

        // Condition becomes false, should reset
        when(wrappedCondition.check(mockLocation)).thenReturn(false);
        assertFalse(delayCondition.check(mockLocation), "Should be false when wrapped becomes false");

        // Condition becomes true again, should restart delay
        when(wrappedCondition.check(mockLocation)).thenReturn(true);
        assertFalse(delayCondition.check(mockLocation), "Should be false again, restarting the delay");
    }
}