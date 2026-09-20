package net.dafealru.pinataspectralite.util;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

/**
 * Cross-version safe sound player for Minecraft 1.20.1 through 26.x.
 */
public final class SoundAdapter {

    private SoundAdapter() {}

    public static final Sound WIND_BURST = resolveSound("ENTITY_WIND_CHARGE_WIND_BURST", "ENTITY_PHANTOM_FLAP", "ENTITY_FIREWORK_ROCKET_LAUNCH");

    public static Sound resolveSound(String... candidateNames) {
        for (String name : candidateNames) {
            try {
                return Sound.valueOf(name.toUpperCase());
            } catch (Exception | NoSuchFieldError ignored) {}
        }
        return null;
    }

    public static void play(Player player, Location loc, Sound sound, float volume, float pitch) {
        if (player == null || loc == null || sound == null) return;
        try {
            player.playSound(loc, sound, volume, pitch);
        } catch (Exception ignored) {}
    }

    public static void play(Player player, Location loc, String soundName, float volume, float pitch, Sound fallback) {
        if (player == null || loc == null) return;
        Sound s = resolveSound(soundName);
        if (s == null) s = fallback;
        if (s != null) {
            try {
                player.playSound(loc, s, volume, pitch);
            } catch (Exception ignored) {}
        }
    }
}
