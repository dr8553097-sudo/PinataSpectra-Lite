package net.dafealru.pinataspectralite.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material != null ? material : Material.STONE);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(ItemStack item) {
        this.item = item.clone();
        this.meta = this.item.getItemMeta();
    }

    public ItemBuilder name(String name) {
        if (meta != null && name != null) {
            meta.setDisplayName(ColorUtils.colorize(name));
        }
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        if (meta != null && lore != null) {
            List<String> colored = new ArrayList<>();
            for (String line : lore) {
                colored.add(ColorUtils.colorize(line));
            }
            meta.setLore(colored);
        }
        return this;
    }

    public ItemBuilder lore(String... lines) {
        if (meta != null && lines != null) {
            List<String> colored = new ArrayList<>();
            for (String line : lines) {
                colored.add(ColorUtils.colorize(line));
            }
            meta.setLore(colored);
        }
        return this;
    }

    public ItemBuilder glow(boolean glow) {
        if (meta != null && glow) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    public ItemBuilder flags(ItemFlag... flags) {
        if (meta != null && flags != null) {
            meta.addItemFlags(flags);
        }
        return this;
    }

    public ItemStack build() {
        if (meta != null) {
            item.setItemMeta(meta);
        }
        return item;
    }
}
