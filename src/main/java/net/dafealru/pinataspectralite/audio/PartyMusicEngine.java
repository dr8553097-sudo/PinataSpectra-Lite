package net.dafealru.pinataspectralite.audio;

import net.dafealru.pinataspectralite.PinataPartyLite;
import net.dafealru.pinataspectralite.pinata.PinataInstance;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * Procedural Dynamic Fiesta Music Engine for Piñata Spectacle events.
 * Plays distinct soundtracks depending on the active Piñata phase:
 * - Phase 1: Cheerful Mexican Carnival Fiesta Symphony (Bells, Flutes, Chimes, Xylophone)
 * - Phase 2: Energetic Arcade Techno Groove (Syncopated Pling, Bass, Fast Hats & Snares)
 * - Phase 3: Dramatic Chaotic Boss Climax (Deep Bass, Urgent Bell Alarms, Rapid Arpeggios)
 */
public class PartyMusicEngine {

    private final PinataPartyLite plugin;
    private final PinataInstance instance;
    private BukkitTask musicTask;
    private int step = 0;
    private int tickCounter = 0;

    // Pitch conversion table from semitones (0 = C4/0.5f, 12 = C5/1.0f, 24 = C6/2.0f)
    private static float getPitch(int semitone) {
        return (float) Math.pow(2.0, (semitone - 12) / 12.0);
    }

    public PartyMusicEngine(PinataPartyLite plugin, PinataInstance instance) {
        this.plugin = plugin;
        this.instance = instance;
    }

    public void start() {
        if (!plugin.getConfig().getBoolean("music.enabled", true)) return;

        musicTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (instance == null || instance.isDead() || instance.getModel() == null) {
                    cancel();
                    return;
                }

                Location loc = (instance.getModel().getHitbox() != null && instance.getModel().getHitbox().isValid())
                        ? instance.getModel().getHitbox().getLocation()
                        : instance.getModel().getBaseLocation();

                if (loc == null || loc.getWorld() == null) return;

                PinataInstance.PinataPhase phase = instance.getCurrentPhase();
                int speedTicks = (phase == PinataInstance.PinataPhase.PHASE_1_STANDARD) ? 3 : 2;

                tickCounter++;
                if (tickCounter % speedTicks != 0) {
                    return;
                }

                switch (phase) {
                    case PHASE_1_STANDARD -> {
                        playPhase1Step(loc, step % 32);
                        step = (step + 1) % 32;
                    }
                    case PHASE_2_MICRO_SPEED -> {
                        playPhase2Step(loc, step % 16);
                        step = (step + 1) % 16;
                    }
                    case PHASE_3_CHAOTIC_SHIFTER -> {
                        playPhase3Step(loc, step % 16);
                        step = (step + 1) % 16;
                    }
                }
            }
        }.runTaskTimer(plugin, 5L, 1L);
    }

    public void stop() {
        if (musicTask != null) {
            musicTask.cancel();
            musicTask = null;
        }
    }

    /**
     * Phase 1 (Standard): Joyful, bouncy carnival melody with festive bells & chimes.
     */
    private void playPhase1Step(Location loc, int s) {
        float masterVol = (float) plugin.getConfig().getDouble("music.volume", 1.2);

        switch (s) {
            case 0 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BASS, 0, 1.2f * masterVol); // C Bass
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_CHIME, 12, 1.0f * masterVol); // C5 Chime
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 12, 1.0f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 0.9f);
            }
            case 1 -> playPerc(loc, Sound.BLOCK_NOTE_BLOCK_HAT, 0.6f);
            case 2 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_CHIME, 16, 1.0f * masterVol); // E5
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_FLUTE, 16, 0.8f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_SNARE, 0.7f);
            }
            case 3 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 19, 1.1f * masterVol); // G5
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_HAT, 0.5f);
            }
            case 4 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BASS, 7, 1.1f * masterVol); // G Bass
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BELL, 24, 1.2f * masterVol); // C6 Bell
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_CHIME, 24, 1.0f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 0.9f);
            }
            case 5 -> playPerc(loc, Sound.BLOCK_NOTE_BLOCK_HAT, 0.6f);
            case 6 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_CHIME, 23, 1.0f * masterVol); // B5
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_FLUTE, 23, 0.8f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_SNARE, 0.7f);
            }
            case 7 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 21, 1.1f * masterVol); // A5
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_HAT, 0.5f);
            }
            case 8 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BASS, 5, 1.2f * masterVol); // F Bass
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 21, 1.1f * masterVol);
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BELL, 21, 1.0f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 0.9f);
            }
            case 9 -> playPerc(loc, Sound.BLOCK_NOTE_BLOCK_HAT, 0.6f);
            case 10 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_CHIME, 24, 1.1f * masterVol); // C6
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_SNARE, 0.7f);
            }
            case 11 -> playChord(loc, Sound.BLOCK_NOTE_BLOCK_FLUTE, 21, 0.9f * masterVol); // A5
            case 12 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BASS, 7, 1.2f * masterVol); // G Bass
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BELL, 19, 1.1f * masterVol); // G5
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_CHIME, 19, 1.0f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 0.9f);
            }
            case 13 -> playChord(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 17, 1.0f * masterVol); // F5
            case 14 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_CHIME, 16, 1.0f * masterVol); // E5
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_SNARE, 0.7f);
            }
            case 15 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 14, 1.0f * masterVol); // D5
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_HAT, 0.6f);
            }
            case 16 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BASS, 0, 1.2f * masterVol); // C Bass
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BELL, 16, 1.1f * masterVol); // E5
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_CHIME, 16, 1.0f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 0.9f);
            }
            case 17 -> playPerc(loc, Sound.BLOCK_NOTE_BLOCK_HAT, 0.6f);
            case 18 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 19, 1.0f * masterVol); // G5
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_SNARE, 0.7f);
            }
            case 19 -> playChord(loc, Sound.BLOCK_NOTE_BLOCK_FLUTE, 24, 0.9f * masterVol); // C6
            case 20 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BASS, 9, 1.2f * masterVol); // A Bass
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BELL, 24, 1.2f * masterVol);
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_CHIME, 24, 1.0f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 0.9f);
            }
            case 21 -> playPerc(loc, Sound.BLOCK_NOTE_BLOCK_HAT, 0.6f);
            case 22 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_CHIME, 24, 1.0f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_SNARE, 0.7f);
            }
            case 23 -> playChord(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 21, 1.0f * masterVol);
            case 24 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BASS, 5, 1.2f * masterVol); // F Bass
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 21, 1.1f * masterVol);
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BELL, 24, 1.1f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 0.9f);
            }
            case 25 -> playChord(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 19, 1.0f * masterVol);
            case 26 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_CHIME, 17, 1.0f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_SNARE, 0.7f);
            }
            case 27 -> playChord(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 16, 1.0f * masterVol);
            case 28 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BASS, 7, 1.2f * masterVol); // G Bass
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BELL, 19, 1.2f * masterVol); // G5
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_CHIME, 19, 1.0f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 0.9f);
            }
            case 29 -> playChord(loc, Sound.BLOCK_NOTE_BLOCK_CHIME, 21, 1.0f * masterVol);
            case 30 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_FLUTE, 23, 1.0f * masterVol);
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 23, 1.0f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_SNARE, 0.8f);
            }
            case 31 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BELL, 24, 1.3f * masterVol); // Triumphant C6 Finale
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_CHIME, 24, 1.2f * masterVol);
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 24, 1.1f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1.0f);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_HAT, 0.8f);
            }
        }
    }

    /**
     * Phase 2 (Micro Speed): Fast, high-energy arcade groove (16-step loop).
     */
    private void playPhase2Step(Location loc, int s) {
        float masterVol = (float) plugin.getConfig().getDouble("music.volume", 1.2);

        switch (s) {
            case 0 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BASS, 12, 1.2f * masterVol);
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 12, 1.1f * masterVol); // C5
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1.0f);
            }
            case 1 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 15, 1.0f * masterVol); // Eb5
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_HAT, 0.7f);
            }
            case 2 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 19, 1.1f * masterVol); // G5
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_SNARE, 0.8f);
            }
            case 3 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 22, 1.1f * masterVol); // Bb5
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_HAT, 0.7f);
            }
            case 4 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BASS, 10, 1.2f * masterVol);
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BELL, 24, 1.2f * masterVol); // C6
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1.0f);
            }
            case 5 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 22, 1.0f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_HAT, 0.7f);
            }
            case 6 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 19, 1.1f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_SNARE, 0.9f);
            }
            case 7 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 15, 1.0f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_HAT, 0.8f);
            }
            case 8 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BASS, 8, 1.2f * masterVol);
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BELL, 20, 1.2f * masterVol); // Ab5
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1.0f);
            }
            case 9 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 20, 1.0f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_HAT, 0.7f);
            }
            case 10 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 24, 1.1f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_SNARE, 0.8f);
            }
            case 11 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 22, 1.1f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_HAT, 0.7f);
            }
            case 12 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BASS, 7, 1.2f * masterVol); // G Bass
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BELL, 19, 1.2f * masterVol); // G5
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1.0f);
            }
            case 13 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 22, 1.1f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_HAT, 0.8f);
            }
            case 14 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BELL, 23, 1.2f * masterVol); // B5
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 23, 1.1f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_SNARE, 0.9f);
            }
            case 15 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 24, 1.3f * masterVol); // C6 Accent
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 0.9f);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_HAT, 0.9f);
            }
        }
    }

    /**
     * Phase 3 (Chaotic Shifter): Dramatic, intense boss climax battle symphony (16-step loop).
     */
    private void playPhase3Step(Location loc, int s) {
        float masterVol = (float) plugin.getConfig().getDouble("music.volume", 1.2);

        switch (s) {
            case 0 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BASS, 0, 1.3f * masterVol); // Low C
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BELL, 12, 1.3f * masterVol);
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 24, 1.2f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1.2f);
            }
            case 1 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 20, 1.1f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_HAT, 0.8f);
            }
            case 2 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BELL, 17, 1.2f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_SNARE, 0.9f);
            }
            case 3 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 23, 1.2f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_HAT, 0.8f);
            }
            case 4 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BASS, 3, 1.3f * masterVol); // Low Eb
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BELL, 15, 1.3f * masterVol);
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 24, 1.2f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1.2f);
            }
            case 5 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 22, 1.1f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_HAT, 0.8f);
            }
            case 6 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BELL, 19, 1.2f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_SNARE, 1.0f);
            }
            case 7 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 24, 1.2f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_HAT, 0.8f);
            }
            case 8 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BASS, 5, 1.3f * masterVol); // Low F
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BELL, 17, 1.3f * masterVol);
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 21, 1.2f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1.2f);
            }
            case 9 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 24, 1.2f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_HAT, 0.8f);
            }
            case 10 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BELL, 23, 1.3f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_SNARE, 1.0f);
            }
            case 11 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 24, 1.2f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_HAT, 0.9f);
            }
            case 12 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BASS, 7, 1.4f * masterVol); // Low G
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BELL, 19, 1.3f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1.2f);
            }
            case 13 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 22, 1.2f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_HAT, 0.9f);
            }
            case 14 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BELL, 24, 1.4f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_SNARE, 1.1f);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1.0f);
            }
            case 15 -> {
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BELL, 24, 1.5f * masterVol); // Mega Climax Hit
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 24, 1.4f * masterVol);
                playChord(loc, Sound.BLOCK_NOTE_BLOCK_BASS, 12, 1.4f * masterVol);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1.2f);
                playPerc(loc, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f);
            }
        }
    }

    private void playChord(Location loc, Sound sound, int semitone, float vol) {
        if (loc == null || loc.getWorld() == null) return;
        int clampedSemi = Math.max(0, Math.min(24, semitone));
        float pitch = getPitch(clampedSemi);

        for (Player p : loc.getWorld().getPlayers()) {
            if (p.getLocation().distanceSquared(loc) <= 45.0 * 45.0) {
                p.playSound(loc, sound, SoundCategory.MASTER, vol, pitch);
            }
        }
    }

    private void playPerc(Location loc, Sound sound, float vol) {
        if (loc == null || loc.getWorld() == null) return;
        float masterVol = (float) plugin.getConfig().getDouble("music.volume", 1.2);
        for (Player p : loc.getWorld().getPlayers()) {
            if (p.getLocation().distanceSquared(loc) <= 45.0 * 45.0) {
                p.playSound(loc, sound, SoundCategory.MASTER, vol * masterVol, 1.0f);
            }
        }
    }
}
