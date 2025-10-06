package com.jerae.zephaire;

import com.jerae.zephaire.commands.ZephaireCommand;
import com.jerae.zephaire.listeners.AnvilListener;
import com.jerae.zephaire.listeners.EnchantListener;
import com.jerae.zephaire.listeners.EntityListener;
import com.jerae.zephaire.listeners.PlayerInteractListener;
import com.jerae.zephaire.particles.ParticleScheduler;
import com.jerae.zephaire.particles.managers.EntityParticleManager;
import com.jerae.zephaire.particles.managers.FactoryManager;
import com.jerae.zephaire.particles.ParticleConfigLoader;
import com.jerae.zephaire.particles.managers.ParticleGroupManager;
import com.jerae.zephaire.particles.managers.ParticleManager;
import com.jerae.zephaire.particles.ParticleRegistry;
import com.jerae.zephaire.particles.managers.PerformanceManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.List;

public class Zephaire extends JavaPlugin {

    private FactoryManager factoryManager;
    private ParticleManager particleManager;
    private ParticleConfigLoader particleConfigLoader;
    private EntityParticleManager entityParticleManager;
    private ParticleGroupManager particleGroupManager;
    private BukkitTask particleSchedulerTask;
    private FileConfiguration entityParticlesConfig;
    private File entityParticlesFile;
    private FileConfiguration staticParticlesConfig;
    private File staticParticlesFile;
    private FileConfiguration animatedParticlesConfig;
    private File animatedParticlesFile;
    private boolean staticParticlesEnabled;
    private boolean animatedParticlesEnabled;
    private boolean entityParticlesEnabled;
    private List<String> disabledWorlds;


    @Override
    public void onEnable() {
        // --- SAVE DEFAULT FILES ---
        this.saveDefaultConfig();
        this.saveResource("guides/static-guide.txt", true);
        this.saveResource("guides/animated-guide.txt", true);
        this.saveResource("guides/condition-guide.txt", true);
        this.saveResource("guides/decorator-guide.txt", true);
        this.saveResource("guides/entity-guide.txt", true);
        this.saveResource("guides/particle-type-guide.txt", true);
        this.saveResource("particles/particles.txt", true);
        this.saveResource("particles/entity-particles.yml", false);
        this.saveResource("particles/static-particles.yml", false);
        this.saveResource("particles/animated-particles.yml", false);
        this.saveResource("particles/particle-groups.yml", false);


        // --- INITIALIZE CORE COMPONENTS ---
        this.factoryManager = new FactoryManager();
        this.particleManager = new ParticleManager(this);
        this.entityParticleManager = new EntityParticleManager(this);
        this.particleGroupManager = new ParticleGroupManager(this);

        // Load entity-particles.yml
        this.entityParticlesFile = new File(getDataFolder(), "particles/entity-particles.yml");
        this.entityParticlesConfig = YamlConfiguration.loadConfiguration(entityParticlesFile);

        this.staticParticlesFile = new File(getDataFolder(), "particles/static-particles.yml");
        this.staticParticlesConfig = YamlConfiguration.loadConfiguration(staticParticlesFile);

        this.animatedParticlesFile = new File(getDataFolder(), "particles/animated-particles.yml");
        this.animatedParticlesConfig = YamlConfiguration.loadConfiguration(animatedParticlesFile);

        this.particleConfigLoader = new ParticleConfigLoader(this, factoryManager, particleManager, entityParticleManager);
        this.particleManager.setConfigLoader(this.particleConfigLoader); // Link loader to manager

        // Make the particle manager globally accessible
        ParticleRegistry.initialize(this.particleManager);
        ParticleScheduler.initialize(this);
        com.jerae.zephaire.particles.util.ParticleUtils.initialize(this);


        // --- LOAD DATA AND PARTICLES ---
        this.particleGroupManager.initialize();
        // Perform the initial load of configuration and particles.
        reloadPluginConfig();


        // --- REGISTER COMMANDS AND LISTENERS ---
        PluginCommand zephairePluginCommand = this.getCommand("zephaire");
        if (zephairePluginCommand != null) {
            ZephaireCommand commandHandler = new ZephaireCommand(this);
            zephairePluginCommand.setExecutor(commandHandler);
            zephairePluginCommand.setTabCompleter(commandHandler);

            // Dynamically register the alias
            String alias = getConfig().getString("command-alias");
            if (alias != null && !alias.isEmpty()) {
                try {
                    final java.lang.reflect.Field commandMapField = getServer().getClass().getDeclaredField("commandMap");
                    commandMapField.setAccessible(true);
                    final org.bukkit.command.CommandMap commandMap = (org.bukkit.command.CommandMap) commandMapField.get(getServer());
                    commandMap.register(alias, getName(), zephairePluginCommand);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    getLogger().log(java.util.logging.Level.SEVERE, "Failed to register command alias", e);
                }
            }
        }

        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
        getServer().getPluginManager().registerEvents(new EnchantListener(this), this);
        getServer().getPluginManager().registerEvents(new AnvilListener(this), this);
    }

    /**
     * Handles the logic for reloading the plugin's configuration and particle effects.
     */
    public void reloadPluginConfig() {
        // 1. Reload the config.yml file from disk and initialize performance settings.
        reloadConfig();
        PerformanceManager.initialize(this, getConfig());
        this.disabledWorlds = getConfig().getStringList("disabled-worlds");
        this.staticParticlesEnabled = getConfig().getBoolean("plugin-features.enable-static-particles", true);
        this.animatedParticlesEnabled = getConfig().getBoolean("plugin-features.enable-animated-particles", true);
        this.entityParticlesEnabled = getConfig().getBoolean("plugin-features.enable-entity-particles", true);

        // Reload entity-particles.yml
        this.entityParticlesConfig = YamlConfiguration.loadConfiguration(entityParticlesFile);
        this.staticParticlesConfig = YamlConfiguration.loadConfiguration(staticParticlesFile);
        this.animatedParticlesConfig = YamlConfiguration.loadConfiguration(animatedParticlesFile);
        this.particleGroupManager.loadParticleGroups();


        // 2. Stop any existing particle scheduler to prevent duplicates.
        if (this.particleSchedulerTask != null) {
            try {
                this.particleSchedulerTask.cancel();
            } catch (IllegalStateException ignored) {}
        }

        // 3. Re-initialize the particle managers and load the new particle configurations.
        if (this.particleConfigLoader != null) {
            this.particleConfigLoader.clearCache();
        }
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

    public EntityParticleManager getEntityParticleManager() {
        return entityParticleManager;
    }

    public ParticleGroupManager getParticleGroupManager() {
        return particleGroupManager;
    }

    public FileConfiguration getEntityParticlesConfig() {
        return entityParticlesConfig;
    }

    public FileConfiguration getStaticParticlesConfig() {
        return staticParticlesConfig;
    }

    public FileConfiguration getAnimatedParticlesConfig() {
        return animatedParticlesConfig;
    }

    public boolean isStaticParticlesEnabled() {
        return staticParticlesEnabled;
    }

    public boolean isAnimatedParticlesEnabled() {
        return animatedParticlesEnabled;
    }

    public boolean isEntityParticlesEnabled() {
        return entityParticlesEnabled;
    }

    public List<String> getDisabledWorlds() {
        return disabledWorlds;
    }
}
