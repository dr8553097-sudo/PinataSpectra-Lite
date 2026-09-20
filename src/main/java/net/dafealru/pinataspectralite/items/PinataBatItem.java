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

        int bonus = plugin.getConfig().getInt("pinata-bat.bonus-damage", 1);

        // Display Name (English default)
        String name = plugin.getConfig().getString("pinata-bat.display-name", "<gradient:#EC4899:#FCD34D><bold>🪅 FESTIVE PIÑATA BAT</bold></gradient>");
        meta.setDisplayName(ColorUtils.colorize(name));

        List<String> configLore = plugin.getConfig().getStringList("pinata-bat.lore");
        List<String> lore = new ArrayList<>();

        if (configLore != null && !configLore.isEmpty()) {
            for (String l : configLore) {
                lore.add(ColorUtils.colorize(l.replace("%bonus%", String.valueOf(bonus))));
            }
        } else {
            // Default English Lore
            lore.add(ColorUtils.colorize("&7A festive club designed to"));
            lore.add(ColorUtils.colorize("&7shatter piñatas with maximum speed!"));
            lore.add("");
            lore.add(ColorUtils.colorize(" &#22C55E✔ &fBonus Damage vs Piñata: &#FCD34D+" + bonus + " HP"));
            lore.add(ColorUtils.colorize(" &#60A5FA✔ &fImpact Effect: &#EC4899Confetti & Sparks"));
            lore.add("");
            lore.add(ColorUtils.colorize("&d✦ PinataSpectra Event Edition ✦"));
        }
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
