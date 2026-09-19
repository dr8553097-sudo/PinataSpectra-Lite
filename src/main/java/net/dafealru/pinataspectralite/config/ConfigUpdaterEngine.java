package net.dafealru.pinataspectralite.config;

import net.dafealru.pinataspectralite.PinataPartyLite;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ConfigUpdaterEngine {

    private final PinataPartyLite plugin;

    public ConfigUpdaterEngine(PinataPartyLite plugin) {
        this.plugin = plugin;
    }

    public void updateAllConfigs() {
        updateFile("config.yml");
        updateFile("messages.yml");
        updateFile("pinatas/festive_llama.yml");
        updateFile("pinatas/custom_party.yml");
    }

    public void updateFile(String resourcePath) {
        File diskFile = new File(plugin.getDataFolder(), resourcePath);
        if (!diskFile.exists()) {
            try {
                if (diskFile.getParentFile() != null && !diskFile.getParentFile().exists()) {
                    diskFile.getParentFile().mkdirs();
                }
                plugin.saveResource(resourcePath, false);
            } catch (Exception ignored) {}
            return;
        }

        try {
            FileConfiguration diskConfig;
            try (InputStream in = new java.io.FileInputStream(diskFile)) {
                diskConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
            }

            InputStream jarStream = plugin.getResource(resourcePath);
            if (jarStream == null) return;

            FileConfiguration jarConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(jarStream, StandardCharsets.UTF_8));

            int addedKeys = mergeSections(jarConfig, diskConfig);
            if (addedKeys > 0) {
                diskConfig.save(diskFile);
                plugin.getLogger().info("[ConfigUpdater] Sincronizadas " + addedKeys + " nuevas opciones en " + resourcePath + " sin alterar tus configuraciones.");
            }
        } catch (Exception e) {
            plugin.getLogger().warning("[ConfigUpdater] Error al actualizar " + resourcePath + ": " + e.getMessage());
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
                if (!target.contains(key, true)) {
                    target.set(key, source.get(key));
                    added++;
                }
            }
        }
        return added;
    }
}
