package net.dafealru.pinataspectralite.items;

import net.dafealru.pinataspectralite.PinataPartyLite;
import net.dafealru.pinataspectralite.utils.ColorUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class PinataBatItem {

    public static final NamespacedKey BAT_KEY = new NamespacedKey("pinataspectralite", "pinata_bat");

    public static ItemStack createBat(PinataPartyLite plugin) {
        Material mat = Material.STICK;
        try {
            String matName = plugin.getConfig().getString("pinata-bat.material", "STICK");
            mat = Material.valueOf(matName.toUpperCase());
        } catch (Exception ignored) {}

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        // Display Name & Lore
        String name = plugin.getConfig().getString("pinata-bat.display-name", "<gradient:#EC4899:#FCD34D><bold>🪅 BATE FESTIVO DE PIÑATA</bold></gradient>");
        meta.setDisplayName(ColorUtils.colorize(name));

        List<String> lore = new ArrayList<>();
        lore.add(ColorUtils.colorize("&7¡Un mazo festivo diseñado para"));
        lore.add(ColorUtils.colorize("&7destrozar piñatas a toda velocidad!"));
        lore.add("");
        int bonus = plugin.getConfig().getInt("pinata-bat.bonus-damage", 1);
        lore.add(ColorUtils.colorize(" &#22C55E✔ &fDaño extra contra Piñata: &#FCD34D+" + bonus + " HP"));
        lore.add(ColorUtils.colorize(" &#60A5FA✔ &fEfecto de impacto: &#EC4899Confeti & Chispas"));
        lore.add("");
        lore.add(ColorUtils.colorize("&d✦ Edición de Evento PinataSpectra ✦"));
        meta.setLore(lore);

        // PDC Tag
        meta.getPersistentDataContainer().set(BAT_KEY, PersistentDataType.BYTE, (byte) 1);

        // Glow
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);

        item.setItemMeta(meta);
        return item;
    }

    public static boolean isPinataBat(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(BAT_KEY, PersistentDataType.BYTE);
    }
}
