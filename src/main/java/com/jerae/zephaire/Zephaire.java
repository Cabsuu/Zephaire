package com.jerae.zephaire;

import com.jerae.zephaire.commands.ZephaireCommand;
import com.jerae.zephaire.data.DataManager;
import com.jerae.zephaire.listeners.EntityListener;
import com.jerae.zephaire.listeners.PlayerInteractListener;
import com.jerae.zephaire.particles.ParticleScheduler;
import com.jerae.zephaire.particles.managers.EntityParticleManager;
import com.jerae.zephaire.particles.managers.FactoryManager;
import com.jerae.zephaire.particles.ParticleConfigLoader;
import com.jerae.zephaire.particles.managers.ParticleManager;
import com.jerae.zephaire.particles.ParticleRegistry;
import com.jerae.zephaire.particles.managers.PerformanceManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;

public final class Zephaire extends JavaPlugin {

    private FactoryManager factoryManager;
    private ParticleManager particleManager;
    private DataManager dataManager;
    private ParticleConfigLoader particleConfigLoader;
    private EntityParticleManager entityParticleManager;
    private BukkitTask particleSchedulerTask;
    private FileConfiguration entityParticlesConfig;
    private File entityParticlesFile;


    @Override
    public void onEnable() {
        // --- SAVE DEFAULT FILES ---
        this.saveDefaultConfig();
        this.saveResource("guide.txt", true);
        this.saveResource("particles.txt", false);
        this.saveResource("disabled-particles.yml", false);
        this.saveResource("entity-particles.yml", false);


        // --- INITIALIZE CORE COMPONENTS ---
        this.factoryManager = new FactoryManager();
        this.dataManager = new DataManager(this);
        this.particleManager = new ParticleManager(this);
        this.entityParticleManager = new EntityParticleManager(this);

        // Load entity-particles.yml
        this.entityParticlesFile = new File(getDataFolder(), "entity-particles.yml");
        this.entityParticlesConfig = YamlConfiguration.loadConfiguration(entityParticlesFile);

        this.particleConfigLoader = new ParticleConfigLoader(this, factoryManager, particleManager, entityParticleManager);
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
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
    }

    /**
     * Handles the logic for reloading the plugin's configuration and particle effects.
     */
    public void reloadPluginConfig() {
        // 1. Reload the config.yml file from disk and initialize performance settings.
        reloadConfig();
        PerformanceManager.initialize(getConfig());

        // Reload entity-particles.yml
        this.entityParticlesConfig = YamlConfiguration.loadConfiguration(entityParticlesFile);


        // 2. Stop any existing particle scheduler to prevent duplicates.
        if (this.particleSchedulerTask != null) {
            try {
                this.particleSchedulerTask.cancel();
            } catch (IllegalStateException ignored) {}
        }

        // 3. Re-initialize the particle managers and load the new particle configurations.
        this.particleManager.initialize();
        this.entityParticleManager.initialize();
        this.particleConfigLoader.loadParticles();
        this.particleManager.startAnimationManager();
        this.entityParticleManager.startManager();

        // 4. Start the new centralized particle scheduler.
        this.particleSchedulerTask = new ParticleScheduler().runTaskTimer(this, 0L, 1L);
    }

    // --- GETTERS FOR MANAGERS ---
    public ParticleManager getParticleManager() {
        return particleManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public EntityParticleManager getEntityParticleManager() {
        return entityParticleManager;
    }

    public FileConfiguration getEntityParticlesConfig() {
        return entityParticlesConfig;
    }
}