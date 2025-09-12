package com.jerae.zephaire.data;

import com.jerae.zephaire.Zephaire;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

/**
 * Manages the persistent state of toggled blocks.
 */
public class DataManager {

    private final Zephaire plugin;
    private FileConfiguration dataConfig = null;
    private final File configFile;
    private final Set<Location> activeToggledBlocks = new HashSet<>();

    public DataManager(Zephaire plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "toggled-blocks.yml");
    }

    public void load() {
        if (!configFile.exists()) {
            plugin.saveResource("toggled-blocks.yml", false);
        }

        dataConfig = YamlConfiguration.loadConfiguration(configFile);
        activeToggledBlocks.clear();

        ConfigurationSection section = dataConfig.getConfigurationSection("active-toggles");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                String[] parts = key.split(";");
                if (parts.length == 4) {
                    World world = Bukkit.getWorld(parts[0]);
                    if (world != null) {
                        try {
                            double x = Double.parseDouble(parts[1]);
                            double y = Double.parseDouble(parts[2]);
                            double z = Double.parseDouble(parts[3]);
                            activeToggledBlocks.add(new Location(world, x, y, z));
                        } catch (NumberFormatException e) {
                            plugin.getLogger().log(Level.WARNING, "Invalid location format in toggled-blocks.yml: " + key);
                        }
                    }
                }
            }
        }
    }

    public void save() {
        // Clear old data
        dataConfig.set("active-toggles", null);
        // Create a new section to avoid leaving an empty 'active-toggles:' line
        ConfigurationSection section = dataConfig.createSection("active-toggles");

        for (Location loc : activeToggledBlocks) {
            // Use a consistent, parsable format for the location key
            String key = String.format("%s;%.2f;%.2f;%.2f", loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ());
            section.set(key, true);
        }

        try {
            dataConfig.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save toggled-blocks.yml!", e);
        }
    }

    public boolean isBlockActive(Location location) {
        return activeToggledBlocks.contains(location);
    }

    public void setBlockActive(Location location) {
        activeToggledBlocks.add(location);
        save();
    }

    public void setBlockInactive(Location location) {
        activeToggledBlocks.remove(location);
        save();
    }
}
