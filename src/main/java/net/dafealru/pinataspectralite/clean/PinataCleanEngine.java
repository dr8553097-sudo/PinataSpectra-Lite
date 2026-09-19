package net.dafealru.pinataspectralite.clean;

import net.dafealru.pinataspectralite.PinataPartyLite;
import net.dafealru.pinataspectralite.pinata.PinataModel;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class PinataCleanEngine {

    private final PinataPartyLite plugin;
    private BukkitTask autoCleanTask;

    public PinataCleanEngine(PinataPartyLite plugin) {
        this.plugin = plugin;
        startAutoCleanTask();
    }

    public void startAutoCleanTask() {
        if (autoCleanTask != null) autoCleanTask.cancel();

        // Run automated silent purge every 8 minutes (9600 ticks)
        autoCleanTask = new BukkitRunnable() {
            @Override
            public void run() {
                // If there's no active running pinata, clean any leftover entities
                if (plugin.getActiveInstance() == null || plugin.getActiveInstance().isDead()) {
                    int removed = cleanOrphansOnly();
                    if (removed > 0) {
                        plugin.getLogger().info("[AutoCleaner] Cleaned " + removed + " residual piñata entity/entities from the server.");
                    }
                }
            }
        }.runTaskTimer(plugin, 9600L, 9600L);
    }

    public int cleanOrphansOnly() {
        int removed = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getPersistentDataContainer().has(PinataModel.PDC_KEY, PersistentDataType.BYTE)) {
                    entity.remove();
                    removed++;
                }
            }
        }
        return removed;
    }

    public int cleanAll() {
        // 1. Remove active instance if running
        if (plugin.getActiveInstance() != null) {
            plugin.getActiveInstance().remove();
            plugin.setActiveInstance(null);
        }

        int removed = 0;

        // 2. Scan all loaded worlds for orphan tagged entities
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getPersistentDataContainer().has(PinataModel.PDC_KEY, PersistentDataType.BYTE)) {
                    world.spawnParticle(Particle.POOF, entity.getLocation(), 4, 0.1, 0.1, 0.1, 0.02);
                    entity.remove();
                    removed++;
                }
            }
        }

        if (removed > 0) {
            for (World world : Bukkit.getWorlds()) {
                if (!world.getPlayers().isEmpty()) {
                    world.playSound(world.getPlayers().iterator().next().getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.4f);
                }
            }
        }

        return removed;
    }
}
