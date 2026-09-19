package net.dafealru.pinataspectralite.database;

import net.dafealru.pinataspectralite.PinataPartyLite;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DatabaseManager {

    private final PinataPartyLite plugin;
    private Connection connection;
    private final File dbFile;

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
            plugin.getLogger().info("[Database] SQLite initialized successfully at pinata_stats.db");
        } catch (Exception e) {
            plugin.getLogger().severe("[Database] Failed to initialize SQLite: " + e.getMessage());
        }
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
