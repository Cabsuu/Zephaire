package com.jerae.zephaire.particles.conditions;

import com.jerae.zephaire.Zephaire;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A condition met when a player interacts with a specific block. Supports two modes:
 * 1. Trigger Mode: A stateless, one-time burst or timed effect.
 * 2. Toggle Mode: A persistent on/off state that survives restarts.
 */
public class BlockInteractCondition implements ParticleCondition {

    // --- Common Properties ---
    private final Location location;
    private final Material material;
    private final Zephaire plugin;

    // --- Mode Flags ---
    private final boolean isToggle;

    // --- Trigger Mode Properties ---
    private final boolean triggerOnce;
    private final long repeatDuration;
    private final Material requiredItem;
    private boolean wasTriggered = false;
    private boolean hasFired = false;
    private long activeTicksRemaining = 0;

    // --- Toggle Mode Properties ---
    private final Material activationItem;
    private final Material deactivationItem;


    public BlockInteractCondition(Location location, Material material, boolean isToggle,
                                  boolean triggerOnce, long repeatDuration, Material requiredItem,
                                  Material activationItem, Material deactivationItem) {
        this.location = location;
        this.material = material;
        this.isToggle = isToggle;
        this.triggerOnce = triggerOnce;
        this.repeatDuration = repeatDuration;
        this.requiredItem = requiredItem;
        this.activationItem = activationItem;
        this.deactivationItem = deactivationItem;
        this.plugin = JavaPlugin.getPlugin(Zephaire.class);
    }


    @Override
    public void tick() {
        // Tick logic is handled inside check() for simplicity.
    }

    @Override
    public boolean check(Location referenceLocation) {
        if (isToggle) {
            // In toggle mode, the state is persistent and managed externally.
            return plugin.getDataManager().isBlockActive(this.location);
        } else {
            // In trigger mode, manage the internal, temporary state.
            if (wasTriggered) {
                wasTriggered = false; // Consume the trigger immediately.
                if (triggerOnce && hasFired) return false;
                hasFired = true;
                return true;
            }

            if (activeTicksRemaining > 0) {
                activeTicksRemaining--;
                if (triggerOnce) hasFired = true;
                return true;
            }
        }
        return false;
    }

    /**
     * Called by an event listener to trigger the non-persistent effect.
     */
    public void trigger() {
        if (isToggle || (triggerOnce && hasFired)) {
            return;
        }

        if (repeatDuration > 0) {
            this.activeTicksRemaining = repeatDuration;
        } else {
            this.wasTriggered = true;
        }
    }

    // --- Getters for the Listener ---
    public Location getLocation() { return location; }
    public Material getMaterial() { return material; }
    public boolean isToggle() { return isToggle; }

    // Trigger-specific getters
    public Material getRequiredItem() { return requiredItem; }

    // Toggle-specific getters
    public Material getActivationItem() { return activationItem; }
    public Material getDeactivationItem() { return deactivationItem; }
}

