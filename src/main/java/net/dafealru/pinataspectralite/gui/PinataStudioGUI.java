package net.dafealru.pinataspectralite.gui;

import net.dafealru.pinataspectralite.pinata.PinataProfile;
import net.dafealru.pinataspectralite.utils.ColorUtils;
import net.dafealru.pinataspectralite.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PinataStudioGUI {

    public static void open(Player player, PinataProfile profile) {
        if (player == null || profile == null) return;
        Inventory inv = Bukkit.createInventory(null, 45, ColorUtils.colorize("&d&l✦ PINATA STUDIO &8› &f" + profile.getId()));

        ItemStack gray = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build();
        for (int i = 0; i < 45; i++) inv.setItem(i, gray);

        // Header
        ItemStack head = new ItemBuilder(Material.NAME_TAG)
                .name("<gradient:#EC4899:#FCD34D><bold>✦ " + profile.getId() + " ✦</bold></gradient>")
                .lore(
                        "&7Health: &e" + profile.getHealth() + " HP",
                        "&7Display: " + profile.getDisplayName(),
                        "&7Death FX: &b" + profile.getDeathAnimation().getEnglishName()
                )
                .build();
        inv.setItem(4, head);

        // Health modifier
        ItemStack hp = new ItemBuilder(Material.RED_DYE)
                .name("&c❤ Max Health")
                .lore("&7Current: &e" + profile.getHealth() + " HP", "&7Configured in YAML file.")
                .build();
        inv.setItem(19, hp);

        // 3D Voxel Form
        ItemStack shape = new ItemBuilder(Material.NETHER_STAR)
                .name("&e⭐ 3D Shape Form")
                .lore(
                        "&7Active: &aClassic 3D Star",
                        "",
                        "&c🔒 &7Mecha, Dragon, Crown & Totem",
                        "&8▪ &6Available in Sovereign PRO!"
                )
                .build();
        inv.setItem(21, shape);

        // Death FX (Slot 23)
        ItemStack death = new ItemBuilder(Material.FIREWORK_ROCKET)
                .name("&d🪅 Death & Loot Animation")
                .lore(
                        "&7Current: &b" + profile.getDeathAnimation().getEnglishName(),
                        "",
                        "&e▶ &fClick to open Animation Gallery!"
                )
                .glow(true)
                .build();
        inv.setItem(23, death);

        // Sovereign PRO Banner (Slot 25)
        ItemStack pro = new ItemBuilder(Material.GOLD_BLOCK)
                .name("<gradient:#FCD34D:#F59E0B><bold>👑 UPGRADE TO SOVEREIGN PRO</bold></gradient>")
                .lore(
                        "&7Unlock unlimited profiles, 7 mythical bats,",
                        "&7gravitational black holes, and 3D mini-games!",
                        "",
                        "&e▶ &6Click to compare editions!"
                )
                .glow(true)
                .build();
        inv.setItem(25, pro);

        // Close
        ItemStack close = new ItemBuilder(Material.BARRIER).name("&c✖ Close").build();
        inv.setItem(40, close);

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.4f);
    }
}
