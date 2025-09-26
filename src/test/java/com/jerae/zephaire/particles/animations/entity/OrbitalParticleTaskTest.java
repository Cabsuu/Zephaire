package com.jerae.zephaire.particles.animations.entity;

import com.jerae.zephaire.particles.ParticleScheduler;
import com.jerae.zephaire.particles.ParticleSpawnData;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.data.EntityTarget;
import com.jerae.zephaire.particles.data.SpawnBehavior;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
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

public class OrbitalParticleTaskTest {

    @Mock
    private World world;
    @Mock
    private Player player;
    @Mock
    private ConditionManager conditionManager;
    @Mock
    private EntityTarget entityTarget;

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
    public void testOrbitalParticleSpawning() {
        Location center = new Location(world, 10, 10, 10);
        when(player.getLocation()).thenReturn(center);

        int orbitingParticles = 5;
        double radius = 2.0;
        OrbitalParticleTask task = new OrbitalParticleTask(
                "test_orbit", Particle.DUST, orbitingParticles, radius, 0.1,
                null, conditionManager, new Vector(0, 0, 0), entityTarget,
                1, SpawnBehavior.ALWAYS, -1
        );

        task.tick(player);

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
