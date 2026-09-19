package net.dafealru.pinataspectralite.finale;

import org.bukkit.Material;

public enum DeathAnimationType {

    FOUNTAIN_CASCADE("Fountain Cascade", "Fuente en Cascada", Material.HEART_OF_THE_SEA,
            "&7Triple pulsating water geyser with crystal chime fanfare.",
            "&7Fuente rítmica en 3 pulsos con salpicaduras y campanadas.", false),

    COSMIC_SUPERNOVA("Cosmic Supernova", "Supernova Cósmica", Material.NETHER_STAR,
            "&5★ PRO ONLY &7- 4-Act gravitational singularity & 3D Fibonacci Big Bang.",
            "&5★ EXCLUSIVO PRO &7- Agujero negro gravitacional y Big Bang 3D.", true),

    VOLCANIC_ERUPTION("Volcanic Eruption", "Erupción Volcánica", Material.MAGMA_BLOCK,
            "&c★ PRO ONLY &7- Magma geyser launching burning ballistic loot arcs.",
            "&c★ EXCLUSIVO PRO &7- Géiser térmico vertical y parábolas de magma.", true),

    SPIRAL_VORTEX("Spiral Vortex", "Vórtice Espiral", Material.WIND_CHARGE,
            "&b★ PRO ONLY &7- Double-helix cyclone lifting items into orbital vortex.",
            "&b★ EXCLUSIVO PRO &7- Doble hélice ascendente de viento ciclónico.", true),

    CELESTIAL_RAIN("Celestial Rain", "Lluvia Celestial", Material.AMETHYST_CLUSTER,
            "&d★ PRO ONLY &7- Skyward beacon light pillar & falling shooting star meteors.",
            "&d★ EXCLUSIVO PRO &7- Rayo celestial al cielo y lluvia de meteoros.", true),

    FIREWORK_BURST("Firework Burst", "Salva Pirotécnica", Material.FIREWORK_ROCKET,
            "&e★ PRO ONLY &7- Synchronized aerial firework symphony & confetti blizzard.",
            "&e★ EXCLUSIVO PRO &7- Salvas pirotécnicas coreografiadas con confeti RGB.", true),

    CYBER_MATRIX("Cyber Matrix", "Matriz Cibernética", Material.BEACON,
            "&3★ PRO ONLY &7- 3D Holographic digital wireframe with electromagnetic drops.",
            "&3★ EXCLUSIVO PRO &7- Matriz holográfica cuántica y teleportación.", true);

    private final String englishName;
    private final String spanishName;
    private final Material icon;
    private final String englishDesc;
    private final String spanishDesc;
    private final boolean proOnly;

    DeathAnimationType(String englishName, String spanishName, Material icon, String englishDesc, String spanishDesc, boolean proOnly) {
        this.englishName = englishName;
        this.spanishName = spanishName;
        this.icon = icon;
        this.englishDesc = englishDesc;
        this.spanishDesc = spanishDesc;
        this.proOnly = proOnly;
    }

    public String getEnglishName() { return englishName; }
    public String getSpanishName() { return spanishName; }
    public Material getIcon() { return icon; }
    public String getEnglishDesc() { return englishDesc; }
    public String getSpanishDesc() { return spanishDesc; }
    public boolean isProOnly() { return proOnly; }
}
