package com.jerae.zephaire.particles;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.data.DataManager;
import com.jerae.zephaire.particles.data.ParticleCreationData;
import com.jerae.zephaire.particles.loaders.AnimatedParticleLoader;
import com.jerae.zephaire.particles.loaders.EntityParticleLoader;
import com.jerae.zephaire.particles.loaders.StaticParticleLoader;
import com.jerae.zephaire.particles.managers.EntityParticleManager;
import com.jerae.zephaire.particles.managers.FactoryManager;
import com.jerae.zephaire.particles.managers.ParticleManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ParticleConfigLoaderTest {

    @Mock
    private Zephaire plugin;
    @Mock
    private FactoryManager factoryManager;
    @Mock
    private ParticleManager particleManager;
    @Mock
    private EntityParticleManager entityParticleManager;
    @Mock
    private DataManager dataManager;
    @Mock
    private FileConfiguration staticParticlesConfig;
    @Mock
    private FileConfiguration animatedParticlesConfig;
    @Mock
    private ConfigurationSection staticSection;
    @Mock
    private ConfigurationSection animatedSection;
    @Mock
    private ConfigurationSection particleConfig;

    private ParticleConfigLoader particleConfigLoader;
    @Mock
    private WorldProvider worldProvider;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(plugin.getLogger()).thenReturn(java.util.logging.Logger.getLogger("TestLogger"));
        when(plugin.getDataManager()).thenReturn(dataManager);
        when(plugin.getStaticParticlesConfig()).thenReturn(staticParticlesConfig);
        when(plugin.getAnimatedParticlesConfig()).thenReturn(animatedParticlesConfig);
        when(staticParticlesConfig.getConfigurationSection("static-particles")).thenReturn(staticSection);
        when(animatedParticlesConfig.getConfigurationSection("animated-particles")).thenReturn(animatedSection);
        when(staticSection.getName()).thenReturn("static-particles");

        particleConfigLoader = new ParticleConfigLoader(plugin, factoryManager, particleManager, entityParticleManager, worldProvider);

        // Mock internal loaders
        try {
            Field staticLoaderField = ParticleConfigLoader.class.getDeclaredField("staticLoader");
            staticLoaderField.setAccessible(true);
            staticLoaderField.set(particleConfigLoader, mock(StaticParticleLoader.class));

            Field animatedLoaderField = ParticleConfigLoader.class.getDeclaredField("animatedLoader");
            animatedLoaderField.setAccessible(true);
            animatedLoaderField.set(particleConfigLoader, mock(AnimatedParticleLoader.class));

            Field entityLoaderField = ParticleConfigLoader.class.getDeclaredField("entityLoader");
            entityLoaderField.setAccessible(true);
            entityLoaderField.set(particleConfigLoader, mock(EntityParticleLoader.class));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCaching() throws Exception {
        // Arrange
        when(worldProvider.getWorld("test_world")).thenReturn(mock(org.bukkit.World.class));
        when(plugin.isStaticParticlesEnabled()).thenReturn(true);
        when(staticSection.getKeys(false)).thenReturn(Set.of("test_particle"));
        when(staticSection.getConfigurationSection("test_particle")).thenReturn(particleConfig);
        when(particleConfig.getString("shape", "POINT")).thenReturn("POINT");
        when(particleConfig.getString("world", "world")).thenReturn("test_world");
        when(particleConfig.getMapList("conditions")).thenReturn(java.util.Collections.emptyList());
        when(plugin.getDataManager().isParticleDisabled(anyString())).thenReturn(false);

        // Act
        particleConfigLoader.loadParticles();

        // Assert
        Field cacheField = ParticleConfigLoader.class.getDeclaredField("creationDataCache");
        cacheField.setAccessible(true);
        Map<String, ParticleCreationData> cache = (Map<String, ParticleCreationData>) cacheField.get(particleConfigLoader);

        assertEquals(1, cache.size());
        assertTrue(cache.containsKey("static-particles.test_particle"));

        // Act again
        particleConfigLoader.loadParticles();

        // Assert that getConfigurationSection is only called once
        verify(staticSection, times(1)).getConfigurationSection("test_particle");
    }
}
