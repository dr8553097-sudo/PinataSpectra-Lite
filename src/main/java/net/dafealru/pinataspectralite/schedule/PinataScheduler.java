package net.dafealru.pinataspectralite.schedule;

import net.dafealru.pinataspectralite.PinataPartyLite;
import net.dafealru.pinataspectralite.pinata.PinataProfile;
import net.dafealru.pinataspectralite.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PinataScheduler {

    private final PinataPartyLite plugin;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final Set<String> executedToday = new HashSet<>();
    private final Set<String> announcedToday = new HashSet<>();
    private String lastCheckedMinute = "";
    private BukkitTask task;

    public PinataScheduler(PinataPartyLite plugin) {
        this.plugin = plugin;
        start();
    }

    public void start() {
        if (task != null) task.cancel();

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.getConfig().getBoolean("schedule.enabled", false)) return;

                LocalTime now = LocalTime.now();
                String currentTime = now.format(timeFormatter);

                if (currentTime.equals("00:00") && !lastCheckedMinute.equals("00:00")) {
                    executedToday.clear();
                    announcedToday.clear();
                }
                lastCheckedMinute = currentTime;

                List<String> scheduledTimes = plugin.getConfig().getStringList("schedule.times");
                if (scheduledTimes.isEmpty()) return;

                int announceBefore = plugin.getConfig().getInt("schedule.announce-minutes-before", 5);

                for (String timeStr : scheduledTimes) {
                    try {
                        LocalTime target = LocalTime.parse(timeStr.trim(), timeFormatter);

                        // Check 5-minute pre-announcement
                        LocalTime announceTime = target.minusMinutes(announceBefore);
                        String announceStr = announceTime.format(timeFormatter);

                        if (currentTime.equals(announceStr) && !announcedToday.contains(timeStr)) {
                            announcedToday.add(timeStr);
                            broadcastPreAnnouncement(announceBefore, timeStr);
                        }

                        // Check actual spawn trigger
                        if (currentTime.equals(timeStr) && !executedToday.contains(timeStr)) {
                            executedToday.add(timeStr);
                            triggerScheduledPinata();
                        }
                    } catch (Exception ignored) {}
                }
            }
        }.runTaskTimer(plugin, 20L, 20L * 30L); // Check every 30 seconds
    }

    private void broadcastPreAnnouncement(int minutes, String eventTime) {
        Bukkit.broadcast(ColorUtils.colorizeComponent(
                "&#FCD34D⏰ &#EC4899&lPIÑATA PARTY EN " + minutes + " MINUTOS! &#8250; &#FCD34D(A las " + eventTime + ")\n" +
                        "&#71717A¡Prepara tu bate y únete a la arena para ganar increíbles premios!"
        ));
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.2f);
        }
    }

    private void triggerScheduledPinata() {
        if (plugin.getActiveInstance() != null && !plugin.getActiveInstance().isDead()) {
            plugin.getLogger().warning("[Scheduler] Scheduled Piñata skipped: an active Piñata is already spawned.");
            return;
        }

        String profileId = plugin.getConfig().getString("schedule.profile", "FESTIVE_LLAMA");
        PinataProfile profile = plugin.getRegistry().getProfile(profileId);
        if (profile == null) {
            profile = plugin.getRegistry().getAllProfiles().stream().findFirst().orElse(null);
        }

        if (profile != null) {
            Location loc = plugin.getSavedLocation("default");
            if (loc == null && !Bukkit.getOnlinePlayers().isEmpty()) {
                loc = Bukkit.getOnlinePlayers().iterator().next().getLocation().add(0, 2.5, 0);
            }

            if (loc != null) {
                plugin.spawnPinata(loc, profile);
                Bukkit.broadcast(ColorUtils.colorizeComponent(
                        "&#FCD34D★══════════════════════════════════════════════════★\n" +
                                "&#22C55E&l🎉 ¡EL EVENTO DE PIÑATA PROGRAMADO HA COMENZADO! 🎉\n" +
                                "&#FCD34D¡La piñata ha aparecido en la arena! ¡Todos a batear!\n" +
                                "&#FCD34D★══════════════════════════════════════════════════★"
                ));
            }
        }
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}
