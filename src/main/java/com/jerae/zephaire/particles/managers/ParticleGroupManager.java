package com.jerae.zephaire.particles.managers;

import com.jerae.zephaire.Zephaire;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticleGroupManager {

    private final Zephaire plugin;
    private final Map<String, List<String>> particleGroups = new HashMap<>();
    private File particleGroupsFile;
    private FileConfiguration particleGroupsConfig;

    public ParticleGroupManager(Zephaire plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        particleGroupsFile = new File(plugin.getDataFolder(), "particles/particle-groups.yml");
        if (!particleGroupsFile.exists()) {
            plugin.saveResource("particles/particle-groups.yml", false);
        }
        particleGroupsConfig = YamlConfiguration.loadConfiguration(particleGroupsFile);
        loadParticleGroups();
    }

    public void loadParticleGroups() {
        particleGroups.clear();
        ConfigurationSection groupsSection = particleGroupsConfig.getConfigurationSection("particle-groups");
        if (groupsSection != null) {
            for (String groupName : groupsSection.getKeys(false)) {
                List<String> particles = groupsSection.getStringList(groupName);
                particleGroups.put(groupName, particles);
            }
        }
    }

    public List<String> getParticlesInGroup(String groupName) {
        return particleGroups.getOrDefault(groupName, Collections.emptyList());
    }

    public boolean isGroup(String name) {
        return particleGroups.containsKey(name);
    }

    public Map<String, List<String>> getParticleGroups() {
        return particleGroups;
    }
}
