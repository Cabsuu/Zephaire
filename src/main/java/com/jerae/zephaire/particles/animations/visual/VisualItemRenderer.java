package com.jerae.zephaire.particles.animations.visual;

import com.jerae.zephaire.Zephaire;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Renders a cosmetic, non-interactive item entity as a "particle".
 * The item cannot be picked up, is not affected by gravity, and will be removed after a configured time.
 */
public class VisualItemRenderer implements IParticleRenderer {

    private final Zephaire plugin;
    private final Material material;
    private final int despawnTimer;

    public VisualItemRenderer(Zephaire plugin, Material material, int despawnTimer) {
        this.plugin = plugin;
        this.material = material;
        // Default to 5 seconds (100 ticks) if the despawn timer is invalid.
        this.despawnTimer = despawnTimer > 0 ? despawnTimer : 100;
    }

    @Override
    public void render(Location location) {
        World world = location.getWorld();
        if (world == null) return;

        // Spawn the item entity.
        Item item = world.dropItem(location, new ItemStack(material));

        // Make the item purely cosmetic.
        item.setCanPlayerPickup(false);
        item.setGravity(false);
        item.setInvulnerable(true);
        item.setVelocity(new Vector(0, 0, 0)); // Ensure it has no initial velocity.
        item.setUnlimitedLifetime(true); // Prevents default despawning.

        // Schedule its removal using our custom timer.
        new BukkitRunnable() {
            @Override
            public void run() {
                if (item.isValid()) {
                    item.remove();
                }
            }
        }.runTaskLater(plugin, this.despawnTimer);
    }
}
