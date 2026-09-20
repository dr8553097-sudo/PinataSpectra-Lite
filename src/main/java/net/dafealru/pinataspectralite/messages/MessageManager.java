package net.dafealru.pinataspectralite.messages;

import net.dafealru.pinataspectralite.PinataPartyLite;
import net.dafealru.pinataspectralite.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sovereign Universal Multi-Language Localization Engine for PinataSpectra.
 * Features per-player auto-detection, SQLite persistence, dynamic hot-reloading,
 * zero-null fallback cascade, tag parsing ([title], [actionbar], [sound]), and MiniMessage + HEX.
 */
public class MessageManager {

    public static class LocaleInfo {
        private final String code;
        private final String name;
        private final String region;
        private final String flag;
        private final File file;
        private final FileConfiguration config;

        public LocaleInfo(String code, String name, String region, String flag, File file, FileConfiguration config) {
            this.code = code;
            this.name = name;
            this.region = region;
            this.flag = flag;
            this.file = file;
            this.config = config;
        }

        public String getCode() { return code; }
        public String getName() { return name; }
        public String getRegion() { return region; }
        public String getFlag() { return flag; }
        public File getFile() { return file; }
        public FileConfiguration getConfig() { return config; }
    }

    public static final String DEFAULT_LOCALE = "en_US";
    public static final List<String> BUNDLED_LOCALES = List.of(
            "en_US", "es_ES", "fr_FR", "de_DE", "pt_BR", "ru_RU", "zh_CN", "it_IT", "ja_JP"
    );

    private final PinataPartyLite plugin;
    private final Map<String, LocaleInfo> loadedLocales = new ConcurrentHashMap<>();
    private YamlConfiguration masterJarConfig;
    private String serverDefaultLanguage = DEFAULT_LOCALE;

    public MessageManager(PinataPartyLite plugin) {
        this.plugin = plugin;
        loadAllLocales();
    }

    public synchronized void loadAllLocales() {
        loadedLocales.clear();

        // 1. Load embedded master template for fallback
        InputStream enStream = plugin.getResource("locales/en_US.yml");
        if (enStream == null) {
            enStream = plugin.getResource("messages.yml");
        }
        if (enStream != null) {
            masterJarConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(enStream, StandardCharsets.UTF_8));
        }

        // 2. Ensure locales folder exists and extract defaults
        File localesFolder = new File(plugin.getDataFolder(), "locales");
        if (!localesFolder.exists()) {
            localesFolder.mkdirs();
        }

        for (String locale : BUNDLED_LOCALES) {
            File locFile = new File(localesFolder, locale + ".yml");
            if (!locFile.exists()) {
                try {
                    plugin.saveResource("locales/" + locale + ".yml", false);
                } catch (Exception ignored) {
                    // If not found in JAR directly, check root fallback
                    if (locale.equals("en_US")) {
                        try { plugin.saveResource("messages.yml", false); } catch (Exception ignored2) {}
                    }
                }
            }
        }

        // 3. Discover and load all .yml files in locales/
        File[] files = localesFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                try {
                    String code = file.getName().replace(".yml", "");
                    YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

                    // Set fallback to master jar config
                    if (masterJarConfig != null) {
                        cfg.setDefaults(masterJarConfig);
                    }

                    String name = cfg.getString("meta.language-name", code);
                    String region = cfg.getString("meta.language-region", "");
                    String flag = cfg.getString("meta.flag-symbol", "🌐");

                    LocaleInfo info = new LocaleInfo(code, name, region, flag, file, cfg);
                    loadedLocales.put(code.toLowerCase(), info);
                    loadedLocales.put(code, info);
                } catch (Exception e) {
                    plugin.getLogger().warning("[Localization] Failed to load locale '" + file.getName() + "': " + e.getMessage());
                }
            }
        }

        this.serverDefaultLanguage = plugin.getConfig().getString("settings.language", DEFAULT_LOCALE).trim();
        plugin.getLogger().info("[Localization] Engine active with " + (loadedLocales.size() / 2) + " loaded language pack(s). Default: " + serverDefaultLanguage);
    }

    public String resolvePlayerLocaleCode(Player player) {
        if (player == null) return serverDefaultLanguage;

        // 1. Check player explicit database preference
        if (plugin.getDatabaseManager() != null) {
            String saved = plugin.getDatabaseManager().getPlayerLanguage(player.getUniqueId());
            if (saved != null && isLocaleLoaded(saved)) {
                return saved;
            }
        }

        // 2. Check client Minecraft auto-detected locale
        if (plugin.getConfig().getBoolean("settings.auto-detect-client-locale", true)) {
            String clientLocale = player.getLocale(); // e.g. "es_es", "en_us", "fr_fr"
            if (clientLocale != null && !clientLocale.isBlank()) {
                clientLocale = clientLocale.replace("-", "_");
                if (isLocaleLoaded(clientLocale)) {
                    return getCanonicalLocaleCode(clientLocale);
                }
                // Try matching language prefix (e.g. "es_mx" -> "es_ES", "en_gb" -> "en_US")
                String prefix = clientLocale.split("_")[0].toLowerCase();
                for (String code : loadedLocales.keySet()) {
                    if (code.toLowerCase().startsWith(prefix + "_") || code.equalsIgnoreCase(prefix)) {
                        return getCanonicalLocaleCode(code);
                    }
                }
            }
        }

        // 3. Fallback to server default
        return isLocaleLoaded(serverDefaultLanguage) ? getCanonicalLocaleCode(serverDefaultLanguage) : DEFAULT_LOCALE;
    }

    public boolean isLocaleLoaded(String code) {
        if (code == null) return false;
        return loadedLocales.containsKey(code.toLowerCase()) || loadedLocales.containsKey(code);
    }

    public String getCanonicalLocaleCode(String code) {
        if (code == null) return DEFAULT_LOCALE;
        LocaleInfo info = loadedLocales.get(code.toLowerCase());
        return (info != null) ? info.getCode() : DEFAULT_LOCALE;
    }

    public LocaleInfo getLocaleInfo(String code) {
        if (code == null) return null;
        return loadedLocales.get(code.toLowerCase());
    }

    public Collection<LocaleInfo> getAllLocales() {
        Map<String, LocaleInfo> unique = new LinkedHashMap<>();
        for (LocaleInfo info : loadedLocales.values()) {
            unique.put(info.getCode(), info);
        }
        return unique.values();
    }

    public void setPlayerLanguage(Player player, String localeCode) {
        if (player == null || localeCode == null) return;
        String canonical = getCanonicalLocaleCode(localeCode);
        if (plugin.getDatabaseManager() != null) {
            plugin.getDatabaseManager().setPlayerLanguage(player.getUniqueId(), canonical);
        }
        send(player, "general.language-changed", "lang", canonical);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
    }

    public void setServerLanguage(String localeCode) {
        if (localeCode == null) return;
        String canonical = getCanonicalLocaleCode(localeCode);
        this.serverDefaultLanguage = canonical;
        plugin.getConfig().set("settings.language", canonical);
        plugin.saveConfig();
    }

    public String getServerLanguage() {
        return serverDefaultLanguage;
    }

    // --- Message Retrieval & Parsing ---

    public String getRaw(Player player, String path, String def) {
        String localeCode = resolvePlayerLocaleCode(player);
        LocaleInfo info = loadedLocales.get(localeCode.toLowerCase());

        if (info != null && info.getConfig().contains(path)) {
            return info.getConfig().getString(path, def);
        }

        // Fallback 1: Server default locale
        LocaleInfo srvInfo = loadedLocales.get(serverDefaultLanguage.toLowerCase());
        if (srvInfo != null && srvInfo.getConfig().contains(path)) {
            return srvInfo.getConfig().getString(path, def);
        }

        // Fallback 2: en_US
        LocaleInfo enInfo = loadedLocales.get("en_us");
        if (enInfo != null && enInfo.getConfig().contains(path)) {
            return enInfo.getConfig().getString(path, def);
        }

        // Fallback 3: Master JAR stream
        if (masterJarConfig != null && masterJarConfig.contains(path)) {
            return masterJarConfig.getString(path, def);
        }

        return def;
    }

    public String getRaw(String path) {
        return getRaw((Player) null, path, "");
    }

    public String getRaw(String path, String def) {
        return getRaw((Player) null, path, def);
    }

    public String getMessage(Player player, String path, String... placeholders) {
        String raw = getRaw(player, path, "");
        if (raw.isEmpty()) return "";

        String prefix = getRaw(player, "prefix", "<#8B5CF6>[<gradient:#EC4899:#FCD34D><bold>PinataSpectra</bold></gradient><#8B5CF6>] ");
        raw = raw.replace("%prefix%", prefix);

        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                raw = raw.replace("%" + placeholders[i] + "%", placeholders[i + 1]);
                raw = raw.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
            }
        }
        return ColorUtils.colorize(raw);
    }

    public Component getComponent(Player player, String path, String... placeholders) {
        String raw = getRaw(player, path, "");
        if (raw.isEmpty()) return Component.empty();

        String prefix = getRaw(player, "prefix", "<#8B5CF6>[<gradient:#EC4899:#FCD34D><bold>PinataSpectra</bold></gradient><#8B5CF6>] ");
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
        Player player = (sender instanceof Player p) ? p : null;

        String raw = getRaw(player, path, "");
        if (raw == null || raw.isEmpty()) return;

        // Parse optional sound tag: [sound: SOUND_NAME, volume, pitch]
        if (player != null && raw.contains("[sound:")) {
            parseAndPlaySound(player, raw);
            raw = raw.replaceAll("\\[sound:[^\\]]+\\]", "").trim();
        }

        // Parse optional actionbar tag: [actionbar]
        if (player != null && raw.startsWith("[actionbar]")) {
            String act = raw.substring("[actionbar]".length()).trim();
            player.sendActionBar(ColorUtils.colorizeComponent(formatPlaceholders(player, act, placeholders)));
            return;
        }

        Component comp = getComponent(player, path, placeholders);
        if (!comp.equals(Component.empty())) {
            sender.sendMessage(comp);
        }
    }

    public void broadcast(String path, String... placeholders) {
        // Broadcast personalized message to every player in their own language!
        for (Player p : Bukkit.getOnlinePlayers()) {
            send(p, path, placeholders);
        }
        // Also send to console in server default language
        String consoleMsg = getMessage(null, path, placeholders);
        if (!consoleMsg.isEmpty()) {
            Bukkit.getConsoleSender().sendMessage(ColorUtils.colorizeComponent(consoleMsg));
        }
    }

    public void sendActionBar(Player player, String path, String... placeholders) {
        if (player == null) return;
        Component comp = getComponent(player, path, placeholders);
        if (!comp.equals(Component.empty())) {
            player.sendActionBar(comp);
        }
    }

    private String formatPlaceholders(Player player, String text, String... placeholders) {
        String prefix = getRaw(player, "prefix", "<#8B5CF6>[<gradient:#EC4899:#FCD34D><bold>PinataSpectra</bold></gradient><#8B5CF6>] ");
        text = text.replace("%prefix%", prefix);
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                text = text.replace("%" + placeholders[i] + "%", placeholders[i + 1]);
                text = text.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
            }
        }
        return text;
    }

    private void parseAndPlaySound(Player player, String text) {
        try {
            int start = text.indexOf("[sound:");
            int end = text.indexOf("]", start);
            if (start != -1 && end != -1) {
                String sub = text.substring(start + 7, end).trim();
                String[] parts = sub.split(",");
                Sound s = Sound.valueOf(parts[0].trim().toUpperCase());
                float vol = parts.length > 1 ? Float.parseFloat(parts[1].trim()) : 1.0f;
                float pitch = parts.length > 2 ? Float.parseFloat(parts[2].trim()) : 1.0f;
                player.playSound(player.getLocation(), s, vol, pitch);
            }
        } catch (Exception ignored) {}
    }
}
