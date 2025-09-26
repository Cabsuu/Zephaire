package com.jerae.zephaire.particles.animations.decorator;

import com.jerae.zephaire.particles.animations.AnimatedParticle;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class VelocityDecoratorTest {

    @Mock
    private AnimatedParticle wrappedParticle;
    @Mock
    private World world;

    private Location particleLocation;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        particleLocation = new Location(world, 10, 10, 10);
        when(wrappedParticle.getCurrentLocation()).thenReturn(particleLocation);
    }

    @Test
    public void testGravity() {
        // Arrange
        Vector velocity = new Vector(0, 1, 0);
        VelocityDecorator decorator = new VelocityDecorator(wrappedParticle, velocity, "gravity", 0, null, 0);

        // Act
        decorator.tick();

        // Assert
        assertEquals(10, particleLocation.getX(), 0.001);
        assertEquals(11, particleLocation.getY(), 0.001);
        assertEquals(10, particleLocation.getZ(), 0.001);

        // Act again
        decorator.tick();

        // Assert
        assertEquals(10, particleLocation.getX(), 0.001);
        assertEquals(11.95, particleLocation.getY(), 0.001);
        assertEquals(10, particleLocation.getZ(), 0.001);
    }

    @Test
    public void testBounce() {
        // Arrange
        Vector velocity = new Vector(0, -1, 0);
        Location ground = new Location(world, 10, 9, 10);
        VelocityDecorator decorator = new VelocityDecorator(wrappedParticle, velocity, "bounce", 0.5, ground, 0);

        // Act
        decorator.tick();

        // Assert
        assertEquals(10, particleLocation.getX(), 0.001);
        assertEquals(9, particleLocation.getY(), 0.001);
        assertEquals(10, particleLocation.getZ(), 0.001);
        assertEquals(0.45, decorator.getVelocity().getY(), 0.001);
    }

    @Test
    public void testSpray() {
        // Arrange
        Vector velocity = new Vector(1, 1, 1);
        VelocityDecorator decorator = new VelocityDecorator(wrappedParticle, velocity, "spray", 0, null, 0.5);
        Location initialLocation = particleLocation.clone();

        // Act
        decorator.tick();

        // Assert
        double distance = particleLocation.distance(initialLocation);
        assertTrue(distance > 0.866, "Distance should be greater than the minimum possible distance.");
        assertTrue(distance < 2.598, "Distance should be less than the maximum possible distance.");
    }
}
