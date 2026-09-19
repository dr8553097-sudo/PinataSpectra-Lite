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

    public MessageManager(PinataPartyLite plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    public void loadMessages() {
        if (plugin.getConfigUpdaterEngine() != null) {
            plugin.getConfigUpdaterEngine().updateFile("messages.yml");
        }

        this.messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        this.messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        InputStream defStream = plugin.getResource("messages.yml");
        if (defStream != null) {
            defaultJarConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defStream, StandardCharsets.UTF_8));
            messagesConfig.setDefaults(defaultJarConfig);
            messagesConfig.options().copyDefaults(true);
        }
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
