package com.jerae.zephaire.listeners;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.ParticleRegistry;
import com.jerae.zephaire.particles.conditions.AnvilCondition;
import com.jerae.zephaire.particles.conditions.EnchantCondition;
import org.bukkit.entity.Player;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EnchantAnvilListenerTest {

    @Mock
    private Zephaire plugin;
    @Mock
    private Player player;
    @Mock
    private EnchantItemEvent enchantEvent;
    @Mock
    private PrepareAnvilEvent anvilEvent;
    @Mock
    private AnvilInventory anvilInventory;

    private EnchantListener enchantListener;
    private AnvilListener anvilListener;

    @BeforeEach
    public void setUp() {
        enchantListener = new EnchantListener(plugin);
        anvilListener = new AnvilListener(plugin);
    }

    @Test
    public void testEnchantListener() {
        EnchantCondition enchantCondition = spy(new EnchantCondition());
        try (MockedStatic<ParticleRegistry> mocked = mockStatic(ParticleRegistry.class)) {
            mocked.when(ParticleRegistry::getEnchantConditions).thenReturn(Collections.singletonList(enchantCondition));
            // Act
            enchantListener.onEnchantItem(enchantEvent);

            // Assert
            verify(enchantCondition, times(1)).trigger();
        }
    }

    @Test
    public void testAnvilListener() {
        AnvilCondition anvilCondition = spy(new AnvilCondition());
        try (MockedStatic<ParticleRegistry> mocked = mockStatic(ParticleRegistry.class)) {
            mocked.when(ParticleRegistry::getAnvilConditions).thenReturn(Collections.singletonList(anvilCondition));

            // Act
            anvilListener.onPrepareAnvil(anvilEvent);

            // Assert
            verify(anvilCondition, times(1)).trigger();
        }
    }
}
