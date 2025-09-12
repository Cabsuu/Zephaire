package com.jerae.zephaire;

import com.jerae.zephaire.commands.ZephaireCommand;
import com.jerae.zephaire.data.DataManager;
import com.jerae.zephaire.listeners.PlayerInteractListener;
import com.jerae.zephaire.particles.FactoryManager;
import com.jerae.zephaire.particles.ParticleConfigLoader;
import com.jerae.zephaire.particles.ParticleManager;
import com.jerae.zephaire.particles.ParticleRegistry;
import com.jerae.zephaire.particles.PerformanceManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Zephaire extends JavaPlugin {

    private FactoryManager factoryManager;
    private ParticleManager particleManager;
    private DataManager dataManager;
    private ParticleConfigLoader particleConfigLoader;


    @Override
    public void onEnable() {
        // --- SAVE DEFAULT FILES ---
        this.saveDefaultConfig();
        this.saveResource("guide.txt", true);
        this.saveResource("particles.txt", false);
        this.saveResource("disabled-particles.yml", false);


        // --- INITIALIZE CORE COMPONENTS ---
        this.factoryManager = new FactoryManager();
        this.dataManager = new DataManager(this);
        this.particleManager = new ParticleManager(this);
        this.particleConfigLoader = new ParticleConfigLoader(this, factoryManager, particleManager);
        this.particleManager.setConfigLoader(this.particleConfigLoader); // Link loader to manager

        // Make the particle manager globally accessible
        ParticleRegistry.initialize(this.particleManager);

        // --- LOAD DATA AND PARTICLES ---
        this.dataManager.load();
        // Perform the initial load of configuration and particles.
        reloadPluginConfig();


        // --- REGISTER COMMANDS AND LISTENERS ---
        PluginCommand zephairePluginCommand = this.getCommand("zephaire");
        if (zephairePluginCommand != null) {
            ZephaireCommand commandHandler = new ZephaireCommand(this);
            zephairePluginCommand.setExecutor(commandHandler);
            zephairePluginCommand.setTabCompleter(commandHandler);
        }

        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
    }

    /**
     * Handles the logic for reloading the plugin's configuration and particle effects.
     */
    public void reloadPluginConfig() {
        // 1. Reload the config.yml file from disk and initialize performance settings.
        reloadConfig();
        PerformanceManager.initialize(getConfig());

        // 2. Re-initialize the particle manager and load the new particle configurations.
        this.particleManager.initialize();
        this.particleConfigLoader.loadParticles();
        this.particleManager.startAnimationManager();
    }

    // --- GETTERS FOR MANAGERS ---
    public ParticleManager getParticleManager() {
        return particleManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}

