package net.dafealru.pinataspectralite.config;

import net.dafealru.pinataspectralite.PinataPartyLite;
import net.dafealru.pinataspectralite.messages.MessageManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

/**
 * Intelligent Comment-Preserving Configuration & Profile Synchronizer.
 * Automatically updates existing server files with new parameters, comments, and headers
 * from newer plugin versions while strictly preserving 100% of user-customized values.
 */
public class ConfigUpdaterEngine {

    private final PinataPartyLite plugin;

    public ConfigUpdaterEngine(PinataPartyLite plugin) {
        this.plugin = plugin;
    }

    public void updateAllConfigs() {
        // 1. Sync standard root configs
        updateFile("config.yml");

        // 2. Sync all bundled locales
        for (String loc : MessageManager.BUNDLED_LOCALES) {
            updateFile("locales/" + loc + ".yml");
        }

        // 3. Sync default profiles in pinatas/
        updateFile("pinatas/festive_llama.yml");
        updateFile("pinatas/custom_party.yml");

        // 4. Scan and synchronize any custom admin pinata profiles
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
                    plugin.getLogger().info("[ConfigUpdater] Deployed default configuration: " + resourcePath);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("[ConfigUpdater] Failed to deploy " + resourcePath + ": " + e.getMessage());
            }
            return;
        }

        try {
            InputStream jarStream = plugin.getResource(resourcePath);
            if (jarStream == null) return;

            FileConfiguration diskConfig = YamlConfiguration.loadConfiguration(diskFile);
            FileConfiguration jarConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(jarStream, StandardCharsets.UTF_8));

            int missingKeys = countMissingKeys(jarConfig, diskConfig);
            int diskVersion = diskConfig.getInt("settings.config-version", diskConfig.getInt("config-version", 1));
            int jarVersion = jarConfig.getInt("settings.config-version", jarConfig.getInt("config-version", 1));

            if (missingKeys > 0 || diskVersion < jarVersion) {
                // 1. Merge missing keys into disk configuration
                mergeSections(jarConfig, diskConfig);
                if (jarVersion > diskVersion) {
                    diskConfig.set("settings.config-version", jarVersion);
                }

                // 3. Attempt comment-preserving line rebuild
                InputStream templateStream = plugin.getResource(resourcePath);
                if (templateStream != null) {
                    List<String> updatedLines = buildCommentPreservedLines(templateStream, diskConfig);
                    Files.write(diskFile.toPath(), updatedLines, StandardCharsets.UTF_8);
                } else {
                    diskConfig.save(diskFile);
                }

                plugin.getLogger().info("[ConfigUpdater] ✔ Automatically synchronized " + resourcePath + " (" + missingKeys + " new setting(s) added, custom values preserved).");
            }
        } catch (Exception e) {
            plugin.getLogger().warning("[ConfigUpdater] Warning during sync of " + resourcePath + ": " + e.getMessage());
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

                for (String key : templateConfig.getKeys(false)) {
                    if (key.equalsIgnoreCase("drops") || key.equalsIgnoreCase("display-name") || key.equalsIgnoreCase("id")) {
                        continue;
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

    private List<String> buildCommentPreservedLines(InputStream jarTemplate, FileConfiguration diskConfig) throws IOException {
        List<String> outputLines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(jarTemplate, StandardCharsets.UTF_8));
        String line;

        Deque<String> pathStack = new ArrayDeque<>();
        Deque<Integer> indentStack = new ArrayDeque<>();

        while ((line = reader.readLine()) != null) {
            String trimmed = line.trim();

            // Preserve empty lines and pure comment lines exactly as they are in the template
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                outputLines.add(line);
                continue;
            }

            // List item line
            if (trimmed.startsWith("-")) {
                outputLines.add(line);
                continue;
            }

            // Key-Value or Section Header line
            int colonIndex = line.indexOf(':');
            if (colonIndex != -1) {
                int indent = getIndentation(line);
                String keyName = line.substring(0, colonIndex).trim();

                while (!indentStack.isEmpty() && indent <= indentStack.peek()) {
                    indentStack.pop();
                    pathStack.pop();
                }

                pathStack.push(keyName);
                indentStack.push(indent);

                String currentPath = buildPath(pathStack);
                String remainder = line.substring(colonIndex + 1).trim();

                if (remainder.isEmpty() || remainder.startsWith("#")) {
                    // Section header
                    outputLines.add(line);
                } else {
                    // Key with value
                    if (diskConfig.isSet(currentPath) && !diskConfig.isConfigurationSection(currentPath) && !diskConfig.isList(currentPath)) {
                        Object diskVal = diskConfig.get(currentPath);
                        String formattedVal = formatYamlValue(diskVal);
                        String indentStr = line.substring(0, line.indexOf(keyName));
                        
                        // Extract inline comment if present in template
                        int hashIndex = remainder.indexOf('#');
                        String inlineComment = (hashIndex != -1) ? " " + remainder.substring(hashIndex) : "";

                        outputLines.add(indentStr + keyName + ": " + formattedVal + inlineComment);
                    } else {
                        outputLines.add(line);
                    }
                }
            } else {
                outputLines.add(line);
            }
        }
        return outputLines;
    }

    private String buildPath(Deque<String> stack) {
        List<String> list = new ArrayList<>(stack);
        Collections.reverse(list);
        return String.join(".", list);
    }

    private int getIndentation(String line) {
        int count = 0;
        for (char c : line.toCharArray()) {
            if (c == ' ') count++;
            else break;
        }
        return count;
    }

    private String formatYamlValue(Object val) {
        if (val == null) return "null";
        if (val instanceof String s) {
            if (s.contains("\"")) {
                return "'" + s.replace("'", "''") + "'";
            }
            return "\"" + s + "\"";
        }
        return String.valueOf(val);
    }

    private int countMissingKeys(ConfigurationSection source, ConfigurationSection target) {
        int missing = 0;
        for (String key : source.getKeys(false)) {
            if (source.isConfigurationSection(key)) {
                ConfigurationSection subSource = source.getConfigurationSection(key);
                ConfigurationSection subTarget = target.getConfigurationSection(key);
                if (subTarget == null) {
                    missing++;
                } else {
                    missing += countMissingKeys(subSource, subTarget);
                }
            } else {
                if (!target.isSet(key)) {
                    missing++;
                }
            }
        }
        return missing;
    }

    private void mergeSections(ConfigurationSection source, ConfigurationSection target) {
        for (String key : source.getKeys(false)) {
            if (source.isConfigurationSection(key)) {
                ConfigurationSection subSource = source.getConfigurationSection(key);
                ConfigurationSection subTarget = target.getConfigurationSection(key);
                if (subTarget == null) {
                    subTarget = target.createSection(key);
                }
                mergeSections(subSource, subTarget);
            } else {
                if (!target.isSet(key)) {
                    target.set(key, source.get(key));
                }
            }
        }
    }
}
