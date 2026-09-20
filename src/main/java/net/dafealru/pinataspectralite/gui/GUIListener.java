package net.dafealru.pinataspectralite.gui;

import net.dafealru.pinataspectralite.PinataPartyLite;
import net.dafealru.pinataspectralite.finale.DeathAnimationType;
import net.dafealru.pinataspectralite.pinata.PinataInstance;
import net.dafealru.pinataspectralite.pinata.PinataModel;
import net.dafealru.pinataspectralite.pinata.PinataProfile;
import net.dafealru.pinataspectralite.utils.ColorUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class GUIListener implements Listener {

    private final PinataPartyLite plugin;

    public GUIListener(PinataPartyLite plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().getTitle();

        if (title.contains("SOVEREIGN PRO") || title.contains("PINATASPECTRA ›")) {
            event.setCancelled(true);
            int slot = event.getRawSlot();

            if (slot == 24 || slot == 40) { // Clicked Pro Card or Buy Button
                player.closeInventory();
                sendProUpsell(player);
            }
        } else if (title.contains("SELECT YOUR LANGUAGE") || title.contains("IDIOMA") || title.contains("LANGUE") || title.contains("SPRACHE") || title.contains("言語") || title.contains("语言")) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked != null && clicked.hasItemMeta()) {
                String langCode = clicked.getItemMeta().getPersistentDataContainer().get(LanguageSelectorGUI.LANG_KEY, PersistentDataType.STRING);
                if (langCode != null) {
                    plugin.getMessageManager().setPlayerLanguage(player, langCode);
                    player.closeInventory();
                }
            }
        } else if (title.contains("PINATA STUDIO")) {
            event.setCancelled(true);
            int slot = event.getRawSlot();

            PinataProfile current = plugin.getRegistry().getAllProfiles().stream().findFirst().orElse(null);
            if (current == null) return;

            if (slot == 23) { // Death Animation
                PinataDeathAnimationGUI.open(player, current);
            } else if (slot == 21 || slot == 25) { // 3D Shape or Pro Banner
                player.closeInventory();
                sendProUpsell(player);
            } else if (slot == 40) {
                player.closeInventory();
            }
        } else if (title.contains("DEATH ANIMATION GALLERY")) {
            event.setCancelled(true);
            int slot = event.getRawSlot();

            PinataProfile current = plugin.getRegistry().getAllProfiles().stream().findFirst().orElse(null);
            if (current == null) return;

            if (slot == 36) { // Back
                PinataStudioGUI.open(player, current);
                return;
            }

            ItemStack clicked = event.getCurrentItem();
            if (clicked != null && clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName()) {
                String name = clicked.getItemMeta().getDisplayName();
                if (name.contains("PRO ONLY") || name.contains("🔒")) {
                    player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1.0f, 1.0f);
                    player.sendMessage(ColorUtils.colorize("&#EC4899✦ &#F43F5E&lEXCLUSIVE PRO FEATURE! &#8250; &#71717AUnlock this God-Tier death animation at: &#FCD34Dhttps://builtbybit.com/pinataspectra"));
                } else if (name.contains("Fountain Cascade")) {
                    current.setDeathAnimation(DeathAnimationType.FOUNTAIN_CASCADE);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
                    player.sendMessage(ColorUtils.colorize("&#22C55E✔ Selected death animation: &#FCD34DFountain Cascade"));
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        Entity target = event.getEntity();

        if (target instanceof Interaction interaction) {
            if (interaction.getPersistentDataContainer().has(PinataModel.PDC_KEY, PersistentDataType.BYTE)) {
                event.setCancelled(true);
                PinataInstance instance = plugin.getActiveInstance();
                if (instance != null && !instance.isDead()) {
                    instance.handleHit(player);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity target = event.getRightClicked();
        if (target instanceof Interaction interaction) {
            if (interaction.getPersistentDataContainer().has(PinataModel.PDC_KEY, PersistentDataType.BYTE)) {
                event.setCancelled(true);
                PinataInstance instance = plugin.getActiveInstance();
                if (instance != null && !instance.isDead()) {
                    instance.handleHit(event.getPlayer());
                }
            }
        }
    }

    public static void sendProUpsell(Player player) {
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.2f, 1.2f);
        player.sendMessage(ColorUtils.colorize("&#8B5CF6━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
        player.sendMessage(ColorUtils.colorize(" <gradient:#8B5CF6:#EC4899:#FCD34D><bold>👑 PINATASPECTRA SOVEREIGN PRO EDITION</bold></gradient>"));
        player.sendMessage(ColorUtils.colorize(" &#71717AUpgrade your server events to God-Tier with:"));
        player.sendMessage(ColorUtils.colorize("  &#FCD34D▸ &#f8 3D Voxel Forms &#71717A(Titan Mecha, Astral Dragon, Royal Crown)"));
        player.sendMessage(ColorUtils.colorize("  &#FCD34D▸ &#f7 Cosmic Supernovas &#71717A(Gravitational Black Holes & Loot Parachutes)"));
        player.sendMessage(ColorUtils.colorize("  &#FCD34D▸ &#f7 Mythical Bats &#71717A(Mjolnir Lightning & Void Siphons)"));
        player.sendMessage(ColorUtils.colorize("  &#FCD34D▸ &#f3D Jackpot Wheels & Dance Floors"));
        player.sendMessage(ColorUtils.colorize("  &#FCD34D▸ &#fHikariCP MySQL / MariaDB / Redis Network Sync"));
        player.sendMessage(ColorUtils.colorize(""));
        player.sendMessage(ColorUtils.colorize(" &#22C55E&l⚡ GET 20% OFF TODAY: &#FCD34D&nhttps://builtbybit.com/pinataspectra"));
        player.sendMessage(ColorUtils.colorize("&#8B5CF6━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
    }
}
