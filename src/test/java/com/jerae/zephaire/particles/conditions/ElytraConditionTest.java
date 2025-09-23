package com.jerae.zephaire.particles.conditions;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class ElytraConditionTest {

    @Mock
    private World world;
    @Mock
    private Player player;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testElytraCondition_PlayerGliding() {
        // Arrange
        Location loc = new Location(world, 0, 0, 0);
        when(world.getPlayers()).thenReturn(Collections.singletonList(player));
        when(player.getLocation()).thenReturn(loc);
        when(player.isGliding()).thenReturn(true);

        ElytraCondition condition = new ElytraCondition();

        // Act
        boolean result = condition.check(loc);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testElytraCondition_PlayerNotGliding() {
        // Arrange
        Location loc = new Location(world, 0, 0, 0);
        when(world.getPlayers()).thenReturn(Collections.singletonList(player));
        when(player.getLocation()).thenReturn(loc);
        when(player.isGliding()).thenReturn(false);

        ElytraCondition condition = new ElytraCondition();

        // Act
        boolean result = condition.check(loc);

        // Assert
        assertFalse(result);
    }
}
