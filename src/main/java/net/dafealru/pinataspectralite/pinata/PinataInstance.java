package net.dafealru.pinataspectralite.pinata;

import net.dafealru.pinataspectralite.PinataPartyLite;
import net.dafealru.pinataspectralite.audio.PartyMusicEngine;
import net.dafealru.pinataspectralite.loot.LootItem;
import net.dafealru.pinataspectralite.utils.ColorUtils;
import net.dafealru.pinataspectralite.util.ParticleAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class PinataInstance {

    private final PinataPartyLite plugin;
    private final Location originLocation;
    private final PinataProfile profile;
    private final PinataModel model;

    private int currentHealth;
    private final int maxHealth;
    private boolean dead = false;

    private final Map<UUID, Integer> hitCounters = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> consecutiveHits = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastLaunchTimes = new ConcurrentHashMap<>();
    private final Map<UUID, Long> playerCooldowns = new ConcurrentHashMap<>();
    private final Map<UUID, Long> fullInvMsgCooldown = new ConcurrentHashMap<>();

    private BossBar bossBar;
    private BukkitTask animationTask;
    private BukkitTask ambientSoundTask;
    private PartyMusicEngine musicEngine;

    public enum PinataPhase {
        PHASE_1_STANDARD,
        PHASE_2_MICRO_SPEED,
        PHASE_3_CHAOTIC_SHIFTER
    }

    private PinataPhase currentPhase = PinataPhase.PHASE_1_STANDARD;
    private double phaseSpeedMultiplier = 1.0;
    private int hitsSinceLastBlink = 0;
    private int totalHitsCounter = 0;
    private long lastBlinkTime = 0;
    private int ticksAlive = 0;
    private final int maxLifetimeTicks;

    public PinataInstance(PinataPartyLite plugin, Location location, PinataProfile profile) {
        this.plugin = plugin;
        this.originLocation = location.clone();
        if (originLocation.getWorld() != null) {
            double groundY = findSafeGroundY(originLocation.getWorld(), originLocation.getX(), originLocation.getZ(), originLocation.getY());
            this.originLocation.setY(groundY + 2.35);
        }
        this.profile = profile;

        // Dynamic health scaling based on online players
        int baseHealth = profile.getHealth() > 0 ? profile.getHealth() : 350;
        boolean scaleHealth = plugin.getConfig().getBoolean("health-scaling.enabled", true);
        int bonusPerPlayer = plugin.getConfig().getInt("health-scaling.bonus-per-player", 15);
        int onlineCount = Bukkit.getOnlinePlayers().size();
        this.maxHealth = scaleHealth ? Math.max(baseHealth, baseHealth + (onlineCount * bonusPerPlayer)) : baseHealth;
        this.currentHealth = maxHealth;

        int timeoutSeconds = plugin.getConfig().getInt("physics.despawn-timeout-seconds", 240);
        this.maxLifetimeTicks = timeoutSeconds * 20;

        this.model = new PinataModel(plugin, originLocation, profile);
    }

    public static double findSafeGroundY(World world, double x, double z, double fallbackY) {
        if (world == null) return fallbackY;
        int bx = (int) Math.floor(x);
        int bz = (int) Math.floor(z);

        int highestY = world.getHighestBlockYAt(bx, bz);
        int maxY = Math.min(world.getMaxHeight() - 2, Math.max(highestY, (int) fallbackY + 12));

        for (int y = maxY; y >= world.getMinHeight() + 1; y--) {
            Block block = world.getBlockAt(bx, y, bz);
            Material mat = block.getType();
            if (mat.isSolid() && !mat.name().contains("LEAVES") && !block.isPassable()) {
                return y + 1.0;
            }
        }
        return highestY > world.getMinHeight() ? highestY + 1.0 : fallbackY;
    }

    public void start() {
        model.spawn();
        model.setScaleAnimated(1.20f, 1);

        // 1. Setup BossBar
        bossBar = Bukkit.createBossBar(
                "",
                BarColor.PINK,
                BarStyle.SOLID
        );
        updateBossBarTitle();
        for (Player p : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(p);
        }

        // 2. Start Continuous Dynamic Fiesta Music Engine
        musicEngine = new PartyMusicEngine(plugin, this);
        musicEngine.start();

        // 3. Start Continuous Ambient Soundscape
        startAmbientSoundscape();

        // 4. Start Animation & Kinetics Loop
        animationTask = new BukkitRunnable() {
            double time = 0;

            @Override
            public void run() {
                if (dead || originLocation.getWorld() == null) {
                    cancel();
                    return;
                }

                ticksAlive++;

                // Animate BossBar sparkles, MVP tracker, and timer every 10 ticks (0.5s)
                if (ticksAlive % 10 == 0) {
                    updateBossBarTitle();
                }

                // Ground height reassurance clamp every 2 seconds
                if (ticksAlive % 40 == 0 && originLocation.getWorld() != null) {
                    double groundY = findSafeGroundY(originLocation.getWorld(), originLocation.getX(), originLocation.getZ(), originLocation.getY() - 2.35);
                    originLocation.setY(groundY + 2.35);
                }

                // Timeout check: Despawn if not destroyed in time
                if (maxLifetimeTicks > 0 && ticksAlive >= maxLifetimeTicks) {
                    despawnDueToTimeout();
                    cancel();
                    return;
                }

                // Countdown urgency heartbeat sound when time is running low (last 15 seconds)
                int remainingSeconds = Math.max(0, (maxLifetimeTicks - ticksAlive) / 20);
                if (remainingSeconds <= 15 && ticksAlive % 20 == 0 && originLocation.getWorld() != null) {
                    originLocation.getWorld().playSound(originLocation, Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 1.8f);
                    bossBar.setColor(BarColor.RED);
                }

                double baseSpeed = plugin.getConfig().getDouble("physics.pendulum-speed", 1.15);
                time += 0.08 * baseSpeed * phaseSpeedMultiplier;

                // Pendulum math (sway on X and Z)
                double swayFactor = (currentPhase == PinataPhase.PHASE_2_MICRO_SPEED) ? 1.9 : 1.45;
                double swayX = Math.sin(time) * swayFactor;
                double swayZ = Math.cos(time * 0.7) * (swayFactor * 0.6);
                double swayY = Math.abs(Math.sin(time * 2)) * 0.38;

                Location currentLoc = originLocation.clone().add(swayX, swayY, swayZ);

                // Update 3D Voxel Model position and rotation
                double yaw = time * 0.6;
                double pitch = Math.sin(time) * 0.22;
                double roll = Math.cos(time) * 0.22;

                model.updatePosition(currentLoc, yaw, pitch, roll);

                // Advanced Aesthetics: Double-Helix Ribbon Halo & Ambient Stardust
                if (currentLoc.getWorld() != null) {
                    World world = currentLoc.getWorld();

                    // 1. Double-Helix Counter-Rotating Ribbon Spirals
                    double helix1 = time * (currentPhase == PinataPhase.PHASE_2_MICRO_SPEED ? 4.5 : 2.8);
                    double radius = (currentPhase == PinataPhase.PHASE_2_MICRO_SPEED) ? 0.65 : 1.05;
                    double hx1 = Math.cos(helix1) * radius;
                    double hz1 = Math.sin(helix1) * radius;
                    double hy1 = Math.sin(time * 3.0) * 0.35;

                    Particle primaryPart = (currentPhase == PinataPhase.PHASE_3_CHAOTIC_SHIFTER) ? ParticleAdapter.PORTAL : ParticleAdapter.resolveParticle("END_ROD", "CRIT");
                    if (primaryPart != null) world.spawnParticle(primaryPart, currentLoc.clone().add(hx1, 0.4 + hy1, hz1), 1, 0, 0, 0, 0);
                    if (profile.getAuraParticle() != null) world.spawnParticle(profile.getAuraParticle(), currentLoc.clone().add(-hx1, 0.4 - hy1, -hz1), 1, 0, 0, 0, 0);

                    // 2. Festive Sparkle Trail under the Piñata
                    if (((int) (time * 10)) % 3 == 0) {
                        if (ParticleAdapter.CHERRY_LEAVES != null) world.spawnParticle(ParticleAdapter.CHERRY_LEAVES, currentLoc.clone().add(0, -0.4, 0), 2, 0.25, 0.1, 0.25, 0.02);
                        if (profile.getTrailParticle() != null) world.spawnParticle(profile.getTrailParticle(), currentLoc.clone().add(0, 0.2, 0), 2, 0.2, 0.2, 0.2, 0.02);
                    }

                    // 3. Periodic Sovereign Aura Fireworks Burst
                    if (ticksAlive % 100 == 0 && ticksAlive > 0) {
                        if (ParticleAdapter.WAX_OFF != null) world.spawnParticle(ParticleAdapter.WAX_OFF, currentLoc.clone().add(0, 0.6, 0), 16, 0.4, 0.4, 0.4, 0.08);
                        if (ParticleAdapter.FIREWORK != null) world.spawnParticle(ParticleAdapter.FIREWORK, currentLoc.clone().add(0, 0.8, 0), 8, 0.3, 0.3, 0.3, 0.05);
                    }
                }

                // Add newly joined players to bossbar
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!bossBar.getPlayers().contains(p)) {
                        bossBar.addPlayer(p);
                    }
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    private void startAmbientSoundscape() {
        ambientSoundTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (dead || originLocation.getWorld() == null) {
                    cancel();
                    return;
                }
                if (model.getHitbox() != null && model.getHitbox().isValid()) {
                    Location loc = model.getHitbox().getLocation();
                    if (loc.getWorld() != null) {
                        World world = loc.getWorld();
                        switch (currentPhase) {
                            case PHASE_1_STANDARD -> {
                                // Phase 1 (Harmonic Celestial Carnival): Beacon resonance, Amethyst harmonic chime, and Allay sparkle
                                float pitch = 0.95f + (float) (ThreadLocalRandom.current().nextDouble() * 0.15);
                                world.playSound(loc, Sound.BLOCK_BEACON_AMBIENT, SoundCategory.MASTER, 0.70f, 1.2f);
                                world.playSound(loc, Sound.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.MASTER, 1.1f, pitch);
                                if (ThreadLocalRandom.current().nextDouble() < 0.45) {
                                    world.playSound(loc, Sound.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.MASTER, 0.85f, 1.2f + (float) (ThreadLocalRandom.current().nextDouble() * 0.4));
                                }
                                if (ThreadLocalRandom.current().nextDouble() < 0.30) {
                                    world.playSound(loc, Sound.ENTITY_ALLAY_AMBIENT_WITH_ITEM, SoundCategory.MASTER, 0.80f, 1.15f);
                                }
                            }
                            case PHASE_2_MICRO_SPEED -> {
                                // Phase 2 (Kinetic Overdrive Surge): Respawn Anchor core hum, High-pitch crystal resonance, and illusioner shimmer
                                world.playSound(loc, Sound.BLOCK_RESPAWN_ANCHOR_AMBIENT, SoundCategory.MASTER, 0.95f, 1.35f);
                                world.playSound(loc, Sound.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.MASTER, 1.2f, 1.55f);
                                if (ThreadLocalRandom.current().nextDouble() < 0.50) {
                                    world.playSound(loc, Sound.BLOCK_AMETHYST_CLUSTER_STEP, SoundCategory.MASTER, 0.90f, 1.6f + (float) (ThreadLocalRandom.current().nextDouble() * 0.3));
                                }
                                if (ThreadLocalRandom.current().nextDouble() < 0.35) {
                                    world.playSound(loc, Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, SoundCategory.MASTER, 0.65f, 1.8f);
                                }
                            }
                            case PHASE_3_CHAOTIC_SHIFTER -> {
                                // Phase 3 (Cataclysmic Frenzy Climax): Heavy Warden Heartbeat, Conduit Sub-bass, Sculk Catalyst Bloom, and Void vortex
                                world.playSound(loc, Sound.ENTITY_WARDEN_HEARTBEAT, SoundCategory.MASTER, 1.4f, 1.1f);
                                world.playSound(loc, Sound.BLOCK_CONDUIT_AMBIENT, SoundCategory.MASTER, 1.2f, 0.85f);
                                if (ThreadLocalRandom.current().nextDouble() < 0.45) {
                                    world.playSound(loc, Sound.BLOCK_SCULK_CATALYST_BLOOM, SoundCategory.MASTER, 0.90f, 1.3f);
                                }
                                if (ThreadLocalRandom.current().nextDouble() < 0.35) {
                                    world.playSound(loc, Sound.BLOCK_PORTAL_AMBIENT, SoundCategory.MASTER, 0.75f, 1.4f);
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 10L, 20L);
    }

    private void despawnDueToTimeout() {
        if (dead) return;
        dead = true;

        if (originLocation.getWorld() != null) {
            if (ParticleAdapter.POOF != null) originLocation.getWorld().spawnParticle(ParticleAdapter.POOF, originLocation, 40, 0.8, 0.8, 0.8, 0.05);
            if (ParticleAdapter.CLOUD != null) originLocation.getWorld().spawnParticle(ParticleAdapter.CLOUD, originLocation, 25, 0.5, 0.5, 0.5, 0.02);
            originLocation.getWorld().playSound(originLocation, Sound.ENTITY_BAT_TAKEOFF, 1.5f, 0.8f);
        }

        if (plugin.getMessageManager() != null) {
            plugin.getMessageManager().broadcast("general.timeout-despawn");
        }

        remove();
        plugin.setActiveInstance(null);
    }

    public void handleHit(Player player) {
        if (dead || player == null) return;

        long now = System.currentTimeMillis();
        long last = playerCooldowns.getOrDefault(player.getUniqueId(), 0L);
        if (now - last < 250) { // 250ms anti-macro limit
            return;
        }
        playerCooldowns.put(player.getUniqueId(), now);

        // Check VIP Multiplier Permission
        int damage = 1;
        boolean isVip = false;
        for (int mult = 5; mult >= 2; mult--) {
            if (player.hasPermission("pinataspectra.hit." + mult) || player.hasPermission("pinata.hit." + mult)) {
                damage = mult;
                isVip = true;
                break;
            }
        }

        // Check Piñata Bat
        ItemStack held = player.getInventory().getItemInMainHand();
        if (net.dafealru.pinataspectralite.items.PinataBatItem.isPinataBat(held)) {
            int batBonus = plugin.getConfig().getInt("pinata-bat.bonus-damage", 1);
            damage += batBonus;
            isVip = true;
            if (player.getLocation().getWorld() != null) {
                player.getLocation().getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR, 1.2f, 1.6f);
                if (ParticleAdapter.FIREWORK != null) player.getLocation().getWorld().spawnParticle(ParticleAdapter.FIREWORK, player.getLocation().add(0, 1.2, 0), 8, 0.2, 0.2, 0.2, 0.05);
            }
        }

        int actualDamage = Math.min(damage, currentHealth);
        currentHealth -= actualDamage;
        totalHitsCounter++;
        hitCounters.merge(player.getUniqueId(), actualDamage, Integer::sum);

        // Check Boss Phases
        checkPhaseTransitions();

        Location loc = model.getHitbox() != null ? model.getHitbox().getLocation() : originLocation;

        // Ground Slam Shockwave Attack (Triggered every 15 hits in Phase 2 or Phase 3)
        if (plugin.getConfig().getBoolean("mechanics.boss-attacks.ground-slam.enabled", true)
                && currentPhase != PinataPhase.PHASE_1_STANDARD
                && totalHitsCounter % plugin.getConfig().getInt("mechanics.boss-attacks.ground-slam.hit-interval", 15) == 0) {
            triggerGroundSlamAttack(loc);
        }

        // Check Evasive Teleport (Active in Phase 3 Frenzy or high hit frequency)
        if (currentPhase == PinataPhase.PHASE_3_CHAOTIC_SHIFTER && currentHealth > 10) {
            hitsSinceLastBlink++;
            int blinkHits = plugin.getConfig().getInt("mechanics.phases.phase3-blink-hits", 3);
            if (hitsSinceLastBlink >= blinkHits && (now - lastBlinkTime >= 2000L)) {
                hitsSinceLastBlink = 0;
                lastBlinkTime = now;
                performEvasiveBlink();
            }
        }

        // Update BossBar & Holograms
        updateBossBarTitle();
        model.updateHealthDisplay(currentHealth, maxHealth);

        // Trigger Kinetic Hit Wobble
        model.applyHitWobble(player.getLocation().getDirection());

        // Trigger Realistic Fracture, Snapping Sounds & Voxel Hole Degradation
        model.applyFractureAndHoles(currentHealth, maxHealth, player.getLocation().getDirection(), loc);

        // Spawn 3D Floating Damage Popup
        if (plugin.getDamagePopupEngine() != null) {
            plugin.getDamagePopupEngine().spawnPopup(loc.clone().add(0, 0.6, 0), actualDamage, isVip);
        }

        // Dynamic Rhythm Physics & Launch Engine:
        // Consecutive combo tracking: Hits 1..(threshold - 1) have 0 knockback so players can chain hits comfortably.
        // When reaching the combo threshold (e.g. 5 hits), the piñata retaliates with a pushback or a timed high upward bounce.
        int combo = consecutiveHits.merge(player.getUniqueId(), 1, Integer::sum);
        int threshold = plugin.getConfig().getInt("physics.knockback.combo-threshold", 5);

        Vector push = player.getLocation().toVector().subtract(loc.toVector());
        push.setY(0);

        if (combo >= threshold) {
            consecutiveHits.put(player.getUniqueId(), 0); // Reset combo

            long lastLaunch = lastLaunchTimes.getOrDefault(player.getUniqueId(), 0L);
            long launchCooldown = plugin.getConfig().getLong("physics.knockback.upward-bounce.cooldown-seconds", 10L) * 1000L;
            boolean launchEnabled = plugin.getConfig().getBoolean("physics.knockback.upward-bounce.enabled", true);

            if (launchEnabled && (now - lastLaunch >= launchCooldown)) {
                lastLaunchTimes.put(player.getUniqueId(), now);
                // 🚀 Controlled High Upward Bounce Launch (~8-10 blocks high)
                double vertLaunch = plugin.getConfig().getDouble("physics.knockback.upward-bounce.vertical-height", 1.30);
                if (isVip) vertLaunch += 0.20;
                if (push.lengthSquared() > 0.0001) {
                    push.normalize().multiply(0.45);
                }
                push.setY(vertLaunch);
                player.setVelocity(push);

                player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.6f, 1.2f);
                player.playSound(player.getLocation(), Sound.ENTITY_SLIME_SQUISH, 1.6f, 1.3f);
                net.dafealru.pinataspectralite.util.SoundAdapter.play(player, player.getLocation(), net.dafealru.pinataspectralite.util.SoundAdapter.WIND_BURST, 1.4f, 1.1f);
                if (ParticleAdapter.FIREWORK != null) player.getWorld().spawnParticle(ParticleAdapter.FIREWORK, player.getLocation().add(0, 0.5, 0), 16, 0.35, 0.35, 0.35, 0.1);
                if (ParticleAdapter.CHERRY_LEAVES != null) player.getWorld().spawnParticle(ParticleAdapter.CHERRY_LEAVES, player.getLocation().add(0, 0.5, 0), 12, 0.3, 0.3, 0.3, 0.05);
                player.sendActionBar(ColorUtils.colorizeComponent("<gradient:#38BDF8:#818CF8><bold>🚀 ¡SUPER FIESTA LAUNCH! 🚀</bold></gradient>"));
            } else {
                // 💥 Retaliatory Strong Horizontal Pushback (~7-9 blocks away)
                double horizForce = plugin.getConfig().getDouble("physics.knockback.horizontal-force", 2.10);
                double vertForce = plugin.getConfig().getDouble("physics.knockback.vertical-force", 0.50);
                if (isVip) {
                    horizForce += 0.35;
                    vertForce += 0.10;
                }
                if (push.lengthSquared() > 0.0001) {
                    push.normalize().multiply(horizForce).setY(vertForce);
                    player.setVelocity(push);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1.5f, 1.0f);
                    net.dafealru.pinataspectralite.util.SoundAdapter.play(player, player.getLocation(), net.dafealru.pinataspectralite.util.SoundAdapter.WIND_BURST, 1.1f, 1.3f);
                    player.sendActionBar(ColorUtils.colorizeComponent("<gradient:#F59E0B:#EF4444><bold>💥 ¡PIÑATA PUSHBACK! 💥</bold></gradient>"));
                }
            }
        }
        // Hits 1..(threshold - 1): 0 Knockback! The player can stand and hit freely without being pushed away!

        // 🌀 Festive Spin-Warp (15% chance to playfully teleport player nearby)
        boolean spinWarpEnabled = plugin.getConfig().getBoolean("physics.knockback.spin-warp.enabled", true);
        double spinChance = plugin.getConfig().getDouble("physics.knockback.spin-warp.chance-percentage", 15.0) / 100.0;
        if (spinWarpEnabled && ThreadLocalRandom.current().nextDouble() < spinChance && player.getWorld() != null) {
            double minD = plugin.getConfig().getDouble("physics.knockback.spin-warp.min-distance", 4.0);
            double maxD = plugin.getConfig().getDouble("physics.knockback.spin-warp.max-distance", 7.5);
            double angle = ThreadLocalRandom.current().nextDouble(0, 2 * Math.PI);
            double dist = ThreadLocalRandom.current().nextDouble(minD, maxD);
            double targetX = loc.getX() + Math.cos(angle) * dist;
            double targetZ = loc.getZ() + Math.sin(angle) * dist;
            double safeY = findSafeGroundY(player.getWorld(), targetX, targetZ, player.getLocation().getY());
            Location warpTarget = new Location(player.getWorld(), targetX, safeY, targetZ, player.getLocation().getYaw() + 180f, player.getLocation().getPitch());

            if (ParticleAdapter.PORTAL != null) player.getWorld().spawnParticle(ParticleAdapter.PORTAL, player.getLocation().add(0, 1, 0), 25, 0.4, 0.4, 0.4, 0.1);
            if (ParticleAdapter.FIREWORK != null) player.getWorld().spawnParticle(ParticleAdapter.FIREWORK, player.getLocation().add(0, 1, 0), 12, 0.3, 0.3, 0.3, 0.05);

            player.teleport(warpTarget);

            if (ParticleAdapter.PORTAL != null) player.getWorld().spawnParticle(ParticleAdapter.PORTAL, warpTarget.clone().add(0, 1, 0), 25, 0.4, 0.4, 0.4, 0.1);
            if (ParticleAdapter.CHERRY_LEAVES != null) player.getWorld().spawnParticle(ParticleAdapter.CHERRY_LEAVES, warpTarget.clone().add(0, 1, 0), 15, 0.4, 0.4, 0.4, 0.05);
            player.playSound(warpTarget, Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1.4f, 1.3f);
            player.playSound(warpTarget, Sound.ENTITY_ENDERMAN_TELEPORT, 1.2f, 1.4f);

            player.sendActionBar(ColorUtils.colorizeComponent("<gradient:#EC4899:#FCD34D><bold>🌀 ¡FIESTA SPIN-WARP! 🌀</bold></gradient>"));
        }

        // Dynamic Cracking & Snapping Pitch Modulation
        double pct = Math.max(0.0, (double) currentHealth / maxHealth);
        float crackPitch = 1.15f + (float) ((1.0 - pct) * 0.55);

        // Direct Universal Loud Piñata Smacking Soundscape with Crisp Cardboard/Wood Snaps
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 2.0f, 1.1f);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 2.0f, 1.2f);
        player.playSound(player.getLocation(), Sound.BLOCK_DECORATED_POT_HIT, 2.0f, 0.95f);
        player.playSound(player.getLocation(), Sound.BLOCK_WOOD_BREAK, 1.8f, crackPitch);
        player.playSound(player.getLocation(), Sound.BLOCK_BAMBOO_WOOD_HIT, 1.8f, crackPitch + 0.2f);
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_FRAME_BREAK, 1.8f, 1.35f);
        player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1.2f, 1.45f);

        if (profile.getHitSound() != null) {
            player.playSound(player.getLocation(), profile.getHitSound(), profile.getHitSoundVolume(), profile.getHitSoundPitch());
        }

        if (loc.getWorld() != null) {
            loc.getWorld().playSound(loc, Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.8f, 1.2f);
            loc.getWorld().playSound(loc, Sound.BLOCK_DECORATED_POT_HIT, 1.8f, 0.95f);
            loc.getWorld().playSound(loc, Sound.BLOCK_WOOD_BREAK, 1.8f, crackPitch);
            loc.getWorld().playSound(loc, Sound.BLOCK_BAMBOO_WOOD_HIT, 1.6f, crackPitch + 0.2f);
            if (profile.getHitSound() != null) {
                loc.getWorld().playSound(loc, profile.getHitSound(), profile.getHitSoundVolume(), profile.getHitSoundPitch());
            }
            if (profile.getHitParticle() != null) {
                loc.getWorld().spawnParticle(profile.getHitParticle(), loc.clone().add(0, 0.8, 0), profile.getHitParticleCount(), 0.35, 0.35, 0.35, profile.getHitParticleSpeed());
            }
            if (ParticleAdapter.CRIT != null) loc.getWorld().spawnParticle(ParticleAdapter.CRIT, loc.clone().add(0, 0.8, 0), 12, 0.3, 0.3, 0.3, 0.15);
            ParticleAdapter.spawnBlockBreak(loc.getWorld(), loc.clone().add(0, 0.6, 0), 14, 0.25, 0.25, 0.25, 0.08, profile.getPrimaryBlock());
        }

        // Drop physical flying prizes & inventory delivery
        deliverHitDrops(player, loc);

        if (currentHealth <= 0) {
            die(player);
        }
    }

    private void triggerGroundSlamAttack(Location loc) {
        World world = loc.getWorld();
        if (world == null) return;

        double radius = plugin.getConfig().getDouble("mechanics.boss-attacks.ground-slam.radius", 6.0);
        double force = plugin.getConfig().getDouble("mechanics.boss-attacks.ground-slam.knockback-force", 1.1);

        if (ParticleAdapter.EXPLOSION != null) world.spawnParticle(ParticleAdapter.EXPLOSION, loc, 5, 0.5, 0.3, 0.5, 0.05);
        if (ParticleAdapter.SWEEP_ATTACK != null) world.spawnParticle(ParticleAdapter.SWEEP_ATTACK, loc, 16, 0.8, 0.2, 0.8, 0.1);
        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 1.4f);
        world.playSound(loc, Sound.BLOCK_ANVIL_LAND, 1.2f, 1.6f);

        for (Player p : world.getPlayers()) {
            if (p.getLocation().distance(loc) <= radius) {
                Vector push = p.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(force * 1.35).setY(0.70);
                p.setVelocity(push);
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1.2f, 0.9f);
                p.sendActionBar(ColorUtils.colorizeComponent("<gradient:#EF4444:#DC2626><bold>⚠ PIÑATA SHOCKWAVE BLAST! ⚠</bold></gradient>"));
            }
        }
    }

    private void checkPhaseTransitions() {
        if (!plugin.getConfig().getBoolean("mechanics.phases.enabled", true)) return;

        double healthRatio = (double) currentHealth / maxHealth;

        if (healthRatio <= 0.33 && currentPhase != PinataPhase.PHASE_3_CHAOTIC_SHIFTER) {
            transitionToPhase3();
        } else if (healthRatio <= 0.66 && currentPhase == PinataPhase.PHASE_1_STANDARD) {
            transitionToPhase2();
        }
    }

    private void transitionToPhase2() {
        this.currentPhase = PinataPhase.PHASE_2_MICRO_SPEED;
        this.phaseSpeedMultiplier = 1.85;

        float targetScale = (float) plugin.getConfig().getDouble("mechanics.phases.phase2-scale", 0.95);
        model.setScaleAnimated(targetScale, 20);

        if (bossBar != null) {
            bossBar.setColor(BarColor.YELLOW);
        }

        if (plugin.getMessageManager() != null) {
            plugin.getMessageManager().broadcast("phases.phase2-broadcast");
        }

        Location loc = model.getHitbox() != null ? model.getHitbox().getLocation() : originLocation;
        if (loc.getWorld() != null) {
            loc.getWorld().playSound(loc, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.MASTER, 1.4f, 1.25f);
            loc.getWorld().playSound(loc, Sound.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.MASTER, 1.5f, 1.8f);
            loc.getWorld().playSound(loc, Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, SoundCategory.MASTER, 1.5f, 1.4f);
            if (ParticleAdapter.POOF != null) loc.getWorld().spawnParticle(ParticleAdapter.POOF, loc, 35, 0.35, 0.35, 0.35, 0.05);
            if (ParticleAdapter.ELECTRIC_SPARK != null) loc.getWorld().spawnParticle(ParticleAdapter.ELECTRIC_SPARK, loc, 20, 0.4, 0.4, 0.4, 0.1);
        }
    }

    private void transitionToPhase3() {
        this.currentPhase = PinataPhase.PHASE_3_CHAOTIC_SHIFTER;
        this.phaseSpeedMultiplier = 2.20;

        model.setScaleAnimated(1.10f, 15);

        if (bossBar != null) {
            bossBar.setColor(BarColor.RED);
        }

        if (plugin.getMessageManager() != null) {
            plugin.getMessageManager().broadcast("phases.phase3-broadcast");
        }

        // Shockwave pulse & alarm
        Location loc = model.getHitbox() != null ? model.getHitbox().getLocation() : originLocation;
        if (loc.getWorld() != null) {
            loc.getWorld().playSound(loc, Sound.ITEM_GOAT_HORN_SOUND_0, SoundCategory.MASTER, 1.5f, 1.0f);
            loc.getWorld().playSound(loc, Sound.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.MASTER, 1.4f, 1.2f);
            loc.getWorld().playSound(loc, Sound.BLOCK_END_PORTAL_SPAWN, SoundCategory.MASTER, 1.2f, 1.2f);
            loc.getWorld().playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.MASTER, 1.5f, 0.8f);
            if (ParticleAdapter.SONIC_BOOM != null) loc.getWorld().spawnParticle(ParticleAdapter.SONIC_BOOM, loc, 1, 0, 0, 0, 0);
            if (ParticleAdapter.PORTAL != null) loc.getWorld().spawnParticle(ParticleAdapter.PORTAL, loc, 60, 0.8, 0.8, 0.8, 0.2);

            // Knockback to nearby players
            for (Player p : loc.getWorld().getPlayers()) {
                if (p.getLocation().distance(loc) <= 7.0) {
                    Vector push = p.getLocation().toVector().subtract(loc.toVector()).normalize().setY(0.40);
                    p.setVelocity(push.multiply(0.85));
                }
            }
        }
    }

    private void performEvasiveBlink() {
        if (originLocation.getWorld() == null) return;
        World world = originLocation.getWorld();

        double minDistance = plugin.getConfig().getDouble("mechanics.teleport.min-distance", 12.0);
        double maxDistance = plugin.getConfig().getDouble("mechanics.teleport.max-distance", 22.0);

        // Calculate far jump coordinates
        double angle = ThreadLocalRandom.current().nextDouble(0, 2 * Math.PI);
        double distance = ThreadLocalRandom.current().nextDouble(minDistance, maxDistance);
        double newX = originLocation.getX() + Math.cos(angle) * distance;
        double newZ = originLocation.getZ() + Math.sin(angle) * distance;

        // Find safe ground surface Y and clamp height to always be accessible regardless of mountains/slopes
        double groundY = findSafeGroundY(world, newX, newZ, originLocation.getY() - 2.35);
        Block groundBlock = world.getBlockAt((int) Math.floor(newX), (int) Math.floor(groundY), (int) Math.floor(newZ));

        // Avoid water or lava
        if (groundBlock.isLiquid() || groundBlock.getType() == Material.WATER || groundBlock.getType() == Material.LAVA) {
            newX = originLocation.getX() + ThreadLocalRandom.current().nextDouble(-4.0, 4.0);
            newZ = originLocation.getZ() + ThreadLocalRandom.current().nextDouble(-4.0, 4.0);
            groundY = findSafeGroundY(world, newX, newZ, originLocation.getY() - 2.35);
        }

        double newY = groundY + 2.35; // Perfect elevated hanging height above ground
        Location targetLoc = new Location(world, newX, newY, newZ);

        Location oldLoc = originLocation.clone();

        // Warp particle streak between old and new location
        Vector dir = targetLoc.toVector().subtract(oldLoc.toVector());
        double dist = oldLoc.distance(targetLoc);
        int steps = (int) (dist * 3);
        if (steps > 0 && ParticleAdapter.PORTAL != null) {
            Vector stepVec = dir.clone().normalize().multiply(0.33);
            Location traceLoc = oldLoc.clone();
            for (int i = 0; i < steps; i++) {
                traceLoc.add(stepVec);
                world.spawnParticle(ParticleAdapter.PORTAL, traceLoc, 2, 0.05, 0.05, 0.05, 0.02);
            }
        }

        // FX at old location
        if (ParticleAdapter.PORTAL != null) world.spawnParticle(ParticleAdapter.PORTAL, oldLoc, 40, 0.6, 0.6, 0.6, 0.15);
        if (ParticleAdapter.POOF != null) world.spawnParticle(ParticleAdapter.POOF, oldLoc, 20, 0.4, 0.4, 0.4, 0.05);
        world.playSound(oldLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.4f, 1.2f);

        // Update origin & anchor
        this.originLocation.setX(newX);
        this.originLocation.setY(newY);
        this.originLocation.setZ(newZ);
        model.setBaseLocation(originLocation);

        // FX at new location
        if (ParticleAdapter.PORTAL != null) world.spawnParticle(ParticleAdapter.PORTAL, targetLoc, 40, 0.6, 0.6, 0.6, 0.15);
        if (ParticleAdapter.FIREWORK != null) world.spawnParticle(ParticleAdapter.FIREWORK, targetLoc, 15, 0.3, 0.3, 0.3, 0.08);
        world.playSound(targetLoc, Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 1.3f, 1.6f);

        String sx = String.valueOf((int) newX);
        String sy = String.valueOf((int) newY);
        String sz = String.valueOf((int) newZ);

        // Broadcast chat and Actionbar with exact coordinates
        if (plugin.getMessageManager() != null) {
            plugin.getMessageManager().broadcast("phases.blink-chat", "x", sx, "y", sy, "z", sz);
            for (Player p : Bukkit.getOnlinePlayers()) {
                plugin.getMessageManager().sendActionBar(p, "phases.blink-actionbar", "x", sx, "y", sy, "z", sz);
            }
        }
    }

    private void deliverHitDrops(Player player, Location loc) {
        List<LootItem> hitDrops = (profile.getCustomHitDrops() != null && !profile.getCustomHitDrops().isEmpty())
                ? profile.getCustomHitDrops() : plugin.getLootManager().getDefaultHitDrops();

        if (loc.getWorld() == null) return;

        // 1. Moderate candy popping chance (20% chance to pop 1 flying candy item into arena)
        if (ThreadLocalRandom.current().nextDouble(0.0, 100.0) <= 20.0 && !hitDrops.isEmpty()) {
            LootItem item = hitDrops.get(ThreadLocalRandom.current().nextInt(hitDrops.size()));
            ItemStack is = item.toItemStack();
            if (is != null) {
                Item dropped = loc.getWorld().dropItem(loc.clone().add(0, 0.5, 0), is);
                dropped.setPickupDelay(10);
                double angle = ThreadLocalRandom.current().nextDouble(0, 2 * Math.PI);
                double speed = ThreadLocalRandom.current().nextDouble(0.20, 0.40);
                dropped.setVelocity(new Vector(Math.cos(angle) * speed, 0.35, Math.sin(angle) * speed));
            }
        }

        // 2. Spawn 1 physical Experience Orb
        ExperienceOrb orb = loc.getWorld().spawn(loc.clone().add(0, 0.5, 0), ExperienceOrb.class);
        orb.setExperience(ThreadLocalRandom.current().nextInt(2, 5));

        // 3. Guaranteed rewards roll for the player who hit
        for (LootItem item : hitDrops) {
            double roll = ThreadLocalRandom.current().nextDouble(0.0, 100.0);
            if (roll <= item.getChance()) {
                ItemStack is = item.toItemStack();
                if (is != null && player.isOnline()) {
                    HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(is);
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.7f, 1.6f);
                    if (!leftover.isEmpty()) {
                        for (ItemStack left : leftover.values()) {
                            Item drop = player.getLocation().getWorld().dropItem(player.getLocation(), left);
                            drop.setPickupDelay(10);
                        }
                        // Throttle inventory full warning
                        long now = System.currentTimeMillis();
                        long lastWarn = fullInvMsgCooldown.getOrDefault(player.getUniqueId(), 0L);
                        if (now - lastWarn >= 10000L) {
                            fullInvMsgCooldown.put(player.getUniqueId(), now);
                            if (plugin.getMessageManager() != null) {
                                plugin.getMessageManager().send(player, "rewards.inventory-full");
                            }
                        }
                    }
                }
                if (item.getMoney() > 0 && plugin.getVaultHook().hasVault()) {
                    plugin.getVaultHook().deposit(player, item.getMoney());
                }
            }
        }
    }

    private void die(Player finalHitter) {
        if (dead) return;
        dead = true;

        if (animationTask != null) {
            animationTask.cancel();
        }
        if (ambientSoundTask != null) {
            ambientSoundTask.cancel();
        }

        Location deathLoc = model.getHitbox() != null ? model.getHitbox().getLocation() : originLocation;
        model.destroy();

        if (bossBar != null) {
            bossBar.removeAll();
        }

        // Compute Leaderboard
        List<Map.Entry<UUID, Integer>> leaderboard = new ArrayList<>(hitCounters.entrySet());
        leaderboard.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // Update database stats
        for (int i = 0; i < leaderboard.size(); i++) {
            Map.Entry<UUID, Integer> entry = leaderboard.get(i);
            Player p = Bukkit.getPlayer(entry.getKey());
            String name = (p != null) ? p.getName() : Bukkit.getOfflinePlayer(entry.getKey()).getName();
            plugin.getDatabaseManager().incrementPlayerStats(entry.getKey(), name, entry.getValue(), i == 0);
        }

        // Participation Rewards
        deliverParticipationRewards(leaderboard);

        // Trigger Grand Finale
        plugin.getGrandFinaleEngine().executeGrandFinale(deathLoc, profile, leaderboard, finalHitter);
        plugin.setActiveInstance(null);
    }

    private void deliverParticipationRewards(List<Map.Entry<UUID, Integer>> leaderboard) {
        if (!plugin.getConfig().getBoolean("rewards.participation-enabled", true)) return;

        int minHits = plugin.getConfig().getInt("rewards.participation-min-hits", 10);
        double partMoney = plugin.getConfig().getDouble("rewards.participation-money", 150.0);

        for (Map.Entry<UUID, Integer> entry : leaderboard) {
            if (entry.getValue() >= minHits) {
                Player p = Bukkit.getPlayer(entry.getKey());
                if (p != null && p.isOnline()) {
                    if (partMoney > 0 && plugin.getVaultHook().hasVault()) {
                        plugin.getVaultHook().deposit(p, partMoney);
                    }
                    if (plugin.getMessageManager() != null) {
                        plugin.getMessageManager().send(p, "rewards.participation-reward",
                                "hits", String.valueOf(entry.getValue()),
                                "money", String.valueOf((int) partMoney));
                    }
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
                }
            }
        }
    }

    public void remove() {
        dead = true;
        if (musicEngine != null) musicEngine.stop();
        if (animationTask != null) animationTask.cancel();
        if (ambientSoundTask != null) ambientSoundTask.cancel();
        if (model != null) model.destroy();
        if (bossBar != null) bossBar.removeAll();
    }

    public void updateBossBarTitle() {
        if (bossBar == null) return;
        int remainingSeconds = Math.max(0, (maxLifetimeTicks - ticksAlive) / 20);
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        String formattedTime = String.format("%d:%02d", minutes, seconds);

        int percent = (int) Math.ceil(((double) currentHealth / maxHealth) * 100);
        double progress = Math.max(0.0, Math.min(1.0, (double) currentHealth / maxHealth));
        bossBar.setProgress(progress);

        // Dynamic BarColor and BarStyle based on Phase & Health
        switch (currentPhase) {
            case PHASE_3_CHAOTIC_SHIFTER -> {
                bossBar.setColor(BarColor.PURPLE);
                bossBar.setStyle(BarStyle.SEGMENTED_6);
            }
            case PHASE_2_MICRO_SPEED -> {
                bossBar.setColor(BarColor.YELLOW);
                bossBar.setStyle(BarStyle.SEGMENTED_10);
            }
            default -> {
                bossBar.setColor(percent > 50 ? BarColor.PINK : BarColor.YELLOW);
                bossBar.setStyle(BarStyle.SEGMENTED_20);
            }
        }

        // Oscillating animated sparkles
        int animStep = (ticksAlive / 10) % 4;
        String spark = switch (animStep) {
            case 0 -> "✦";
            case 1 -> "★";
            case 2 -> "✦";
            default -> "✨";
        };

        // Compact Health display
        String healthGrad = (percent > 60)
                ? "<#22C55E><bold>❤ " + percent + "%</bold>"
                : (percent > 30 ? "<#FCD34D><bold>❤ " + percent + "%</bold>" : "<#EF4444><bold>❤ " + percent + "%</bold>");

        // Urgency countdown timer animation
        String timeDisplay = (remainingSeconds <= 30 && (ticksAlive % 10 < 5))
                ? "<#EF4444><bold>⏳ " + formattedTime + "</bold>"
                : "<yellow><bold>⏳ " + formattedTime + "</bold>";

        // Compact, clean & Bedrock/Mobile-friendly title (No wrapping on small screens)
        String title = spark + " " + profile.getDisplayName() + " <dark_gray>• " + healthGrad + " <dark_gray>• " + timeDisplay;

        bossBar.setTitle(ColorUtils.colorize(title));
    }

    public boolean isDead() { return dead; }
    public PinataModel getModel() { return model; }
    public PinataProfile getProfile() { return profile; }
    public PinataPhase getCurrentPhase() { return currentPhase; }
}
