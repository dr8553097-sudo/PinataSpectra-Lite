package net.dafealru.pinataspectralite.config;

import net.dafealru.pinataspectralite.PinataPartyLite;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

/**
 * Intelligent Automatic Config & Profile Synchronizer.
 * Ensures all existing and newly created configuration files on disk
 * receive newly introduced keys, default values, and headers from the JAR
 * without overwriting or resetting any user-customized settings.
 */
public class ConfigUpdaterEngine {

    private final PinataPartyLite plugin;

    public ConfigUpdaterEngine(PinataPartyLite plugin) {
        this.plugin = plugin;
    }

    public void updateAllConfigs() {
        // 1. Sync standard root configs
        updateFile("config.yml");
        updateFile("messages.yml");
        updateFile("messages_es.yml");

        // 2. Sync and deploy all default and custom pinatas in pinatas/ folder
        updateFile("pinatas/festive_llama.yml");
        updateFile("pinatas/custom_party.yml");

        // 3. Scan and synchronize any custom pinatas created by admins in /pinatas/
        syncAllCustomPinataProfiles();
    }

    public void updateFile(String resourcePath) {
        File diskFile = new File(plugin.getDataFolder(), resourcePath);
        if (!diskFile.exists()) {
            try {
                if (diskFile.getParentFile() != null && !diskFile.getParentFile().exists()) {
                    diskFile.getParentFile().mkdirs();
                }
                InputStream jarIn = plugin.getResource(resourcePath);
                if (jarIn != null) {
                    plugin.saveResource(resourcePath, false);
                    plugin.getLogger().info("[ConfigUpdater] Deployed default configuration file: " + resourcePath);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("[ConfigUpdater] Failed to deploy " + resourcePath + ": " + e.getMessage());
            }
            return;
        }

        try {
            FileConfiguration diskConfig = YamlConfiguration.loadConfiguration(diskFile);

            InputStream jarStream = plugin.getResource(resourcePath);
            if (jarStream == null) return;

            FileConfiguration jarConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(jarStream, StandardCharsets.UTF_8));

            int addedKeys = mergeSections(jarConfig, diskConfig);
            if (addedKeys > 0) {
                // Ensure header is copied
                if (jarConfig.options().header() != null && (diskConfig.options().header() == null || diskConfig.options().header().isEmpty())) {
                    diskConfig.options().header(jarConfig.options().header());
                }
                diskConfig.options().copyHeader(true);
                diskConfig.save(diskFile);
                plugin.getLogger().info("[ConfigUpdater] ✔ Synchronized " + addedKeys + " new option(s) in " + resourcePath + " preserving your custom settings.");
            }
        } catch (Exception e) {
            plugin.getLogger().warning("[ConfigUpdater] Error synchronizing " + resourcePath + ": " + e.getMessage());
        }
    }

    private void syncAllCustomPinataProfiles() {
        File pinatasDir = new File(plugin.getDataFolder(), "pinatas");
        if (!pinatasDir.exists() || !pinatasDir.isDirectory()) return;

        File[] files = pinatasDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null || files.length == 0) return;

        InputStream sampleStream = plugin.getResource("pinatas/festive_llama.yml");
        if (sampleStream == null) return;

        FileConfiguration templateConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(sampleStream, StandardCharsets.UTF_8));

        for (File file : files) {
            try {
                FileConfiguration profileConfig = YamlConfiguration.loadConfiguration(file);
                int added = 0;

                // Essential Profile Keys to guarantee
                for (String key : templateConfig.getKeys(false)) {
                    if (key.equalsIgnoreCase("drops") || key.equalsIgnoreCase("display-name") || key.equalsIgnoreCase("id")) {
                        continue; // Keep profile custom identity and custom loot
                    }
                    if (!profileConfig.isSet(key)) {
                        profileConfig.set(key, templateConfig.get(key));
                        added++;
                    }
                }

                if (added > 0) {
                    profileConfig.save(file);
                    plugin.getLogger().info("[ConfigUpdater] ✔ Profile 'pinatas/" + file.getName() + "' updated with " + added + " new default parameter(s).");
                }
            } catch (Exception e) {
                plugin.getLogger().warning("[ConfigUpdater] Error checking profile " + file.getName() + ": " + e.getMessage());
            }
        }
    }

    private int mergeSections(ConfigurationSection source, ConfigurationSection target) {
        int added = 0;
        for (String key : source.getKeys(false)) {
            if (source.isConfigurationSection(key)) {
                ConfigurationSection subSource = source.getConfigurationSection(key);
                ConfigurationSection subTarget = target.getConfigurationSection(key);
                if (subTarget == null) {
                    subTarget = target.createSection(key);
                    added++;
                }
                added += mergeSections(subSource, subTarget);
            } else {
                if (!target.isSet(key)) {
                    target.set(key, source.get(key));
                    added++;
                }
            }
        }
        return added;
    }
}
