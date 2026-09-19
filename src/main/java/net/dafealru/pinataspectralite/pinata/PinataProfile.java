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
    private final Particle auraParticle;
    private final Particle hitParticle;
    private final Particle trailParticle;
    private final Sound ambientSound;
    private final Sound hitSound;
    private final Sound deathSound;
    private final double teleportChance;
    private final int teleportIntervalSeconds;
    private final double movementSpeed;
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
                         Particle hitParticle,
                         Particle trailParticle,
                         Sound ambientSound,
                         Sound hitSound,
                         Sound deathSound,
                         double teleportChance,
                         int teleportIntervalSeconds,
                         double movementSpeed) {
        this.id = id.toUpperCase();
        this.displayName = displayName;
        this.health = Math.max(1, health);
        this.primaryBlock = primaryBlock != null ? primaryBlock : Material.MAGENTA_CONCRETE;
        this.secondaryBlock = secondaryBlock != null ? secondaryBlock : Material.CYAN_CONCRETE;
        this.ribbonBlocks = (ribbonBlocks != null && !ribbonBlocks.isEmpty()) ? ribbonBlocks : List.of(Material.PINK_WOOL, Material.CYAN_WOOL, Material.YELLOW_WOOL);
        this.glowColor = glowColor != null ? glowColor : Color.fromRGB(255, 105, 180);
        this.auraParticle = auraParticle != null ? auraParticle : Particle.WAX_OFF;
        this.hitParticle = hitParticle != null ? hitParticle : Particle.FIREWORK;
        this.trailParticle = trailParticle != null ? trailParticle : Particle.CHERRY_LEAVES;
        this.ambientSound = ambientSound != null ? ambientSound : Sound.ENTITY_LLAMA_AMBIENT;
        this.hitSound = hitSound != null ? hitSound : Sound.BLOCK_WOOD_HIT;
        this.deathSound = deathSound != null ? deathSound : Sound.UI_TOAST_CHALLENGE_COMPLETE;
        this.teleportChance = Math.max(0.0, Math.min(1.0, teleportChance));
        this.teleportIntervalSeconds = Math.max(3, teleportIntervalSeconds);
        this.movementSpeed = Math.max(0.1, movementSpeed);
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public int getHealth() { return health; }
    public Material getPrimaryBlock() { return primaryBlock; }
    public Material getSecondaryBlock() { return secondaryBlock; }
    public List<Material> getRibbonBlocks() { return ribbonBlocks; }
    public Color getGlowColor() { return glowColor; }
    public Particle getAuraParticle() { return auraParticle; }
    public Particle getHitParticle() { return hitParticle; }
    public Particle getTrailParticle() { return trailParticle; }
    public Sound getAmbientSound() { return ambientSound; }
    public Sound getHitSound() { return hitSound; }
    public Sound getDeathSound() { return deathSound; }
    public double getTeleportChance() { return teleportChance; }
    public int getTeleportIntervalSeconds() { return teleportIntervalSeconds; }
    public double getMovementSpeed() { return movementSpeed; }
    public DeathAnimationType getDeathAnimation() { return deathAnimation; }
    public void setDeathAnimation(DeathAnimationType deathAnimation) { this.deathAnimation = deathAnimation; }
    public PinataShapeType getShapeType() { return shapeType; }
    public void setShapeType(PinataShapeType shapeType) { this.shapeType = shapeType; }

    public List<LootItem> getCustomHitDrops() { return customHitDrops; }
    public void setCustomHitDrops(List<LootItem> drops) { this.customHitDrops = drops != null ? drops : new ArrayList<>(); }
    public List<LootItem> getCustomFinaleDrops() { return customFinaleDrops; }
    public void setCustomFinaleDrops(List<LootItem> drops) { this.customFinaleDrops = drops != null ? drops : new ArrayList<>(); }
}
