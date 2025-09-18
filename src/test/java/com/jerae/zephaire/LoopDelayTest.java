package com.jerae.zephaire;

import com.jerae.zephaire.particles.animations.CircleParticleTask;
import com.jerae.zephaire.particles.animations.LoopDelay;
import com.jerae.zephaire.particles.animations.Ticker;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.managers.AnimationManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoopDelayTest {

    private static class MockTicker implements Ticker {
        private long time = 0;
        public void advance(long millis) {
            time += millis;
        }
        @Override
        public long getTime() {
            return time;
        }
    }

    @Test
    public void testLoopDelay() {
        // 1. Create a mock ticker and a CircleParticleTask with a loop-delay of 10 ticks (500ms).
        MockTicker ticker = new MockTicker();
        LoopDelay loopDelay = new LoopDelay(10, ticker);
        Location dummyLocation = new Location(null, 0, 0, 0);
        ConditionManager conditionManager = new ConditionManager(Collections.emptyList());
        CircleParticleTask task = new CircleParticleTask(dummyLocation, Particle.FLAME, 1, 0.5, 1, null, 0, 0, conditionManager, false, 0, false, loopDelay);

        System.out.println("Task hash code in test: " + task.hashCode());
        // 2. Create an AnimationManager.
        AnimationManager animationManager = new AnimationManager(ticker);
        animationManager.addAnimatedTask(task);

        // 3. Run the manager enough times to complete a loop.
        // A full circle is 2 * PI radians. With a speed of 0.5, it takes 13 ticks to complete.
        for (int i = 0; i < 13; i++) {
            animationManager.run();
        }

        // 4. Check that the isWaiting() method of the LoopDelay object returns true.
        assertTrue(task.getLoopDelay().isWaiting(), "Task should be waiting after loop completion");

        // 5. Advance the ticker's time by 600ms (more than the 500ms delay).
        ticker.advance(600);

        // 6. Check that isWaiting() now returns false.
        assertFalse(task.getLoopDelay().isWaiting(), "Task should not be waiting after delay has passed");
    }
}
