package net.dafealru.pinataspectralite.discord;

import net.dafealru.pinataspectralite.PinataPartyLite;
import org.bukkit.Bukkit;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class DiscordWebhookService {

    private final PinataPartyLite plugin;

    public DiscordWebhookService(PinataPartyLite plugin) {
        this.plugin = plugin;
    }

    public void sendSpawnNotification(String pinataName) {
        if (!isEnabled()) return;
        String title = "🪅 Piñata Event Started!";
        String description = "A **" + pinataName + "** has spawned in the arena! Join now and strike it for rewards!";
        int color = 0xEC4899; // Pink
        sendEmbedAsync(title, description, color);
    }

    public void sendVictoryNotification(String pinataName, String mvpName, int mvpHits) {
        if (!isEnabled()) return;
        String title = "🎉 Piñata Conquered!";
        String description = "**" + pinataName + "** was defeated!\n👑 **Top MVP:** " + mvpName + " (" + mvpHits + " hits)\n\n*⚡ Powered by PinataSpectra Lite*";
        int color = 0xFCD34D; // Gold
        sendEmbedAsync(title, description, color);
    }

    private boolean isEnabled() {
        return plugin.getConfig().getBoolean("discord.enabled", false) &&
                !plugin.getConfig().getString("discord.webhook-url", "").trim().isEmpty();
    }

    private void sendEmbedAsync(String title, String description, int color) {
        String webhookUrl = plugin.getConfig().getString("discord.webhook-url", "").trim();
        String username = plugin.getConfig().getString("discord.username", "PinataSpectra Lite");
        String avatarUrl = plugin.getConfig().getString("discord.avatar-url", "");

        CompletableFuture.runAsync(() -> {
            try {
                URL url = URI.create(webhookUrl).toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("User-Agent", "PinataSpectra-Lite");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                String json = "{"
                        + "\"username\":\"" + escapeJson(username) + "\","
                        + (avatarUrl.isEmpty() ? "" : "\"avatar_url\":\"" + escapeJson(avatarUrl) + "\",")
                        + "\"embeds\":[{"
                        + "\"title\":\"" + escapeJson(title) + "\","
                        + "\"description\":\"" + escapeJson(description) + "\","
                        + "\"color\":" + color + ","
                        + "\"footer\":{\"text\":\"PinataSpectra Lite • Unlock God-Tier Supernovas in PRO\"}"
                        + "}]"
                        + "}";

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = json.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int code = conn.getResponseCode();
                if (code < 200 || code >= 300) {
                    plugin.getLogger().warning("[Discord] Webhook returned HTTP code " + code);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("[Discord] Failed to send webhook: " + e.getMessage());
            }
        });
    }

    private String escapeJson(String raw) {
        if (raw == null) return "";
        return raw.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
