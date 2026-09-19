package net.dafealru.pinataspectralite.gui;

import net.dafealru.pinataspectralite.finale.DeathAnimationType;
import net.dafealru.pinataspectralite.pinata.PinataProfile;
import net.dafealru.pinataspectralite.utils.ColorUtils;
import net.dafealru.pinataspectralite.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PinataDeathAnimationGUI {

    public static void open(Player player, PinataProfile profile) {
        if (player == null || profile == null) return;
        Inventory inv = Bukkit.createInventory(null, 45, ColorUtils.colorize("&d&l✦ DEATH ANIMATION GALLERY ✦"));

        ItemStack gray = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build();
        for (int i = 0; i < 45; i++) inv.setItem(i, gray);

        ItemStack info = new ItemBuilder(Material.BOOK)
                .name("<gradient:#8B5CF6:#EC4899><bold>✦ DEATH & REWARD RAIN STYLES ✦</bold></gradient>")
                .lore(
                        "&7Select the grand finale animation for &f" + profile.getId(),
                        "&7Lite Edition includes the classic &bFountain Cascade&7.",
                        "&7Unlock &66 God-Tier Supernovas &7in Sovereign PRO!"
                )
                .build();
        inv.setItem(4, info);

        DeathAnimationType[] types = DeathAnimationType.values();
        int[] slots = {20, 21, 22, 23, 24, 29, 30};

        for (int i = 0; i < types.length && i < slots.length; i++) {
            DeathAnimationType type = types[i];
            boolean isCurrent = profile.getDeathAnimation() == type;

            if (type.isProOnly()) {
                // PRO LOCKED ITEM (Golden padlock)
                ItemStack lockedItem = new ItemBuilder(type.getIcon())
                        .name("&c🔒 <gradient:#FCD34D:#F59E0B><bold>" + type.getEnglishName() + "</bold></gradient> &8(&6PRO ONLY&8)")
                        .lore(
                                type.getEnglishDesc(),
                                "",
                                "&8▪ &c[LOCKED IN LITE EDITION]",
                                "&e▶ &6Click to learn how to unlock in PRO!"
                        )
                        .glow(true)
                        .build();
                inv.setItem(slots[i], lockedItem);
            } else {
                // UNLOCKED ITEM
                ItemStack unlockedItem = new ItemBuilder(type.getIcon())
                        .name("&a✔ <gradient:#22C55E:#10B981><bold>" + type.getEnglishName() + "</bold></gradient> &8(&aACTIVE&8)")
                        .lore(
                                type.getEnglishDesc(),
                                "",
                                "&a✔ Currently Selected & Active",
                                "&8▪ &7Left-Click to save"
                        )
                        .glow(isCurrent)
                        .build();
                inv.setItem(slots[i], unlockedItem);
            }
        }

        // Back / Close
        ItemStack back = new ItemBuilder(Material.ARROW).name("&c◀ Back to Studio Hub").build();
        inv.setItem(36, back);

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.8f, 1.2f);
    }
}
