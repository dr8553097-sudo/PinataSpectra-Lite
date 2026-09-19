package net.dafealru.pinataspectralite.effects;

import net.dafealru.pinataspectralite.PinataPartyLite;
import net.dafealru.pinataspectralite.pinata.PinataModel;
import net.dafealru.pinataspectralite.utils.ColorUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

public class DamagePopupEngine {

    private final PinataPartyLite plugin;

    public DamagePopupEngine(PinataPartyLite plugin) {
        this.plugin = plugin;
    }

    public void spawnPopup(Location hitLoc, int damage, boolean isVip) {
        if (hitLoc == null || hitLoc.getWorld() == null) return;

        double offsetX = ThreadLocalRandom.current().nextDouble(-0.35, 0.35);
        double offsetZ = ThreadLocalRandom.current().nextDouble(-0.35, 0.35);
        double offsetY = ThreadLocalRandom.current().nextDouble(0.2, 0.6);
        Location spawnPos = hitLoc.clone().add(offsetX, offsetY, offsetZ);

        TextDisplay popup = (TextDisplay) hitLoc.getWorld().spawnEntity(spawnPos, EntityType.TEXT_DISPLAY);
        popup.getPersistentDataContainer().set(PinataModel.PDC_KEY, PersistentDataType.BYTE, (byte) 1);
        popup.setBillboard(Display.Billboard.CENTER);
        popup.setShadowed(true);
        popup.setBackgroundColor(Color.fromARGB(0, 0, 0, 0)); // Transparent background

        String text = isVip
                ? ColorUtils.colorize("<gradient:#FCD34D:#F59E0B><bold>⚡ -" + damage + " VIP!</bold></gradient>")
                : ColorUtils.colorize("<gradient:#EC4899:#F43F5E><bold>💥 -" + damage + "</bold></gradient>");

        popup.setText(text);

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks++ > 16 || !popup.isValid()) {
                    if (popup.isValid()) popup.remove();
                    cancel();
                    return;
                }
                // Ascend smoothly upward
                popup.teleport(popup.getLocation().add(0, 0.045, 0));
                popup.setInterpolationDuration(1);
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }
}
