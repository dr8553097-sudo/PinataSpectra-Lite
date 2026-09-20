package net.dafealru.pinataspectralite.util;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

/**
 * Cross-version safe particle dispatcher.
 * Seamlessly adapts particle enum renames between Minecraft 1.20.1, 1.20.4, 1.20.5+, 1.21+, and 26.x.
 */
public final class ParticleAdapter {

    private ParticleAdapter() {}

    public static final Particle FIREWORK = resolveParticle("FIREWORK", "FIREWORKS_SPARK", "CRIT");
    public static final Particle EXPLOSION_EMITTER = resolveParticle("EXPLOSION_EMITTER", "EXPLOSION_HUGE", "EXPLOSION_LARGE");
    public static final Particle EXPLOSION = resolveParticle("EXPLOSION", "EXPLOSION_NORMAL", "POOF");
    public static final Particle BLOCK = resolveParticle("BLOCK", "BLOCK_CRACK", "BLOCK_DUST");
    public static final Particle ITEM = resolveParticle("ITEM", "ITEM_CRACK");
    public static final Particle DUST = resolveParticle("DUST", "REDSTONE");
    public static final Particle POOF = resolveParticle("POOF", "SMOKE_NORMAL", "CLOUD");
    public static final Particle WAX_OFF = resolveParticle("WAX_OFF", "CRIT");
    public static final Particle CHERRY_LEAVES = resolveParticle("CHERRY_LEAVES", "HEART", "CRIT");
    public static final Particle PORTAL = resolveParticle("PORTAL", "CRIT");
    public static final Particle CRIT = resolveParticle("CRIT", "ENCHANTED_HIT");
    public static final Particle ELECTRIC_SPARK = resolveParticle("ELECTRIC_SPARK", "CRIT");
    public static final Particle SONIC_BOOM = resolveParticle("SONIC_BOOM", "EXPLOSION_NORMAL");
    public static final Particle SWEEP_ATTACK = resolveParticle("SWEEP_ATTACK", "SWEEP_ATTACK", "CRIT");
    public static final Particle CLOUD = resolveParticle("CLOUD", "SMOKE_NORMAL");
    public static final Particle SPLASH = resolveParticle("SPLASH", "WATER_SPLASH");

    public static Particle resolveParticle(String... candidateNames) {
        for (String name : candidateNames) {
            try {
                return Particle.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException | NoSuchFieldError ignored) {}
        }
        return Particle.values().length > 0 ? Particle.values()[0] : null;
    }

    public static Particle match(String input, Particle fallback) {
        if (input == null || input.isBlank()) return fallback;
        String clean = input.trim().toUpperCase().replace("MINECRAFT:", "");
        try {
            return Particle.valueOf(clean);
        } catch (IllegalArgumentException e) {
            // Attempt legacy alias mapping
            if (clean.equals("FIREWORK") || clean.equals("FIREWORKS_SPARK")) return FIREWORK;
            if (clean.equals("BLOCK") || clean.equals("BLOCK_CRACK")) return BLOCK;
            if (clean.equals("ITEM") || clean.equals("ITEM_CRACK")) return ITEM;
            if (clean.equals("DUST") || clean.equals("REDSTONE")) return DUST;
            if (clean.equals("POOF") || clean.equals("SMOKE_NORMAL")) return POOF;
            if (clean.equals("EXPLOSION") || clean.equals("EXPLOSION_NORMAL")) return EXPLOSION;
            return fallback;
        }
    }

    public static void spawnBlockBreak(World world, Location loc, int count, double ox, double oy, double oz, Material mat) {
        spawnBlockBreak(world, loc, count, ox, oy, oz, 0.12, mat);
    }

    public static void spawnBlockBreak(World world, Location loc, int count, double ox, double oy, double oz, double speed, Material mat) {
        if (world == null || loc == null || mat == null) return;
        try {
            if (BLOCK != null) {
                world.spawnParticle(BLOCK, loc, count, ox, oy, oz, speed, mat.createBlockData());
            }
        } catch (Exception ignored) {}
    }

    public static void spawnItemDebris(World world, Location loc, int count, double ox, double oy, double oz, double speed, Material mat) {
        if (world == null || loc == null || mat == null) return;
        try {
            if (ITEM != null) {
                world.spawnParticle(ITEM, loc, count, ox, oy, oz, speed, new ItemStack(mat));
            }
        } catch (Exception ignored) {}
    }

    public static void spawnDust(World world, Location loc, int count, double ox, double oy, double oz, Color color, float size) {
        if (world == null || loc == null) return;
        try {
            if (DUST != null) {
                world.spawnParticle(DUST, loc, count, ox, oy, oz, new Particle.DustOptions(color, size));
            }
        } catch (Exception ignored) {}
    }
}
