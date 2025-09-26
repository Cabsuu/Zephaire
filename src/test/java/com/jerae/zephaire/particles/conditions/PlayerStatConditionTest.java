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

public class PlayerStatConditionTest {

    @Mock
    private World world;
    @Mock
    private Player player;

    private Location location;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        location = new Location(world, 10, 10, 10);
        when(world.getPlayers()).thenReturn(Collections.singletonList(player));
        when(player.getLocation()).thenReturn(location);
    }

    @Test
    public void testHealthCondition() {
        when(player.getHealth()).thenReturn(15.0);
        PlayerStatCondition condition = new PlayerStatCondition(PlayerStatCondition.Stat.HEALTH, PlayerStatCondition.Comparison.LESS_THAN, 20.0);
        assertTrue(condition.check(location));

        condition = new PlayerStatCondition(PlayerStatCondition.Stat.HEALTH, PlayerStatCondition.Comparison.GREATER_THAN, 10.0);
        assertTrue(condition.check(location));

        condition = new PlayerStatCondition(PlayerStatCondition.Stat.HEALTH, PlayerStatCondition.Comparison.EQUAL_TO, 15.0);
        assertTrue(condition.check(location));

        condition = new PlayerStatCondition(PlayerStatCondition.Stat.HEALTH, PlayerStatCondition.Comparison.LESS_THAN, 10.0);
        assertFalse(condition.check(location));
    }

    @Test
    public void testHungerCondition() {
        when(player.getFoodLevel()).thenReturn(10);
        PlayerStatCondition condition = new PlayerStatCondition(PlayerStatCondition.Stat.HUNGER, PlayerStatCondition.Comparison.LESS_THAN, 15);
        assertTrue(condition.check(location));

        condition = new PlayerStatCondition(PlayerStatCondition.Stat.HUNGER, PlayerStatCondition.Comparison.GREATER_THAN, 5);
        assertTrue(condition.check(location));

        condition = new PlayerStatCondition(PlayerStatCondition.Stat.HUNGER, PlayerStatCondition.Comparison.EQUAL_TO, 10);
        assertTrue(condition.check(location));

        condition = new PlayerStatCondition(PlayerStatCondition.Stat.HUNGER, PlayerStatCondition.Comparison.GREATER_THAN, 15);
        assertFalse(condition.check(location));
    }

    @Test
    public void testExperienceCondition() {
        when(player.getTotalExperience()).thenReturn(100);
        PlayerStatCondition condition = new PlayerStatCondition(PlayerStatCondition.Stat.EXPERIENCE, PlayerStatCondition.Comparison.GREATER_THAN, 50);
        assertTrue(condition.check(location));

        condition = new PlayerStatCondition(PlayerStatCondition.Stat.EXPERIENCE, PlayerStatCondition.Comparison.LESS_THAN, 150);
        assertTrue(condition.check(location));

        condition = new PlayerStatCondition(PlayerStatCondition.Stat.EXPERIENCE, PlayerStatCondition.Comparison.EQUAL_TO, 100);
        assertTrue(condition.check(location));

        condition = new PlayerStatCondition(PlayerStatCondition.Stat.EXPERIENCE, PlayerStatCondition.Comparison.GREATER_THAN, 150);
        assertFalse(condition.check(location));
    }
}
