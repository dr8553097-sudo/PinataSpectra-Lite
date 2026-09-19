package net.dafealru.pinataspectralite.loot;

import net.dafealru.pinataspectralite.PinataPartyLite;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class LootManager {

    private final PinataPartyLite plugin;
    private final List<LootItem> defaultHitDrops = new ArrayList<>();
    private final List<LootItem> defaultFinaleDrops = new ArrayList<>();

    public LootManager(PinataPartyLite plugin) {
        this.plugin = plugin;
        loadDefaults();
    }

    private void loadDefaults() {
        defaultHitDrops.clear();
        defaultFinaleDrops.clear();

        // High-tier hit drops
        defaultHitDrops.add(new LootItem(Material.GOLDEN_APPLE, 1, 15.0, "&#FCD34D★ Golden Apple ★", List.of("&7¡Bocadillo dorado festivo!"), 50.0, List.of()));
        defaultHitDrops.add(new LootItem(Material.DIAMOND, 1, 20.0, "&#38BDF8★ Diamante Brillante ★", List.of("&7Cristal precioso de piñata."), 75.0, List.of()));
        defaultHitDrops.add(new LootItem(Material.EXPERIENCE_BOTTLE, 2, 45.0, "&#22C55E★ Frasco de Experiencia ★", List.of("&7¡Esencia mágica de fiesta!"), 20.0, List.of()));
        defaultHitDrops.add(new LootItem(Material.EMERALD, 2, 35.0, "&#22C55E★ Esmeralda Festiva ★", List.of("&7Moneda valiosa."), 30.0, List.of()));
        defaultHitDrops.add(new LootItem(Material.COOKIE, 3, 60.0, "&#FCD34D★ Galleta Dulce de Fiesta ★", List.of("&7¡Dulce tradicional de piñata!"), 15.0, List.of()));
        defaultHitDrops.add(new LootItem(Material.TOTEM_OF_UNDYING, 1, 3.0, "&#F59E0B★ Tótem de Inmortalidad ★", List.of("&7¡Talismán legendario de piñata!"), 500.0, List.of()));

        // Grand finale drops
        defaultFinaleDrops.add(new LootItem(Material.NETHERITE_INGOT, 1, 40.0, "&#71717A★ Lingote de Inframundita ★", List.of("&7¡Reliquia mítica de la piñata!"), 1000.0, List.of()));
        defaultFinaleDrops.add(new LootItem(Material.TOTEM_OF_UNDYING, 1, 50.0, "&#F59E0B★ Tótem Sagrado Festivo ★", List.of("&7Protegido por los espíritus de la piñata."), 750.0, List.of()));
        defaultFinaleDrops.add(new LootItem(Material.ENCHANTED_GOLDEN_APPLE, 1, 30.0, "&#EC4899★ Manzana Encantada ★", List.of("&7¡Festín divino de victoria!"), 1500.0, List.of()));
        defaultFinaleDrops.add(new LootItem(Material.DIAMOND_BLOCK, 2, 80.0, "&#38BDF8★ Bloque de Diamante ★", List.of("&7¡Gran premio de celebración!"), 500.0, List.of()));
        defaultFinaleDrops.add(new LootItem(Material.GOLD_BLOCK, 3, 100.0, "&#FCD34D★ Bloque de Oro Puro ★", List.of("&7Tesoro real de la fiesta."), 350.0, List.of()));
        defaultFinaleDrops.add(new LootItem(Material.EXPERIENCE_BOTTLE, 16, 100.0, "&#22C55E★ Fuente de Experiencia ★", List.of("&7Lluvia masiva de experiencia."), 200.0, List.of()));
    }

    public List<LootItem> getDefaultHitDrops() { return defaultHitDrops; }
    public List<LootItem> getDefaultFinaleDrops() { return defaultFinaleDrops; }
}
