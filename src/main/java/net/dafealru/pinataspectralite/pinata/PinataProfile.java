package net.dafealru.pinataspectralite.pinata;

import net.dafealru.pinataspectralite.finale.DeathAnimationType;
import net.dafealru.pinataspectralite.loot.LootItem;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.ArrayList;
import java.util.List;

public class PinataProfile {

    private final String id;
    private final String displayName;
    private final int health;
    private final Material primaryBlock;
    private final Material secondaryBlock;
    private final List<Material> ribbonBlocks;
    private final Color glowColor;

    // Particles & FX customization
    private final Particle auraParticle;
    private final int auraParticleCount;
    private final double auraParticleSpeed;

    private final Particle hitParticle;
    private final int hitParticleCount;
    private final double hitParticleSpeed;

    private final Particle breakParticle;
    private final int breakParticleCount;
    private final double breakParticleSpeed;

    private final Particle trailParticle;
    private final int trailParticleCount;
    private final double trailParticleSpeed;

    // Audio customization
    private final Sound ambientSound;
    private final float ambientSoundVolume;
    private final float ambientSoundPitch;

    private final Sound hitSound;
    private final float hitSoundVolume;
    private final float hitSoundPitch;

    private final Sound breakSound;
    private final float breakSoundVolume;
    private final float breakSoundPitch;

    private final Sound deathSound;
    private final float deathSoundVolume;
    private final float deathSoundPitch;

    // Kinetics & Mechanics
    private final double teleportChance;
    private final int teleportIntervalSeconds;
    private final double movementSpeed;
    private final double deformationIntensity;
    private final float voxelScale;

    private DeathAnimationType deathAnimation = DeathAnimationType.FOUNTAIN_CASCADE;
    private PinataShapeType shapeType = PinataShapeType.STAR;
    private List<LootItem> customHitDrops = new ArrayList<>();
    private List<LootItem> customFinaleDrops = new ArrayList<>();

    public PinataProfile(String id,
                         String displayName,
                         int health,
                         Material primaryBlock,
                         Material secondaryBlock,
                         List<Material> ribbonBlocks,
                         Color glowColor,
                         Particle auraParticle,
                         int auraParticleCount,
                         double auraParticleSpeed,
                         Particle hitParticle,
                         int hitParticleCount,
                         double hitParticleSpeed,
                         Particle breakParticle,
                         int breakParticleCount,
                         double breakParticleSpeed,
                         Particle trailParticle,
                         int trailParticleCount,
                         double trailParticleSpeed,
                         Sound ambientSound,
                         float ambientSoundVolume,
                         float ambientSoundPitch,
                         Sound hitSound,
                         float hitSoundVolume,
                         float hitSoundPitch,
                         Sound breakSound,
                         float breakSoundVolume,
                         float breakSoundPitch,
                         Sound deathSound,
                         float deathSoundVolume,
                         float deathSoundPitch,
                         double teleportChance,
                         int teleportIntervalSeconds,
                         double movementSpeed,
                         double deformationIntensity,
                         float voxelScale) {
        this.id = id.toUpperCase();
        this.displayName = displayName;
        this.health = Math.max(1, health);
        this.primaryBlock = primaryBlock != null ? primaryBlock : Material.MAGENTA_CONCRETE;
        this.secondaryBlock = secondaryBlock != null ? secondaryBlock : Material.CYAN_CONCRETE;
        this.ribbonBlocks = (ribbonBlocks != null && !ribbonBlocks.isEmpty()) ? ribbonBlocks : List.of(Material.PINK_WOOL, Material.CYAN_WOOL, Material.YELLOW_WOOL);
        this.glowColor = glowColor != null ? glowColor : Color.fromRGB(255, 105, 180);

        this.auraParticle = auraParticle != null ? auraParticle : Particle.WAX_OFF;
        this.auraParticleCount = Math.max(1, auraParticleCount);
        this.auraParticleSpeed = Math.max(0.0, auraParticleSpeed);

        this.hitParticle = hitParticle != null ? hitParticle : Particle.FIREWORK;
        this.hitParticleCount = Math.max(1, hitParticleCount);
        this.hitParticleSpeed = Math.max(0.0, hitParticleSpeed);

        this.breakParticle = breakParticle != null ? breakParticle : Particle.CRIT;
        this.breakParticleCount = Math.max(1, breakParticleCount);
        this.breakParticleSpeed = Math.max(0.0, breakParticleSpeed);

        this.trailParticle = trailParticle != null ? trailParticle : Particle.CHERRY_LEAVES;
        this.trailParticleCount = Math.max(1, trailParticleCount);
        this.trailParticleSpeed = Math.max(0.0, trailParticleSpeed);

        this.ambientSound = ambientSound != null ? ambientSound : Sound.BLOCK_AMETHYST_BLOCK_CHIME;
        this.ambientSoundVolume = ambientSoundVolume > 0 ? ambientSoundVolume : 1.0f;
        this.ambientSoundPitch = ambientSoundPitch > 0 ? ambientSoundPitch : 1.0f;

        this.hitSound = hitSound != null ? hitSound : Sound.BLOCK_WOOD_HIT;
        this.hitSoundVolume = hitSoundVolume > 0 ? hitSoundVolume : 1.8f;
        this.hitSoundPitch = hitSoundPitch > 0 ? hitSoundPitch : 1.1f;

        this.breakSound = breakSound != null ? breakSound : Sound.BLOCK_WOOD_BREAK;
        this.breakSoundVolume = breakSoundVolume > 0 ? breakSoundVolume : 1.8f;
        this.breakSoundPitch = breakSoundPitch > 0 ? breakSoundPitch : 1.2f;

        this.deathSound = deathSound != null ? deathSound : Sound.UI_TOAST_CHALLENGE_COMPLETE;
        this.deathSoundVolume = deathSoundVolume > 0 ? deathSoundVolume : 1.5f;
        this.deathSoundPitch = deathSoundPitch > 0 ? deathSoundPitch : 1.0f;

        this.teleportChance = Math.max(0.0, Math.min(1.0, teleportChance));
        this.teleportIntervalSeconds = Math.max(1, teleportIntervalSeconds);
        this.movementSpeed = Math.max(0.1, movementSpeed);
        this.deformationIntensity = Math.max(0.1, deformationIntensity);
        this.voxelScale = Math.max(0.1f, voxelScale);
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public int getHealth() { return health; }
    public Material getPrimaryBlock() { return primaryBlock; }
    public Material getSecondaryBlock() { return secondaryBlock; }
    public List<Material> getRibbonBlocks() { return ribbonBlocks; }
    public Color getGlowColor() { return glowColor; }

    public Particle getAuraParticle() { return auraParticle; }
    public int getAuraParticleCount() { return auraParticleCount; }
    public double getAuraParticleSpeed() { return auraParticleSpeed; }

    public Particle getHitParticle() { return hitParticle; }
    public int getHitParticleCount() { return hitParticleCount; }
    public double getHitParticleSpeed() { return hitParticleSpeed; }

    public Particle getBreakParticle() { return breakParticle; }
    public int getBreakParticleCount() { return breakParticleCount; }
    public double getBreakParticleSpeed() { return breakParticleSpeed; }

    public Particle getTrailParticle() { return trailParticle; }
    public int getTrailParticleCount() { return trailParticleCount; }
    public double getTrailParticleSpeed() { return trailParticleSpeed; }

    public Sound getAmbientSound() { return ambientSound; }
    public float getAmbientSoundVolume() { return ambientSoundVolume; }
    public float getAmbientSoundPitch() { return ambientSoundPitch; }

    public Sound getHitSound() { return hitSound; }
    public float getHitSoundVolume() { return hitSoundVolume; }
    public float getHitSoundPitch() { return hitSoundPitch; }

    public Sound getBreakSound() { return breakSound; }
    public float getBreakSoundVolume() { return breakSoundVolume; }
    public float getBreakSoundPitch() { return breakSoundPitch; }

    public Sound getDeathSound() { return deathSound; }
    public float getDeathSoundVolume() { return deathSoundVolume; }
    public float getDeathSoundPitch() { return deathSoundPitch; }

    public double getTeleportChance() { return teleportChance; }
    public int getTeleportIntervalSeconds() { return teleportIntervalSeconds; }
    public double getMovementSpeed() { return movementSpeed; }
    public double getDeformationIntensity() { return deformationIntensity; }
    public float getVoxelScale() { return voxelScale; }

    public DeathAnimationType getDeathAnimation() { return deathAnimation; }
    public void setDeathAnimation(DeathAnimationType deathAnimation) { this.deathAnimation = deathAnimation; }
    public PinataShapeType getShapeType() { return shapeType; }
    public void setShapeType(PinataShapeType shapeType) { this.shapeType = shapeType; }

    public List<LootItem> getCustomHitDrops() { return customHitDrops; }
    public void setCustomHitDrops(List<LootItem> drops) { this.customHitDrops = drops != null ? drops : new ArrayList<>(); }
    public List<LootItem> getCustomFinaleDrops() { return customFinaleDrops; }
    public void setCustomFinaleDrops(List<LootItem> drops) { this.customFinaleDrops = drops != null ? drops : new ArrayList<>(); }
}
