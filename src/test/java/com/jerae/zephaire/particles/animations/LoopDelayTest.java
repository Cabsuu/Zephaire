package com.jerae.zephaire.particles.animations;

import com.jerae.zephaire.particles.conditions.ConditionManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class LoopDelayTest {

    @Test
    public void testHelixParticleLoopDelay() {
        Location location = new Location(null, 0, 0, 0);
        ConditionManager conditionManager = Mockito.mock(ConditionManager.class);
        when(conditionManager.allConditionsMet(any())).thenReturn(true);

        HelixParticleTask task = new HelixParticleTask(
                location,
                Particle.FLAME,
                1.0,
                2.0,
                0.1,
                0.1,
                1,
                0,
                null,
                0,
                0,
                false,
                conditionManager,
                false,
                0,
                false,
                10
        );

        // Tick until the loop should complete
        for (int i = 0; i < 20; i++) {
            task.tick();
        }

        Location locationAfterLoop = task.getCurrentLocation().clone();

        // Now the loop has completed, and the delay should be active.
        // The particle should not move for the next 10 ticks.
        for (int i = 0; i < 10; i++) {
            task.tick();
        }

        assertEquals(locationAfterLoop.getX(), task.getCurrentLocation().getX(), 0.001);
        assertEquals(locationAfterLoop.getY(), task.getCurrentLocation().getY(), 0.001);
        assertEquals(locationAfterLoop.getZ(), task.getCurrentLocation().getZ(), 0.001);

        // After 10 ticks, the particle should move again
        task.tick();
        Location locationAfterDelay = task.getCurrentLocation().clone();

        org.junit.jupiter.api.Assertions.assertNotEquals(locationAfterLoop.getX(), locationAfterDelay.getX(), 0.001);
    }
}
