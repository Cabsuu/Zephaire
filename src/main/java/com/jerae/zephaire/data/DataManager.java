package com.jerae.zephaire.data;

import com.jerae.zephaire.Zephaire;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

/**
 * Manages the persistent state of disabled particles.
 */
public class DataManager {

    private final Zephaire plugin;
    private FileConfiguration disabledParticlesConfig = null;
    private final File disabledParticlesFile;
    private final Set<String> disabledParticles = new HashSet<>();

    public DataManager(Zephaire plugin) {
        this.plugin = plugin;
        this.disabledParticlesFile = new File(plugin.getDataFolder(), "disabled-particles.yml");
    }

    public void load() {
        if (!disabledParticlesFile.exists()) {
            plugin.saveResource("disabled-particles.yml", false);
        }

        disabledParticlesConfig = YamlConfiguration.loadConfiguration(disabledParticlesFile);
        disabledParticles.clear();
        disabledParticles.addAll(disabledParticlesConfig.getStringList("disabled-list"));
    }

    public void save() {
        // Save the list of disabled particles to the file
        disabledParticlesConfig.set("disabled-list", new ArrayList<>(disabledParticles));
        try {
            disabledParticlesConfig.save(disabledParticlesFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save disabled-particles.yml!", e);
        }
    }

    public boolean isParticleDisabled(String particleName) {
        return disabledParticles.contains(particleName.toLowerCase());
    }

    public boolean toggleParticle(String particleName) {
        String lowerCaseName = particleName.toLowerCase();
        boolean isDisabled = isParticleDisabled(lowerCaseName);

        if (isDisabled) {
            disabledParticles.remove(lowerCaseName);
        } else {
            disabledParticles.add(lowerCaseName);
        }
        save();
        return !isDisabled; // Return the new state (true if it's now enabled)
    }
}

