package net.dafealru.pinataspectralite.database;

import net.dafealru.pinataspectralite.PinataPartyLite;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseManager {

    private final PinataPartyLite plugin;
    private Connection connection;
    private final File dbFile;
    private final Map<UUID, String> playerLanguageCache = new ConcurrentHashMap<>();

    public DatabaseManager(PinataPartyLite plugin) {
        this.plugin = plugin;
        this.dbFile = new File(plugin.getDataFolder(), "pinata_stats.db");
    }

    public void initialize() {
        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            try (PreparedStatement ps = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS pinata_stats (" +
                            "uuid VARCHAR(36) PRIMARY KEY, " +
                            "username VARCHAR(16), " +
                            "total_hits INT DEFAULT 0, " +
                            "events_won INT DEFAULT 0" +
                            ");"
            )) {
                ps.executeUpdate();
            }

            try (PreparedStatement ps = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS player_locales (" +
                            "uuid VARCHAR(36) PRIMARY KEY, " +
                            "locale VARCHAR(16) NOT NULL" +
                            ");"
            )) {
                ps.executeUpdate();
            }

            // Load cached player locales
            try (PreparedStatement ps = connection.prepareStatement("SELECT uuid, locale FROM player_locales;");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        UUID uuid = UUID.fromString(rs.getString("uuid"));
                        String loc = rs.getString("locale");
                        if (loc != null) playerLanguageCache.put(uuid, loc);
                    } catch (Exception ignored) {}
                }
            }

            plugin.getLogger().info("[Database] SQLite initialized with player language persistence at pinata_stats.db");
        } catch (Exception e) {
            plugin.getLogger().severe("[Database] Failed to initialize SQLite: " + e.getMessage());
        }
    }

    public String getPlayerLanguage(UUID uuid) {
        if (uuid == null) return null;
        return playerLanguageCache.get(uuid);
    }

    public void setPlayerLanguage(UUID uuid, String locale) {
        if (uuid == null || locale == null) return;
        playerLanguageCache.put(uuid, locale);

        if (connection == null) return;
        CompletableFuture.runAsync(() -> {
            String query = "INSERT INTO player_locales (uuid, locale) VALUES (?, ?) " +
                    "ON CONFLICT(uuid) DO UPDATE SET locale = excluded.locale;";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, uuid.toString());
                ps.setString(2, locale);
                ps.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().warning("[Database] Error updating player locale: " + e.getMessage());
            }
        });
    }

    public void incrementPlayerStats(UUID uuid, String name, int hits, boolean won) {
        if (connection == null || uuid == null) return;
        CompletableFuture.runAsync(() -> {
            String query = "INSERT INTO pinata_stats (uuid, username, total_hits, events_won) VALUES (?, ?, ?, ?) " +
                    "ON CONFLICT(uuid) DO UPDATE SET " +
                    "username = excluded.username, " +
                    "total_hits = total_hits + excluded.total_hits, " +
                    "events_won = events_won + excluded.events_won;";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, uuid.toString());
                ps.setString(2, name != null ? name : "Player");
                ps.setInt(3, hits);
                ps.setInt(4, won ? 1 : 0);
                ps.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().warning("[Database] Error updating player stats: " + e.getMessage());
            }
        });
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignored) {}
        }
    }
}
