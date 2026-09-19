package net.dafealru.pinataspectralite.messages;

import net.dafealru.pinataspectralite.PinataPartyLite;
import net.dafealru.pinataspectralite.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MessageManager {

    private final PinataPartyLite plugin;
    private FileConfiguration messagesConfig;
    private File messagesFile;
    private YamlConfiguration defaultJarConfig;
    private YamlConfiguration fallbackJarConfig;
    private String currentLanguage = "EN";

    public MessageManager(PinataPartyLite plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    public void loadMessages() {
        this.currentLanguage = plugin.getConfig().getString("settings.language", "EN").trim().toUpperCase();
        String fileName = currentLanguage.equals("ES") ? "messages_es.yml" : "messages.yml";

        if (plugin.getConfigUpdaterEngine() != null) {
            plugin.getConfigUpdaterEngine().updateFile(fileName);
        }

        this.messagesFile = new File(plugin.getDataFolder(), fileName);
        if (!messagesFile.exists()) {
            plugin.saveResource(fileName, false);
        }

        this.messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        InputStream defStream = plugin.getResource(fileName);
        if (defStream != null) {
            defaultJarConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defStream, StandardCharsets.UTF_8));
            messagesConfig.setDefaults(defaultJarConfig);
            messagesConfig.options().copyDefaults(true);
        }

        InputStream enStream = plugin.getResource("messages.yml");
        if (enStream != null) {
            fallbackJarConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(enStream, StandardCharsets.UTF_8));
        }
    }

    public void setLanguage(String lang) {
        if (lang == null) lang = "EN";
        String normalized = lang.trim().equalsIgnoreCase("ES") ? "ES" : "EN";
        this.currentLanguage = normalized;
        plugin.getConfig().set("settings.language", normalized);
        plugin.saveConfig();
        loadMessages();
    }

    public String getLanguage() {
        return currentLanguage;
    }

    public String getRaw(String path) {
        return getRaw(path, "");
    }

    public String getRaw(String path, String def) {
        if (messagesConfig != null && messagesConfig.contains(path)) {
            return messagesConfig.getString(path, def);
        }
        if (defaultJarConfig != null && defaultJarConfig.contains(path)) {
            return defaultJarConfig.getString(path, def);
        }
        if (fallbackJarConfig != null && fallbackJarConfig.contains(path)) {
            return fallbackJarConfig.getString(path, def);
        }
        return def;
    }

    public String getMessage(String path, String... placeholders) {
        String raw = getRaw(path, "");
        if (raw.isEmpty()) {
            return "";
        }
        String prefix = getRaw("prefix", "<#8B5CF6>[<gradient:#EC4899:#FCD34D><bold>PinataSpectra</bold></gradient><#8B5CF6>] ");
        raw = raw.replace("%prefix%", prefix);

        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                raw = raw.replace("%" + placeholders[i] + "%", placeholders[i + 1]);
                raw = raw.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
            }
        }
        return ColorUtils.colorize(raw);
    }

    public Component getComponent(String path, String... placeholders) {
        String raw = getRaw(path, "");
        if (raw.isEmpty()) {
            return Component.empty();
        }
        String prefix = getRaw("prefix", "<#8B5CF6>[<gradient:#EC4899:#FCD34D><bold>PinataSpectra</bold></gradient><#8B5CF6>] ");
        raw = raw.replace("%prefix%", prefix);

        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                raw = raw.replace("%" + placeholders[i] + "%", placeholders[i + 1]);
                raw = raw.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
            }
        }
        return ColorUtils.colorizeComponent(raw);
    }

    public void send(CommandSender sender, String path, String... placeholders) {
        if (sender == null) return;
        Component comp = getComponent(path, placeholders);
        if (!comp.equals(Component.empty())) {
            sender.sendMessage(comp);
        }
    }

    public void broadcast(String path, String... placeholders) {
        Component comp = getComponent(path, placeholders);
        if (!comp.equals(Component.empty())) {
            Bukkit.broadcast(comp);
        }
    }

    public void sendActionBar(Player player, String path, String... placeholders) {
        if (player == null) return;
        Component comp = getComponent(path, placeholders);
        if (!comp.equals(Component.empty())) {
            player.sendActionBar(comp);
        }
    }
}
