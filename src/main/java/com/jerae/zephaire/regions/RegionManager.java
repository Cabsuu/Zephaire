package com.jerae.zephaire.regions;

import com.jerae.zephaire.Zephaire;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegionManager {

    private final Zephaire plugin;
    private final Map<String, Region> regions = new HashMap<>();
    private FileConfiguration regionConfig;
    private File regionFile;

    public RegionManager(Zephaire plugin) {
        this.plugin = plugin;
        setup();
        loadRegions();
    }

    public void setup() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        regionFile = new File(plugin.getDataFolder(), "regions.yml");
        if (!regionFile.exists()) {
            plugin.saveResource("regions.yml", false);
        }
        regionConfig = YamlConfiguration.loadConfiguration(regionFile);
    }

    public void loadRegions() {
        regions.clear();
        ConfigurationSection regionsSection = regionConfig.getConfigurationSection("regions");
        if (regionsSection == null) {
            return;
        }

        for (String key : regionsSection.getKeys(false)) {
            ConfigurationSection regionSection = regionsSection.getConfigurationSection(key);
            if (regionSection == null) {
                continue;
            }

            String worldName = regionSection.getString("world");
            if (worldName == null) {
                continue;
            }

            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                continue;
            }

            Vector min = regionSection.getVector("min");
            Vector max = regionSection.getVector("max");

            if (min == null || max == null) {
                continue;
            }

            regions.put(key.toLowerCase(), new Region(key, world, min, max));
        }
    }

    public Region getRegion(String name, World world) {
        Region region = regions.get(name.toLowerCase());
        if (region != null && region.getWorld().equals(world)) {
            return region;
        }
        return null;
    }

    public Region getRegionByName(String name) {
        return regions.get(name.toLowerCase());
    }

    public List<Region> getRegions(Location location) {
        List<Region> containingRegions = new ArrayList<>();
        for (Region region : regions.values()) {
            if (region.contains(location)) {
                containingRegions.add(region);
            }
        }
        return containingRegions;
    }

    public List<Region> getRegions(World world) {
        List<Region> worldRegions = new ArrayList<>();
        for (Region region : regions.values()) {
            if (region.getWorld().equals(world)) {
                worldRegions.add(region);
            }
        }
        return worldRegions;
    }
}
