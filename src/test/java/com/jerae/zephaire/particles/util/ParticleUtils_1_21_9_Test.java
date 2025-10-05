package com.jerae.zephaire.particles.util;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.nms.NMSManager;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ParticleUtils_1_21_9_Test {

    @Mock
    private Zephaire plugin;

    private FileConfiguration config;
    private static ServerMock server;

    @BeforeAll
    public static void setUpClass() {
        server = MockBukkit.mock();
    }

    @AfterAll
    public static void tearDownClass() {
        MockBukkit.unmock();
    }

    @BeforeEach
    public void setUp() {
        config = new YamlConfiguration();
    }

    @Test
    public void testFlashParticle_withColor_on_1_21_9() {
        try (MockedStatic<NMSManager> mockedNMSManager = Mockito.mockStatic(NMSManager.class)) {
            mockedNMSManager.when(() -> NMSManager.isVersionAtLeast("1.21.9")).thenReturn(true);

            config.set("particle.options.color", "FF0000");
            Object options = ParticleUtils.parseParticleOptions(Particle.FLASH, config.getConfigurationSection("particle.options"));

            assertNotNull(options, "Options should not be null for FLASH on 1.21.9+ with color");
            assertEquals(Color.RED, options, "Color should be parsed correctly");
        }
    }

    @Test
    public void testFlashParticle_withoutColor_on_1_21_9() {
        try (MockedStatic<NMSManager> mockedNMSManager = Mockito.mockStatic(NMSManager.class)) {
            mockedNMSManager.when(() -> NMSManager.isVersionAtLeast("1.21.9")).thenReturn(true);

            Object options = ParticleUtils.parseParticleOptions(Particle.FLASH, config.getConfigurationSection("particle.options"));

            assertNull(options, "Options should be null for FLASH on 1.21.9+ without color");
        }
    }

    @Test
    public void testInstantEffectParticle_withColor_on_1_21_9() {
        try (MockedStatic<NMSManager> mockedNMSManager = Mockito.mockStatic(NMSManager.class)) {
            mockedNMSManager.when(() -> NMSManager.isVersionAtLeast("1.21.9")).thenReturn(true);

            config.set("particle.options.color", "00FF00");
            Object options = ParticleUtils.parseParticleOptions(Particle.INSTANT_EFFECT, config.getConfigurationSection("particle.options"));

            assertNotNull(options, "Options should not be null for INSTANT_EFFECT on 1.21.9+ with color");
            assertEquals(Color.LIME, options, "Color should be parsed correctly");
        }
    }

    @Test
    public void testEffectParticle_withColor_on_1_21_9() {
        try (MockedStatic<NMSManager> mockedNMSManager = Mockito.mockStatic(NMSManager.class)) {
            mockedNMSManager.when(() -> NMSManager.isVersionAtLeast("1.21.9")).thenReturn(true);

            config.set("particle.options.color", "0000FF");
            Object options = ParticleUtils.parseParticleOptions(Particle.EFFECT, config.getConfigurationSection("particle.options"));

            assertNotNull(options, "Options should not be null for EFFECT on 1.21.9+ with color");
            assertEquals(Color.BLUE, options, "Color should be parsed correctly");
        }
    }

    @Test
    public void testFlashParticle_on_older_version() {
        try (MockedStatic<NMSManager> mockedNMSManager = Mockito.mockStatic(NMSManager.class)) {
            mockedNMSManager.when(() -> NMSManager.isVersionAtLeast("1.21.9")).thenReturn(false);

            config.set("particle.options.color", "FF0000");
            Object options = ParticleUtils.parseParticleOptions(Particle.FLASH, config.getConfigurationSection("particle.options"));

            assertNull(options, "Options should be null for FLASH on older versions");
        }
    }
}