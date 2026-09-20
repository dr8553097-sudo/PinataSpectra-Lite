package net.dafealru.pinataspectralite.finale;

import net.dafealru.pinataspectralite.PinataPartyLite;
import net.dafealru.pinataspectralite.pinata.PinataModel;
import net.dafealru.pinataspectralite.pinata.PinataProfile;
import net.dafealru.pinataspectralite.utils.ColorUtils;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class GrandFinaleEngine {

    private final PinataPartyLite plugin;

    public GrandFinaleEngine(PinataPartyLite plugin) {
        this.plugin = plugin;
    }

    public void executeGrandFinale(Location center, PinataProfile profile, List<Map.Entry<UUID, Integer>> leaderboard, Player finalHitter) {
        if (center == null || center.getWorld() == null) return;
        World world = center.getWorld();

        // 1. Detonation, Voxel Debris & Victory Fanfare
        world.spawnParticle(net.dafealru.pinataspectralite.util.ParticleAdapter.EXPLOSION_EMITTER, center, 4, 0.5, 0.5, 0.5, 0.0);
        world.spawnParticle(net.dafealru.pinataspectralite.util.ParticleAdapter.FIREWORK, center, 60, 1.2, 1.2, 1.2, 0.25);
        world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.8f);
        world.playSound(center, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.5f, 1.0f);

        // Voxel Debris fragment burst
        Material[] debrisMats = {Material.MAGENTA_CONCRETE, Material.YELLOW_CONCRETE, Material.CYAN_CONCRETE, Material.LIME_CONCRETE, Material.ORANGE_WOOL};
        for (Material mat : debrisMats) {
            net.dafealru.pinataspectralite.util.ParticleAdapter.spawnBlockBreak(world, center, 40, 0.8, 0.8, 0.8, mat);
        }
        spawnFlyingDebris(center, debrisMats);

        // Harmonic victory fanfare
        playVictoryFanfare(center);

        // 2. Gather Drops & Fountain Cascade Animation
        List<ItemStack> allLoot = gatherFinaleLoot(profile);
        playFountainCascade(center, allLoot);

        // 3. Spawning 3D MVP Hologram
        spawnVictoryHologram(center, profile, leaderboard, finalHitter);

        // 4. Broadcasts and Vault Rewards
        distributeRewards(profile, leaderboard, finalHitter);
    }

    private void playVictoryFanfare(Location center) {
        World world = center.getWorld();
        if (world == null) return;

        new BukkitRunnable() {
            int note = 0;
            final float[] pitches = {0.6f, 0.75f, 0.9f, 1.2f, 1.5f};

            @Override
            public void run() {
                if (note >= pitches.length) {
                    cancel();
                    return;
                }
                float p = pitches[note];
                world.playSound(center, Sound.BLOCK_NOTE_BLOCK_BELL, 1.4f, p);
                world.playSound(center, Sound.BLOCK_NOTE_BLOCK_CHIME, 1.3f, p);
                world.playSound(center, Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.0f, p);
                note++;
            }
        }.runTaskTimer(plugin, 2L, 4L);
    }

    private void spawnFlyingDebris(Location center, Material[] debrisMats) {
        World world = center.getWorld();
        if (world == null) return;

        List<Item> debrisItems = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            Material mat = debrisMats[i % debrisMats.length];
            Item it = world.dropItem(center.clone().add(0, 0.5, 0), new ItemStack(mat, 1));
            it.setPickupDelay(Integer.MAX_VALUE);
            it.setGlowing(true);
            double angle = ThreadLocalRandom.current().nextDouble(0, 2 * Math.PI);
            double speed = ThreadLocalRandom.current().nextDouble(0.35, 0.75);
            double up = ThreadLocalRandom.current().nextDouble(0.4, 0.75);
            it.setVelocity(new Vector(Math.cos(angle) * speed, up, Math.sin(angle) * speed));
            debrisItems.add(it);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Item item : debrisItems) {
                    if (item.isValid()) {
                        item.getWorld().spawnParticle(net.dafealru.pinataspectralite.util.ParticleAdapter.POOF, item.getLocation(), 4, 0.1, 0.1, 0.1, 0.02);
                        item.remove();
                    }
                }
            }
        }.runTaskLater(plugin, 45L);
    }

    private List<ItemStack> gatherFinaleLoot(PinataProfile profile) {
        List<ItemStack> list = new ArrayList<>();
        List<net.dafealru.pinataspectralite.loot.LootItem> source = (profile != null && profile.getCustomFinaleDrops() != null && !profile.getCustomFinaleDrops().isEmpty())
                ? profile.getCustomFinaleDrops() : plugin.getLootManager().getDefaultFinaleDrops();

        for (net.dafealru.pinataspectralite.loot.LootItem item : source) {
            double roll = ThreadLocalRandom.current().nextDouble(0.0, 100.0);
            if (roll <= item.getChance()) {
                ItemStack is = item.toItemStack();
                if (is != null) list.add(is);
            }
        }

        // Celebratory fiesta goodies
        int bonusCount = 20;
        Material[] bonus = {Material.DIAMOND, Material.GOLDEN_APPLE, Material.EMERALD, Material.EXPERIENCE_BOTTLE, Material.GOLD_INGOT};
        for (int i = 0; i < bonusCount; i++) {
            list.add(new ItemStack(bonus[i % bonus.length], 2));
        }
        return list;
    }

    private void playFountainCascade(Location center, List<ItemStack> items) {
        World world = center.getWorld();
        if (world == null || items.isEmpty()) return;

        new BukkitRunnable() {
            int pulse = 0;
            int itemPtr = 0;

            @Override
            public void run() {
                if (itemPtr >= items.size()) {
                    cancel();
                    return;
                }

                world.playSound(center, Sound.ENTITY_DOLPHIN_SPLASH, 1.4f, 1.0f + (pulse * 0.15f));
                world.playSound(center, Sound.BLOCK_NOTE_BLOCK_BELL, 1.2f, 1.0f + (pulse * 0.15f));
                world.spawnParticle(net.dafealru.pinataspectralite.util.ParticleAdapter.SPLASH, center.clone().add(0, 0.4, 0), 30, 0.4, 0.2, 0.4, 0.1);

                int batchSize = Math.max(4, (items.size() - itemPtr) / Math.max(1, (6 - pulse)));
                for (int i = 0; i < batchSize && itemPtr < items.size(); i++) {
                    ItemStack is = items.get(itemPtr++);
                    Item it = world.dropItem(center.clone().add(0, 0.3, 0), is);
                    it.setGlowing(true);
                    it.setPickupDelay(0); // Available to pick up IMMEDIATELY!

                    double a = (2 * Math.PI / Math.max(1, batchSize)) * i + (pulse * 0.5);
                    double spd = ThreadLocalRandom.current().nextDouble(0.25, 0.45);
                    it.setVelocity(new Vector(Math.cos(a) * spd, 0.38, Math.sin(a) * spd));

                    // Fountain experience orbs
                    ExperienceOrb orb = world.spawn(center.clone().add(0, 0.4, 0), ExperienceOrb.class);
                    orb.setExperience(ThreadLocalRandom.current().nextInt(10, 25));
                    orb.setVelocity(new Vector(Math.cos(a + 0.2) * (spd * 0.7), 0.35, Math.sin(a + 0.2) * (spd * 0.7)));
                }

                // Firework Rocket on pulse
                Location fwLoc = center.clone().add(Math.sin(pulse) * 1.5, 0.5, Math.cos(pulse) * 1.5);
                Firework fw = world.spawn(fwLoc, Firework.class);
                FireworkMeta meta = fw.getFireworkMeta();
                meta.addEffect(FireworkEffect.builder()
                        .withColor(Color.FUCHSIA, Color.YELLOW, Color.AQUA, Color.LIME)
                        .with(FireworkEffect.Type.BALL_LARGE)
                        .trail(true)
                        .build());
                meta.setPower(1);
                fw.setFireworkMeta(meta);

                pulse++;
            }
        }.runTaskTimer(plugin, 1L, 6L);
    }

    private void spawnVictoryHologram(Location center, PinataProfile profile, List<Map.Entry<UUID, Integer>> leaderboard, Player finalHitter) {
        World world = center.getWorld();
        if (world == null) return;

        Location holoLoc = center.clone().add(0, 3.4, 0);
        TextDisplay hologram = (TextDisplay) world.spawnEntity(holoLoc, EntityType.TEXT_DISPLAY);
        hologram.getPersistentDataContainer().set(PinataModel.PDC_KEY, PersistentDataType.BYTE, (byte) 1);
        hologram.setBillboard(Display.Billboard.CENTER);
        hologram.setShadowed(true);
        hologram.setBackgroundColor(Color.fromARGB(180, 25, 12, 38));

        int totalHits = leaderboard.stream().mapToInt(Map.Entry::getValue).sum();
        if (totalHits == 0) totalHits = 1;

        StringBuilder sb = new StringBuilder();
        sb.append("§d§l✦ §e§l🪅 PIÑATA CONQUERED! 🪅 §d§l✦\n");
        sb.append("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        if (leaderboard.isEmpty()) {
            sb.append("§7Defeated by the server community!\n");
        } else {
            for (int i = 0; i < Math.min(3, leaderboard.size()); i++) {
                Map.Entry<UUID, Integer> entry = leaderboard.get(i);
                Player p = Bukkit.getPlayer(entry.getKey());
                String name = p != null ? p.getName() : Bukkit.getOfflinePlayer(entry.getKey()).getName();
                if (name == null) name = "Warrior";
                int hits = entry.getValue();
                int pct = (int) Math.round(((double) hits / totalHits) * 100.0);
                String medal = (i == 0) ? "§e👑 1st MVP " : (i == 1 ? "§f🥈 2nd Place " : "§6🥉 3rd Place ");
                sb.append(medal).append("§b").append(name).append(" §7- §e").append(hits).append(" hits §a(").append(pct).append("%)\n");
            }
        }
        sb.append("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        hologram.setText(sb.toString());

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks++ > 300 || !hologram.isValid()) {
                    if (hologram.isValid()) {
                        hologram.remove();
                    }
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    private void distributeRewards(PinataProfile profile, List<Map.Entry<UUID, Integer>> leaderboard, Player finalHitter) {
        String mvpName = "Community";
        int mvpHits = 0;
        if (!leaderboard.isEmpty()) {
            UUID mvpUuid = leaderboard.get(0).getKey();
            mvpHits = leaderboard.get(0).getValue();
            Player onlineMvp = Bukkit.getPlayer(mvpUuid);
            mvpName = (onlineMvp != null) ? onlineMvp.getName() : Bukkit.getOfflinePlayer(mvpUuid).getName();
        } else if (finalHitter != null) {
            mvpName = finalHitter.getName();
            mvpHits = 1;
        }
        if (mvpName == null) mvpName = "Hero";

        // Titles & Sound Fanfare
        if (plugin.getMessageManager() != null) {
            String title = plugin.getMessageManager().getRaw("rewards.victory-title", "<gradient:#EC4899:#FCD34D><bold>🪅 PIÑATA CONQUERED! 🪅</bold></gradient>");
            String sub = plugin.getMessageManager().getRaw("rewards.victory-subtitle", "<#FCD34D>👑 Top MVP: <gradient:#F59E0B:#EC4899><bold>%mvp%</bold></gradient>").replace("%mvp%", mvpName);
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendTitle(ColorUtils.colorize(title), ColorUtils.colorize(sub), 10, 80, 20);
                p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.8f, 1.5f);
            }
            // Broadcast podium header
            plugin.getMessageManager().broadcast("rewards.victory-broadcast-header");
            plugin.getMessageManager().broadcast("rewards.victory-mvp-highlight", "mvp", mvpName, "hits", String.valueOf(mvpHits));
        }

        int totalHits = leaderboard.stream().mapToInt(Map.Entry::getValue).sum();
        if (totalHits == 0) totalHits = 1;

        int rank = 1;
        for (Map.Entry<UUID, Integer> entry : leaderboard) {
            if (rank > 3) break;
            Player p = Bukkit.getPlayer(entry.getKey());
            String name = p != null ? p.getName() : Bukkit.getOfflinePlayer(entry.getKey()).getName();
            if (name == null) name = "Player";

            int hits = entry.getValue();
            int pct = (int) Math.round(((double) hits / totalHits) * 100.0);

            double rewardMoney = (rank == 1) ? plugin.getConfig().getDouble("rewards.mvp-1st-money", 1000.0) :
                    (rank == 2 ? plugin.getConfig().getDouble("rewards.mvp-2nd-money", 500.0) :
                            plugin.getConfig().getDouble("rewards.mvp-3rd-money", 250.0));

            String key = (rank == 1) ? "rewards.victory-podium-1st" :
                    (rank == 2 ? "rewards.victory-podium-2nd" : "rewards.victory-podium-3rd");

            if (plugin.getMessageManager() != null) {
                plugin.getMessageManager().broadcast(key,
                        "player", name,
                        "hits", String.valueOf(hits),
                        "percent", String.valueOf(pct),
                        "money", String.valueOf((int) rewardMoney));
            }

            if (p != null && p.isOnline()) {
                if (plugin.getVaultHook().hasVault()) {
                    plugin.getVaultHook().deposit(p, rewardMoney);
                }
                if (rank == 1) {
                    p.getInventory().addItem(new ItemStack(Material.TOTEM_OF_UNDYING, 1), new ItemStack(Material.DIAMOND_BLOCK, 2), new ItemStack(Material.GOLD_BLOCK, 4));
                } else if (rank == 2) {
                    p.getInventory().addItem(new ItemStack(Material.DIAMOND_BLOCK, 1), new ItemStack(Material.GOLD_BLOCK, 2));
                }
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.4f);
            }
            rank++;
        }

        if (plugin.getMessageManager() != null) {
            plugin.getMessageManager().broadcast("rewards.victory-broadcast-footer");
        }

        // Discord Webhook notification
        if (plugin.getDiscordWebhookService() != null) {
            int topHits = !leaderboard.isEmpty() ? leaderboard.get(0).getValue() : 0;
            String pName = profile != null ? profile.getId() : "Piñata";
            plugin.getDiscordWebhookService().sendVictoryNotification(pName, mvpName, topHits);
        }

        // Promotion hint - ONLY sent to OPs / Admins!
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.isOp() || p.hasPermission("pinataspectra.admin")) {
                if (plugin.getMessageManager() != null) {
                    plugin.getMessageManager().send(p, "rewards.pro-promo-hint");
                }
            }
        }
    }
}
