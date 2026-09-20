package net.dafealru.pinataspectralite.gui;

import net.dafealru.pinataspectralite.PinataPartyLite;
import net.dafealru.pinataspectralite.messages.MessageManager;
import net.dafealru.pinataspectralite.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LanguageSelectorGUI {

    public static final NamespacedKey LANG_KEY = new NamespacedKey("pinataspectralite", "lang_code");

    public static void open(Player player, PinataPartyLite plugin) {
        String titleRaw = plugin.getMessageManager().getRaw(player, "gui.language-title", "<gradient:#8B5CF6:#EC4899><bold>SELECT YOUR LANGUAGE</bold></gradient>");
        Inventory inv = Bukkit.createInventory(null, 27, ColorUtils.colorizeComponent(titleRaw));

        // Border Glass
        ItemStack border = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta bMeta = border.getItemMeta();
        if (bMeta != null) {
            bMeta.setDisplayName(" ");
            border.setItemMeta(bMeta);
        }
        for (int i = 0; i < 27; i++) {
            if (i < 9 || i >= 18 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, border);
            }
        }

        String currentPlayerLocale = plugin.getMessageManager().resolvePlayerLocaleCode(player);
        Collection<MessageManager.LocaleInfo> locales = plugin.getMessageManager().getAllLocales();

        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25};
        int slotIndex = 0;

        for (MessageManager.LocaleInfo info : locales) {
            if (slotIndex >= slots.length) break;

            boolean isCurrent = info.getCode().equalsIgnoreCase(currentPlayerLocale);
            Material mat = getMaterialForLocale(info.getCode());

            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ColorUtils.colorize("<gradient:#FCD34D:#EC4899><bold>" + info.getFlag() + " " + info.getName() + "</bold></gradient> &7(" + info.getCode() + ")"));

                List<String> lore = new ArrayList<>();
                lore.add(ColorUtils.colorize("&8Locale: " + info.getCode()));
                lore.add("");

                if (isCurrent) {
                    lore.add(ColorUtils.colorize(" &#22C55E✔ &aCurrently Selected"));
                    meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                } else {
                    lore.add(ColorUtils.colorize(" &#FCD34D▶ &fClick to select this language"));
                }
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                meta.setLore(lore);

                meta.getPersistentDataContainer().set(LANG_KEY, PersistentDataType.STRING, info.getCode());
                item.setItemMeta(meta);
            }

            inv.setItem(slots[slotIndex++], item);
        }

        player.openInventory(inv);
    }

    private static Material getMaterialForLocale(String code) {
        String lower = code.toLowerCase();
        if (lower.startsWith("en")) return Material.WRITABLE_BOOK;
        if (lower.startsWith("es")) return Material.GOLDEN_APPLE;
        if (lower.startsWith("fr")) return Material.SWEET_BERRIES;
        if (lower.startsWith("de")) return Material.HONEYCOMB;
        if (lower.startsWith("pt")) return Material.EMERALD;
        if (lower.startsWith("ru")) return Material.AMETHYST_SHARD;
        if (lower.startsWith("zh")) return Material.FIREWORK_STAR;
        if (lower.startsWith("it")) return Material.COOKIE;
        if (lower.startsWith("ja")) return Material.PINK_PETALS;
        return Material.PAPER;
    }
}
