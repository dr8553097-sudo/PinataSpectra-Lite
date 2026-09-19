package net.dafealru.pinataspectralite.loot;

import net.dafealru.pinataspectralite.utils.ColorUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class LootItem {

    private final Material material;
    private final int amount;
    private final double chance;
    private final String customName;
    private final List<String> lore;
    private final double money;
    private final List<String> commands;

    public LootItem(Material material, int amount, double chance, String customName, List<String> lore, double money, List<String> commands) {
        this.material = material != null ? material : Material.COOKIE;
        this.amount = Math.max(1, amount);
        this.chance = Math.max(0.1, Math.min(100.0, chance));
        this.customName = customName;
        this.lore = lore != null ? lore : new ArrayList<>();
        this.money = Math.max(0.0, money);
        this.commands = commands != null ? commands : new ArrayList<>();
    }

    public Material getMaterial() { return material; }
    public int getAmount() { return amount; }
    public double getChance() { return chance; }
    public String getCustomName() { return customName; }
    public List<String> getLore() { return lore; }
    public double getMoney() { return money; }
    public List<String> getCommands() { return commands; }

    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (customName != null && !customName.isEmpty()) {
                meta.setDisplayName(ColorUtils.colorize(customName));
            }
            if (lore != null && !lore.isEmpty()) {
                List<String> coloredLore = new ArrayList<>();
                for (String line : lore) {
                    coloredLore.add(ColorUtils.colorize(line));
                }
                meta.setLore(coloredLore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}
