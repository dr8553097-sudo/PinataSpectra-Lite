package net.dafealru.pinataspectralite;

import net.dafealru.pinataspectralite.commands.PinataLiteCommand;
import net.dafealru.pinataspectralite.config.ConfigUpdaterEngine;
import net.dafealru.pinataspectralite.database.DatabaseManager;
import net.dafealru.pinataspectralite.finale.GrandFinaleEngine;
import net.dafealru.pinataspectralite.gui.GUIListener;
import net.dafealru.pinataspectralite.hooks.VaultHook;
import net.dafealru.pinataspectralite.loot.LootManager;
import net.dafealru.pinataspectralite.pinata.PinataInstance;
import net.dafealru.pinataspectralite.pinata.PinataProfile;
import net.dafealru.pinataspectralite.pinata.PinataRegistry;
import net.dafealru.pinataspectralite.votes.VoteGoalManager;
import net.dafealru.pinataspectralite.votes.VoteListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class PinataPartyLite extends JavaPlugin {

    private static PinataPartyLite instance;

    private ConfigUpdaterEngine configUpdaterEngine;
    private DatabaseManager databaseManager;
    private VaultHook vaultHook;
    private LootManager lootManager;
    private GrandFinaleEngine grandFinaleEngine;
    private PinataRegistry registry;
    private VoteGoalManager voteGoalManager;
    private net.dafealru.pinataspectralite.pool.PinataPoolManager pinataPoolManager;
    private net.dafealru.pinataspectralite.effects.DamagePopupEngine damagePopupEngine;
    private net.dafealru.pinataspectralite.schedule.PinataScheduler pinataScheduler;
    private net.dafealru.pinataspectralite.messages.MessageManager messageManager;
    private net.dafealru.pinataspectralite.clean.PinataCleanEngine cleanEngine;
    private net.dafealru.pinataspectralite.discord.DiscordWebhookService discordWebhookService;
    private PinataInstance activeInstance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // 1. Automatic Config Updater Engine
        this.configUpdaterEngine = new ConfigUpdaterEngine(this);
        this.configUpdaterEngine.updateAllConfigs();

        // 2. Initialize Subsystems
        this.messageManager = new net.dafealru.pinataspectralite.messages.MessageManager(this);
        this.cleanEngine = new net.dafealru.pinataspectralite.clean.PinataCleanEngine(this);

        this.databaseManager = new DatabaseManager(this);
        this.databaseManager.initialize();

        this.vaultHook = new VaultHook(this);
        this.lootManager = new LootManager(this);
        this.grandFinaleEngine = new GrandFinaleEngine(this);
        this.damagePopupEngine = new net.dafealru.pinataspectralite.effects.DamagePopupEngine(this);
        this.discordWebhookService = new net.dafealru.pinataspectralite.discord.DiscordWebhookService(this);

        this.registry = new PinataRegistry(this);
        this.registry.loadProfiles();

        this.voteGoalManager = new VoteGoalManager(this);
        this.pinataPoolManager = new net.dafealru.pinataspectralite.pool.PinataPoolManager(this);
        this.pinataScheduler = new net.dafealru.pinataspectralite.schedule.PinataScheduler(this);

        // 3. Register Commands & Listeners
        PinataLiteCommand cmdHandler = new PinataLiteCommand(this);
        if (getCommand("pinata") != null) {
            getCommand("pinata").setExecutor(cmdHandler);
            getCommand("pinata").setTabCompleter(cmdHandler);
        }
        if (getCommand("pinatapro") != null) {
            getCommand("pinatapro").setExecutor(cmdHandler);
        }
        if (getCommand("vote") != null) {
            getCommand("vote").setExecutor(cmdHandler);
        }
        if (getCommand("clean") != null) {
            getCommand("clean").setExecutor(cmdHandler);
        }

        Bukkit.getPluginManager().registerEvents(new GUIListener(this), this);

        if (Bukkit.getPluginManager().isPluginEnabled("Votifier") || Bukkit.getPluginManager().isPluginEnabled("NuVotifier")) {
            Bukkit.getPluginManager().registerEvents(new VoteListener(this), this);
            getLogger().info("[Votes] NuVotifier hook detected and registered!");
        }

        // 4. Print Stylized Console Banner
        if (getConfig().getBoolean("settings.show-pro-banner", true)) {
            var console = getServer().getConsoleSender();
            var mm = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage();
            console.sendMessage(mm.deserialize("<gradient:#EC4899:#F43F5E:#FCD34D>================================================================</gradient>"));
            console.sendMessage(mm.deserialize("<gradient:#EC4899:#F43F5E><bold>  ____  _             _          ____                  _             </bold></gradient>"));
            console.sendMessage(mm.deserialize("<gradient:#EC4899:#F43F5E><bold> |  _ \\(_)_ __   __ _| |_ __ _  / ___| _ __   ___  ___| |_ _ __ __ _ </bold></gradient>"));
            console.sendMessage(mm.deserialize("<gradient:#EC4899:#F43F5E><bold> | |_) | | '_ \\ / _` | __/ _` | \\___ \\| '_ \\ / _ \\/ __| __| '__/ _` |</bold></gradient>"));
            console.sendMessage(mm.deserialize("<gradient:#F43F5E:#FCD34D><bold> |  __/| | | | | (_| | || (_| |  ___) | |_) |  __/ (__| |_| | | (_| |</bold></gradient>"));
            console.sendMessage(mm.deserialize("<gradient:#F43F5E:#FCD34D><bold> |_|   |_|_| |_|\\__,_|\\__\\__,_| |____/| .__/ \\___|\\___|\\__|_|  \\__,_|</bold></gradient>"));
            console.sendMessage(mm.deserialize("<gradient:#FCD34D:#22C55E><bold>                                      |_|   🍃 L I T E   E D I T I O N </bold></gradient>"));
            console.sendMessage(mm.deserialize("<gradient:#EC4899:#F43F5E:#FCD34D>================================================================</gradient>"));
            console.sendMessage(mm.deserialize("  <#FCD34D>★</#FCD34D> <white>Version:</white> <#EC4899>v" + getDescription().getVersion() + "</#EC4899> <gray>| Native Java 21 & Paper 1.20 - 1.26</gray>"));
            console.sendMessage(mm.deserialize("  <#FCD34D>👑</#FCD34D> <white>Author:</white> <#38BDF8>Dafealru</#38BDF8> <gray>| Standard 3D Voxel Engine</gray>"));
            console.sendMessage(mm.deserialize("  <#22C55E>✔</#22C55E> <white>Profiles Loaded:</white> <green>" + registry.getAllProfiles().size() + "</green> <gray>| Fountain Death FX Active</gray>"));
            console.sendMessage(mm.deserialize("  <#8B5CF6>✦</#8B5CF6> <white>Sovereign PRO:</white> <#FCD34D><underlined>https://builtbybit.com/pinataspectra</underlined></#FCD34D>"));
            console.sendMessage(mm.deserialize("<gradient:#EC4899:#F43F5E:#FCD34D>================================================================</gradient>"));
        }
    }

    @Override
    public void onDisable() {
        if (activeInstance != null) {
            activeInstance.remove();
            activeInstance = null;
        }
        if (pinataScheduler != null) {
            pinataScheduler.stop();
        }
        if (databaseManager != null) {
            databaseManager.close();
        }
        getLogger().info("PinataSpectra Lite safely disabled.");
    }

    public void spawnPinata(Location location, PinataProfile profile) {
        if (activeInstance != null) {
            activeInstance.remove();
        }
        activeInstance = new PinataInstance(this, location, profile);
        activeInstance.start();
        if (discordWebhookService != null) {
            discordWebhookService.sendSpawnNotification(profile != null ? profile.getId() : "Piñata");
        }
    }

    public void saveLocation(String name, Location loc) {
        if (loc == null || loc.getWorld() == null) return;
        String path = "spawns." + name.toLowerCase();
        getConfig().set(path + ".world", loc.getWorld().getName());
        getConfig().set(path + ".x", loc.getX());
        getConfig().set(path + ".y", loc.getY());
        getConfig().set(path + ".z", loc.getZ());
        getConfig().set(path + ".yaw", loc.getYaw());
        getConfig().set(path + ".pitch", loc.getPitch());
        saveConfig();
    }

    public Location getSavedLocation(String name) {
        String path = "spawns." + name.toLowerCase();
        if (!getConfig().contains(path + ".world")) return null;
        String worldName = getConfig().getString(path + ".world");
        org.bukkit.World world = Bukkit.getWorld(worldName);
        if (world == null) return null;
        double x = getConfig().getDouble(path + ".x");
        double y = getConfig().getDouble(path + ".y");
        double z = getConfig().getDouble(path + ".z");
        float yaw = (float) getConfig().getDouble(path + ".yaw", 0.0);
        float pitch = (float) getConfig().getDouble(path + ".pitch", 0.0);
        return new Location(world, x, y, z, yaw, pitch);
    }

    public java.util.Set<String> getSavedLocationNames() {
        if (getConfig().isConfigurationSection("spawns")) {
            return getConfig().getConfigurationSection("spawns").getKeys(false);
        }
        return java.util.Collections.emptySet();
    }

    public static PinataPartyLite getInstance() { return instance; }
    public ConfigUpdaterEngine getConfigUpdaterEngine() { return configUpdaterEngine; }
    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public VaultHook getVaultHook() { return vaultHook; }
    public LootManager getLootManager() { return lootManager; }
    public GrandFinaleEngine getGrandFinaleEngine() { return grandFinaleEngine; }
    public net.dafealru.pinataspectralite.effects.DamagePopupEngine getDamagePopupEngine() { return damagePopupEngine; }
    public net.dafealru.pinataspectralite.pool.PinataPoolManager getPinataPoolManager() { return pinataPoolManager; }
    public net.dafealru.pinataspectralite.schedule.PinataScheduler getPinataScheduler() { return pinataScheduler; }
    public net.dafealru.pinataspectralite.messages.MessageManager getMessageManager() { return messageManager; }
    public net.dafealru.pinataspectralite.clean.PinataCleanEngine getCleanEngine() { return cleanEngine; }
    public PinataRegistry getRegistry() { return registry; }
    public VoteGoalManager getVoteGoalManager() { return voteGoalManager; }
    public net.dafealru.pinataspectralite.discord.DiscordWebhookService getDiscordWebhookService() { return discordWebhookService; }
    public PinataInstance getActiveInstance() { return activeInstance; }
    public void setActiveInstance(PinataInstance activeInstance) { this.activeInstance = activeInstance; }
}
