package net.dafealru.pinataspectralite.pinata;

import org.bukkit.Material;

public enum PinataShapeType {

    STAR("Classic 3D Star", "Estrella 3D Clásica", Material.NETHER_STAR, false),
    MECHA("Titan Mecha Core", "Núcleo Mecha Titán", Material.IRON_BLOCK, true),
    DRAGON_WYRM("Astral Cosmic Wyrm", "Gusano Dragón Cósmico", Material.DRAGON_HEAD, true),
    ROYALE_CROWN("Imperial Royal Crown", "Corona Real Imperial", Material.GOLD_BLOCK, true),
    ANCIENT_TOTEM("Ancient Mystic Totem", "Tótem Místico Ancestral", Material.TOTEM_OF_UNDYING, true),
    SOLAR_BIRD("Solar Pyre Phoenix", "Fénix Solar del Sol", Material.BLAZE_ROD, true);

    private final String englishName;
    private final String spanishName;
    private final Material icon;
    private final boolean proOnly;

    PinataShapeType(String englishName, String spanishName, Material icon, boolean proOnly) {
        this.englishName = englishName;
        this.spanishName = spanishName;
        this.icon = icon;
        this.proOnly = proOnly;
    }

    public String getEnglishName() { return englishName; }
    public String getSpanishName() { return spanishName; }
    public Material getIcon() { return icon; }
    public boolean isProOnly() { return proOnly; }
}
