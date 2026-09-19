package net.dafealru.pinataspectralite.pinata;

import net.dafealru.pinataspectralite.PinataPartyLite;
import net.dafealru.pinataspectralite.finale.DeathAnimationType;
import net.dafealru.pinataspectralite.loot.LootItem;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

public class PinataRegistry {

    public static final int LITE_MAX_PROFILES = 2;

    private final PinataPartyLite plugin;
    private final Map<String, PinataProfile> profiles = new HashMap<>();

    public PinataRegistry(PinataPartyLite plugin) {
        this.plugin = plugin;
    }

    public void loadProfiles() {
        profiles.clear();
        File folder = new File(plugin.getDataFolder(), "pinatas");
        if (!folder.exists()) {
            folder.mkdirs();
            saveDefaultProfile("festive_llama.yml");
            saveDefaultProfile("custom_party.yml");
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null || files.length == 0) {
            saveDefaultProfile("festive_llama.yml");
            files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        }

        int loadedCount = 0;
        if (files != null) {
            for (File file : files) {
                if (loadedCount >= LITE_MAX_PROFILES) {
                    plugin.getLogger().warning("[PinataSpectra-Lite] Limit reached (" + LITE_MAX_PROFILES + " profiles max in Free Edition). " +
                            "Skipped '" + file.getName() + "'. Upgrade to Sovereign PRO for unlimited Piñata profiles! (/pinata pro)");
                    continue;
                }
                try {
                    PinataProfile profile = loadProfileFromFile(file);
                    if (profile != null) {
                        profiles.put(profile.getId(), profile);
                        loadedCount++;
                    }
                } catch (Exception e) {
                    plugin.getLogger().severe("Error loading profile '" + file.getName() + "': " + e.getMessage());
                }
            }
        }
        plugin.getLogger().info("[Registry] Loaded " + profiles.size() + "/" + LITE_MAX_PROFILES + " Piñata Profile(s). (Lite Edition)");
    }

    private void saveDefaultProfile(String name) {
        File file = new File(plugin.getDataFolder(), "pinatas/" + name);
        if (!file.exists()) {
            try (InputStream in = plugin.getResource("pinatas/" + name)) {
                if (in != null) {
                    Files.copy(in, file.toPath());
                }
            } catch (Exception ignored) {}
        }
    }

    private PinataProfile loadProfileFromFile(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String id = config.getString("id", file.getName().replace(".yml", "")).toUpperCase();
        String displayName = config.getString("display-name", "&d✦ Festive Piñata ✦");
        int health = config.getInt("health", 120);

        Material primary = matchMaterial(config.getString("primary-block"), Material.MAGENTA_CONCRETE);
        Material secondary = matchMaterial(config.getString("secondary-block"), Material.CYAN_CONCRETE);

        List<String> ribStrings = config.getStringList("ribbon-blocks");
        List<Material> ribbons = new ArrayList<>();
        for (String rs : ribStrings) {
            ribbons.add(matchMaterial(rs, Material.PINK_WOOL));
        }
        if (ribbons.isEmpty()) ribbons.add(Material.PINK_WOOL);

        Color glowColor = parseHexColor(config.getString("glow-color", "#EC4899"));
        Particle aura = matchParticle(config.getString("aura-particle"), Particle.WAX_OFF);
        Particle hit = matchParticle(config.getString("hit-particle"), Particle.FIREWORK);
        Particle trail = matchParticle(config.getString("trail-particle"), Particle.CHERRY_LEAVES);

        Sound ambient = matchSound(config.getString("ambient-sound", config.getString("ambient_sound")), Sound.ENTITY_LLAMA_AMBIENT);
        Sound hitSound = matchSound(config.getString("hit-sound", config.getString("hit_sound")), Sound.BLOCK_WOOD_HIT);
        Sound deathSound = matchSound(config.getString("death-sound", config.getString("death_sound")), Sound.UI_TOAST_CHALLENGE_COMPLETE);

        double tpChance = config.getDouble("teleport-chance", 0.15);
        int tpInterval = config.getInt("teleport-interval-seconds", 15);
        double speed = config.getDouble("movement-speed", 1.0);

        PinataProfile profile = new PinataProfile(id, displayName, health, primary, secondary, ribbons, glowColor,
                aura, hit, trail, ambient, hitSound, deathSound, tpChance, tpInterval, speed);

        // Drops
        profile.setCustomHitDrops(loadDropsFromSection(config.getConfigurationSection("drops.hit-drops")));
        profile.setCustomFinaleDrops(loadDropsFromSection(config.getConfigurationSection("drops.finale-drops")));

        return profile;
    }

    private List<LootItem> loadDropsFromSection(ConfigurationSection sec) {
        List<LootItem> list = new ArrayList<>();
        if (sec == null) return list;

        for (String key : sec.getKeys(false)) {
            ConfigurationSection itemSec = sec.getConfigurationSection(key);
            if (itemSec == null) continue;

            Material mat = matchMaterial(itemSec.getString("material"), Material.COOKIE);
            int amount = itemSec.getInt("amount", 1);
            double chance = itemSec.getDouble("chance", 100.0);
            String name = itemSec.getString("name", "");
            List<String> lore = itemSec.getStringList("lore");
            double money = itemSec.getDouble("money", 0.0);
            List<String> commands = itemSec.getStringList("commands");

            list.add(new LootItem(mat, amount, chance, name, lore, money, commands));
        }
        return list;
    }

    private Material matchMaterial(String name, Material def) {
        if (name == null) return def;
        try { return Material.valueOf(name.toUpperCase()); } catch (Exception e) { return def; }
    }

    private Particle matchParticle(String name, Particle def) {
        if (name == null) return def;
        try { return Particle.valueOf(name.toUpperCase()); } catch (Exception e) { return def; }
    }

    private Sound matchSound(String name, Sound def) {
        if (name == null || name.isBlank()) return def;
        String clean = name.trim().toUpperCase().replace("-", "_").replace(".", "_");
        try {
            return Sound.valueOf(clean);
        } catch (Exception e) {
            for (Sound s : Sound.values()) {
                if (s.name().equalsIgnoreCase(clean)) return s;
            }
            return def;
        }
    }

    private Color parseHexColor(String hex) {
        if (hex == null || !hex.startsWith("#") || hex.length() != 7) return Color.fromRGB(236, 72, 153);
        try {
            int r = Integer.parseInt(hex.substring(1, 3), 16);
            int g = Integer.parseInt(hex.substring(3, 5), 16);
            int b = Integer.parseInt(hex.substring(5, 7), 16);
            return Color.fromRGB(r, g, b);
        } catch (Exception e) {
            return Color.fromRGB(236, 72, 153);
        }
    }

    public PinataProfile getProfile(String id) {
        if (id == null) return null;
        return profiles.get(id.toUpperCase());
    }

    public Collection<PinataProfile> getAllProfiles() {
        return profiles.values();
    }
}
