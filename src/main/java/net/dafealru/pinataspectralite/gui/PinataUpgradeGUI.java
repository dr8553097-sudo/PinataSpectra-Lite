package net.dafealru.pinataspectralite.gui;

import net.dafealru.pinataspectralite.PinataPartyLite;
import net.dafealru.pinataspectralite.utils.ColorUtils;
import net.dafealru.pinataspectralite.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PinataUpgradeGUI {

    public static void open(Player player) {
        if (player == null) return;
        Inventory inv = Bukkit.createInventory(null, 45, ColorUtils.colorize("&d&l✦ PINATASPECTRA &8› &6&lSOVEREIGN PRO"));

        ItemStack grayGlass = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build();
        ItemStack purpleGlass = new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).name(" ").build();
        ItemStack goldGlass = new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).name(" ").build();

        for (int i = 0; i < 45; i++) {
            inv.setItem(i, grayGlass);
        }

        // Header Banner
        ItemStack header = new ItemBuilder(Material.NETHER_STAR)
                .name("<gradient:#8B5CF6:#EC4899:#FCD34D><bold>👑 PINATASPECTRA SOVEREIGN PRO EDITION</bold></gradient>")
                .lore(
                        "&7Compare your active &aLite Edition &7with the",
                        "&7ultimate enterprise &6Sovereign PRO &7engine.",
                        "",
                        "&e⚡ Ready to take your server events to God-Tier level?"
                )
                .glow(true)
                .build();
        inv.setItem(4, header);

        // LITE COLUMN (Slot 19, 20, 21)
        ItemStack liteCard = new ItemBuilder(Material.IRON_INGOT)
                .name("&#22C55E&l🍃 ACTIVE: PinataSpectra Lite")
                .lore(
                        "&7Standard Free Community Edition",
                        "",
                        "&8▪ &73D Voxel Star Model &a✔",
                        "&8▪ &7Max 2 Piñata Profiles &a✔",
                        "&8▪ &71 Classic Fountain Death FX &a✔",
                        "&8▪ &7Basic Wood Festival Bat &a✔",
                        "&8▪ &7Local SQLite Storage &a✔",
                        "&8▪ &7NuVotifier Goal Auto-Spawn &a✔",
                        "",
                        "&a&lCURRENTLY ACTIVE ON YOUR SERVER"
                )
                .build();
        inv.setItem(20, liteCard);

        // VS Separator
        ItemStack vs = new ItemBuilder(Material.BEACON)
                .name("&f&l⚡ &e&lVS &f&l⚡")
                .lore("&7Click below to unlock Sovereign PRO!")
                .build();
        inv.setItem(22, vs);

        // PRO COLUMN (Slot 23, 24, 25)
        ItemStack proCard = new ItemBuilder(Material.NETHERITE_INGOT)
                .name("<gradient:#FCD34D:#F59E0B><bold>👑 UNLOCK: Sovereign PRO (God-Tier)</bold></gradient>")
                .lore(
                        "&7Enterprise Network Powerhouse Edition",
                        "",
                        "&8▪ &e8 Full 3D Shapes &7(Mecha, Dragon, Crown, Totem) &6★",
                        "&8▪ &e7 Cinematic Supernovas &7(Gravitational Black Hole) &6★",
                        "&8▪ &eUnlimited Piñata Profiles & Boss Bars &6★",
                        "&8▪ &e7 Mythic Bats &7(Mjolnir Lightning, Void Siphon) &6★",
                        "&8▪ &e3D Jackpot Wheels & Synchronized RGB Floor &6★",
                        "&8▪ &eHikariCP MySQL / MariaDB / Redis Sync &6★",
                        "&8▪ &eLuxury Discord Rich Embeds & Live Leaderboards &6★",
                        "",
                        "&e▶ &6&lCLICK TO GET 20% DISCOUNT NOW!"
                )
                .glow(true)
                .build();
        inv.setItem(24, proCard);

        // Call to action button at bottom
        ItemStack buyButton = new ItemBuilder(Material.EMERALD_BLOCK)
                .name("<gradient:#22C55E:#10B981><bold>⚡ [ CLICK HERE TO UPGRADE TO PRO ] ⚡</bold></gradient>")
                .lore(
                        "&7Click to receive the direct store link in chat!",
                        "&7Instant instant digital download & lifetime updates."
                )
                .glow(true)
                .build();
        inv.setItem(40, buyButton);

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.6f);
    }
}
