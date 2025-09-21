package com.jerae.zephaire.particles;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.util.ParticleUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.Vibration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ParticleUtilsTest {

    private MockedStatic<JavaPlugin> mockedPlugin;
    private MockedStatic<Bukkit> mockedBukkit;
    private Zephaire mockZephaire;

    @BeforeEach
    public void setUp() {
        mockZephaire = mock(Zephaire.class);
        when(mockZephaire.getLogger()).thenReturn(Logger.getLogger("TestLogger"));

        mockedPlugin = mockStatic(JavaPlugin.class);
        mockedPlugin.when(() -> JavaPlugin.getPlugin(Zephaire.class)).thenReturn(mockZephaire);

        mockedBukkit = mockStatic(Bukkit.class);
    }

    @AfterEach
    public void tearDown() {
        mockedPlugin.close();
        mockedBukkit.close();
    }

    @Test
    public void testParseVibrationParticle() {
        World mockWorld = mock(World.class);
        when(mockWorld.getName()).thenReturn("test_world");
        mockedBukkit.when(() -> Bukkit.getWorld(anyString())).thenReturn(mockWorld);

        ConfigurationSection optionsSection = mock(ConfigurationSection.class);
        ConfigurationSection destSection = mock(ConfigurationSection.class);
        when(optionsSection.getParent()).thenReturn(mock(ConfigurationSection.class));


        when(optionsSection.getConfigurationSection("destination")).thenReturn(destSection);
        when(destSection.getString("world", "world")).thenReturn("test_world");
        when(destSection.getDouble("x")).thenReturn(10.0);
        when(destSection.getDouble("y")).thenReturn(20.0);
        when(destSection.getDouble("z")).thenReturn(30.0);
        when(optionsSection.getInt("arrival-time", 20)).thenReturn(100);

        Object result = ParticleUtils.parseParticleOptions(Particle.VIBRATION, optionsSection);

        assertNotNull(result, "Result should not be null for VIBRATION particle");
        assertTrue(result instanceof Vibration, "Result should be a Vibration object");

        Vibration vibration = (Vibration) result;
        assertEquals(100, vibration.getArrivalTime(), "Arrival time should be parsed correctly");
        verify(destSection).getDouble("x");
        verify(destSection).getDouble("y");
        verify(destSection).getDouble("z");
    }

    @Test
    public void testParseShriekParticle() {
        ConfigurationSection optionsSection = mock(ConfigurationSection.class);
        when(optionsSection.getInt("delay", 0)).thenReturn(5);

        Object result = ParticleUtils.parseParticleOptions(Particle.SHRIEK, optionsSection);

        assertNotNull(result, "Result should not be null for SHRIEK particle");
        assertTrue(result instanceof Integer, "Result should be an Integer");
        assertEquals(5, (Integer) result, "Delay should be parsed correctly");
    }

    @Test
    public void testParseSculkChargeParticle() {
        ConfigurationSection optionsSection = mock(ConfigurationSection.class);
        when(optionsSection.getDouble("roll", 0.0)).thenReturn(0.5);

        Object result = ParticleUtils.parseParticleOptions(Particle.SCULK_CHARGE, optionsSection);

        assertNotNull(result, "Result should not be null for SCULK_CHARGE particle");
        assertTrue(result instanceof Float, "Result should be a Float");
        assertEquals(0.5f, (Float) result, 0.001f, "Roll should be parsed correctly");
    }

    @Test
    public void testParseTrailParticle() {
        ConfigurationSection optionsSection = mock(ConfigurationSection.class);
        when(optionsSection.getInt("duration", 20)).thenReturn(100);

        Object result = ParticleUtils.parseParticleOptions(Particle.TRAIL, optionsSection);

        assertNotNull(result, "Result should not be null for TRAIL particle");
        assertTrue(result instanceof Integer, "Result should be an Integer");
        assertEquals(100, (Integer) result, "Duration should be parsed correctly");
    }
}
