package net.dafealru.pinataspectralite.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ColorUtils {

    private static final Pattern HEX_AMPERSAND_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern SECTION_HEX_PATTERN = Pattern.compile("(?i)[&§]x([&§][0-9a-f]){6}");
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer SECTION_SERIALIZER = LegacyComponentSerializer.legacySection();
    private static final LegacyComponentSerializer AMPERSAND_SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();
    private static final PlainTextComponentSerializer PLAIN_SERIALIZER = PlainTextComponentSerializer.plainText();

    private ColorUtils() {}

    /**
     * Converts any string with MiniMessage, Hex (&#RRGGBB or <#RRGGBB>), or Legacy (&a, §a) into an Adventure Component.
     */
    public static Component colorizeComponent(String message) {
        if (message == null || message.isEmpty()) return Component.empty();

        // 1. Pre-process to normalize legacy hex and legacy formatting
        String prepared = normalizeToMiniMessage(message);

        // 2. Parse using MiniMessage
        try {
            return MINI_MESSAGE.deserialize(prepared);
        } catch (Exception e) {
            // Safe fallback to Adventure Legacy Component Serializer with Hex support
            return AMPERSAND_SERIALIZER.deserialize(message.replace('§', '&'));
        }
    }

    /**
     * Serializes to a legacy § string for Bukkit/Spigot APIs where Components cannot be used directly (e.g. item lore, titles).
     */
    public static String colorize(String message) {
        if (message == null || message.isEmpty()) return "";
        Component comp = colorizeComponent(message);
        return SECTION_SERIALIZER.serialize(comp);
    }

    public static String stripColor(String message) {
        if (message == null || message.isEmpty()) return "";
        Component comp = colorizeComponent(message);
        return PLAIN_SERIALIZER.serialize(comp);
    }

    public static String toMiniMessage(String message) {
        return normalizeToMiniMessage(message);
    }

    public static String normalizeToMiniMessage(String input) {
        if (input == null || input.isEmpty()) return "";

        String res = input;

        // Fix potential typos like &#f or &#a to &f or &a
        res = res.replaceAll("&#([0-9a-fk-orA-FK-OR])(?![0-9a-fA-F]{4})", "&$1");

        // Convert &#RRGGBB to <#RRGGBB>
        Matcher hexMatcher = HEX_AMPERSAND_PATTERN.matcher(res);
        res = hexMatcher.replaceAll("<#$1>");

        // Convert &x&r&r&g&g&b&b or §x§r§r§g§g§b§b to <#RRGGBB>
        res = convertLegacySectionHex(res);

        // Replace § with & for legacy codes
        res = res.replace('§', '&');

        // Convert legacy color codes &0-&f, &l, etc.
        res = convertLegacyCodes(res);

        return res;
    }

    private static String convertLegacySectionHex(String input) {
        Matcher m = SECTION_HEX_PATTERN.matcher(input);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String hex = m.group().replaceAll("[&§]x?", "");
            m.appendReplacement(sb, "<#" + hex + ">");
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public static String convertLegacyToMiniMessage(String input) {
        return convertLegacyCodes(input);
    }

    private static String convertLegacyCodes(String input) {
        if (!input.contains("&")) return input;

        return input
                .replaceAll("(?i)&0", "<black>")
                .replaceAll("(?i)&1", "<dark_blue>")
                .replaceAll("(?i)&2", "<dark_green>")
                .replaceAll("(?i)&3", "<dark_aqua>")
                .replaceAll("(?i)&4", "<dark_red>")
                .replaceAll("(?i)&5", "<dark_purple>")
                .replaceAll("(?i)&6", "<gold>")
                .replaceAll("(?i)&7", "<gray>")
                .replaceAll("(?i)&8", "<dark_gray>")
                .replaceAll("(?i)&9", "<blue>")
                .replaceAll("(?i)&a", "<green>")
                .replaceAll("(?i)&b", "<aqua>")
                .replaceAll("(?i)&c", "<red>")
                .replaceAll("(?i)&d", "<light_purple>")
                .replaceAll("(?i)&e", "<yellow>")
                .replaceAll("(?i)&f", "<white>")
                .replaceAll("(?i)&l", "<bold>")
                .replaceAll("(?i)&m", "<strikethrough>")
                .replaceAll("(?i)&n", "<underlined>")
                .replaceAll("(?i)&o", "<italic>")
                .replaceAll("(?i)&r", "<reset>");
    }
}
