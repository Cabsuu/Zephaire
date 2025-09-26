package com.jerae.zephaire.particles.animations;

import com.jerae.zephaire.particles.ParticleScheduler;
import com.jerae.zephaire.particles.ParticleSpawnData;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class AnimatedOrbitalParticleTaskTest {

    @Mock
    private World world;
    @Mock
    private ConditionManager conditionManager;

    private MockedStatic<ParticleScheduler> particleScheduler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        particleScheduler = Mockito.mockStatic(ParticleScheduler.class);
        when(conditionManager.allConditionsMet(any())).thenReturn(true);
    }

    @AfterEach
    public void tearDown() {
        particleScheduler.close();
    }

    @Test
    public void testStaticOrbitalParticleSpawning() {
        Location center = new Location(world, 10, 10, 10);

        int orbitingParticles = 5;
        double radius = 2.0;
        AnimatedOrbitalParticleTask task = new AnimatedOrbitalParticleTask(
                center, Particle.DUST, orbitingParticles, radius, 0.1,
                null, conditionManager
        );

        task.tick();

        ArgumentCaptor<ParticleSpawnData> captor = ArgumentCaptor.forClass(ParticleSpawnData.class);
        particleScheduler.verify(() -> ParticleScheduler.queueParticle(captor.capture()), times(orbitingParticles));

        for (int i = 0; i < orbitingParticles; i++) {
            Location spawnLoc = captor.getAllValues().get(i).location;
            assertEquals(world, spawnLoc.getWorld());
            assertEquals(10.0, spawnLoc.getY(), 0.001);
            double distanceFromCenter = spawnLoc.distance(center);
            assertEquals(radius, distanceFromCenter, 0.001);
        }
    }
}
