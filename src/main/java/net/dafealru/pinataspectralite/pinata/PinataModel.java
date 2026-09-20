package net.dafealru.pinataspectralite.pinata;

import net.dafealru.pinataspectralite.PinataPartyLite;
import net.dafealru.pinataspectralite.utils.ColorUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Procedural 3D Voxel Model Engine for PinataSpectra Lite.
 * Features realistic physical cracking, hollow cavity exposure, angular cardboard deformation,
 * progressive voxel shattering, and multi-layer snapping soundscapes.
 */
public class PinataModel {

    public static final NamespacedKey PDC_KEY = new NamespacedKey("pinataspectralite", "pinata_display");

    private final PinataPartyLite plugin;
    private final Location baseLocation;
    private final PinataProfile profile;

    public static class VoxelData {
        final BlockDisplay display;
        final Vector unscaledOffset;
        Vector currentOffset;
        float baseScale;
        final int tier; // 3 = Star tips (shatter first), 2 = Outer edges, 1 = Core faces, 0 = Inner heart
        final Material material;
        boolean shattered = false;
        float deformYaw = 0f;
        float deformPitch = 0f;
        float deformRoll = 0f;

        VoxelData(BlockDisplay display, Vector offset, float baseScale, int tier, Material material) {
            this.display = display;
            this.unscaledOffset = offset.clone();
            this.currentOffset = offset.clone();
            this.baseScale = baseScale;
            this.tier = tier;
            this.material = material;
        }
    }

    private final List<VoxelData> voxelList = new ArrayList<>();

    private Interaction hitbox;
    private TextDisplay nameTag;
    private TextDisplay infoTag;

    // Kinematic Wobble physics
    private double wobblePitch = 0.0;
    private double wobbleRoll = 0.0;
    private double wobbleDecay = 0.88;
    private float currentScale = 1.0f;

    // Cached transforms
    private Location lastCenter;
    private double lastYaw = 0.0;
    private double lastPitch = 0.0;
    private double lastRoll = 0.0;
    private int animTicks = 0;
    private double currentDamagePct = 0.0;

    public PinataModel(PinataPartyLite plugin, Location baseLocation, PinataProfile profile) {
        this.plugin = plugin;
        this.baseLocation = baseLocation.clone();
        this.profile = profile;
        this.lastCenter = baseLocation.clone();
    }

    public void spawn() {
        if (baseLocation.getWorld() == null) return;

        // 1. Text Displays (Nametag & Real-Time Percentage Health Hologram)
        nameTag = (TextDisplay) baseLocation.getWorld().spawnEntity(baseLocation.clone().add(0, 1.65, 0), EntityType.TEXT_DISPLAY);
        nameTag.getPersistentDataContainer().set(PDC_KEY, PersistentDataType.BYTE, (byte) 1);
        nameTag.setBillboard(Display.Billboard.CENTER);
        nameTag.setShadowed(true);
        nameTag.setText(ColorUtils.colorize(profile.getDisplayName()));

        infoTag = (TextDisplay) baseLocation.getWorld().spawnEntity(baseLocation.clone().add(0, 1.35, 0), EntityType.TEXT_DISPLAY);
        infoTag.getPersistentDataContainer().set(PDC_KEY, PersistentDataType.BYTE, (byte) 1);
        infoTag.setBillboard(Display.Billboard.CENTER);
        infoTag.setShadowed(true);
        infoTag.setText(ColorUtils.colorize("<gradient:#22C55E:#38BDF8><bold>❤ 100%</bold></gradient> <dark_gray>•</dark_gray> <white>" + profile.getHealth() + " HP</white>"));

        // 2. Physical Hitbox
        hitbox = (Interaction) baseLocation.getWorld().spawnEntity(baseLocation.clone().add(0, -0.6, 0), EntityType.INTERACTION);
        hitbox.getPersistentDataContainer().set(PDC_KEY, PersistentDataType.BYTE, (byte) 1);
        hitbox.setInteractionWidth(2.2f);
        hitbox.setInteractionHeight(2.6f);

        // 3. Build Tiered 3D Voxel Star
        buildStarVoxelGrid();
    }

    private void buildStarVoxelGrid() {
        Material primary = profile.getPrimaryBlock();
        Material secondary = profile.getSecondaryBlock();
        List<Material> ribbons = profile.getRibbonBlocks();

        double spacing = 0.36;
        float voxelSize = 0.36f;

        // Core 3x3x3 Voxels
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    int dist = Math.abs(x) + Math.abs(y) + Math.abs(z);
                    if (dist <= 2) {
                        Material mat = (dist == 0) ? secondary : primary;
                        int tier = (dist == 0) ? 0 : ((dist == 1) ? 1 : 2);
                        spawnVoxel(new Vector(x * spacing, y * spacing, z * spacing), mat, voxelSize, tier);
                    }
                }
            }
        }

        // 6 Star Points (Up, Down, North, South, East, West) — Tier 3 (Shatter first)
        double pt = 0.72;
        float tipSize = 0.32f;
        spawnVoxel(new Vector(0, pt, 0), ribbons.get(0 % ribbons.size()), tipSize, 3);
        spawnVoxel(new Vector(0, -pt, 0), ribbons.get(1 % ribbons.size()), tipSize, 3);
        spawnVoxel(new Vector(pt, 0, 0), ribbons.get(2 % ribbons.size()), tipSize, 3);
        spawnVoxel(new Vector(-pt, 0, 0), ribbons.get(0 % ribbons.size()), tipSize, 3);
        spawnVoxel(new Vector(0, 0, pt), ribbons.get(1 % ribbons.size()), tipSize, 3);
        spawnVoxel(new Vector(0, 0, -pt), ribbons.get(2 % ribbons.size()), tipSize, 3);
    }

    private void spawnVoxel(Vector offset, Material material, float scale, int tier) {
        if (baseLocation.getWorld() == null) return;
        Location loc = baseLocation.clone().add(offset);
        BlockDisplay bd = (BlockDisplay) baseLocation.getWorld().spawnEntity(loc, EntityType.BLOCK_DISPLAY);
        bd.getPersistentDataContainer().set(PDC_KEY, PersistentDataType.BYTE, (byte) 1);
        bd.setBlock(material.createBlockData());
        bd.setGlowing(true);

        bd.setTransformation(new Transformation(
                new Vector3f(-scale / 2.0f, -scale / 2.0f, -scale / 2.0f),
                new AxisAngle4f(0, 0, 1, 0),
                new Vector3f(scale, scale, scale),
                new AxisAngle4f(0, 0, 1, 0)
        ));

        voxelList.add(new VoxelData(bd, offset, scale, tier, material));
    }

    /**
     * Realistic Dynamic Fracture & Cavity Deformation Engine:
     * - Breaks off outer voxels progressively (Tier 3 -> Tier 2 -> Tier 1) opening gaping holes.
     * - Deforms, tilts, and stretches remaining intact voxels to simulate crumbling, torn cardboard.
     * - Spawns loud snapping audio layers and bursting debris.
     */
    public void applyFractureAndHoles(int currentHealth, int maxHealth, Vector hitDirection, Location currentCenter) {
        if (voxelList.isEmpty() || currentCenter == null || currentCenter.getWorld() == null) return;

        double healthPct = Math.max(0.0, (double) currentHealth / maxHealth);
        this.currentDamagePct = 1.0 - healthPct;

        int totalVoxels = voxelList.size();
        int targetIntact = Math.max(1, (int) Math.round(totalVoxels * healthPct));

        long intactCount = voxelList.stream().filter(v -> !v.shattered).count();
        World world = currentCenter.getWorld();

        // 1. Shatter outer voxels to create physical holes
        while (intactCount > targetIntact) {
            int highestTier = -1;
            for (VoxelData v : voxelList) {
                if (!v.shattered && v.tier > highestTier) {
                    highestTier = v.tier;
                }
            }

            if (highestTier <= 0) break; // Keep center core intact until death

            List<VoxelData> candidates = new ArrayList<>();
            for (VoxelData v : voxelList) {
                if (!v.shattered && v.tier == highestTier) {
                    candidates.add(v);
                }
            }

            if (candidates.isEmpty()) break;

            Collections.shuffle(candidates);
            VoxelData toShatter = candidates.get(0);
            toShatter.shattered = true;
            intactCount--;

            Vector rotatedOffset = rotateVector(toShatter.currentOffset, lastYaw, lastPitch, lastRoll);
            Location voxelLoc = currentCenter.clone().add(rotatedOffset);

            // Loud Multi-Layer Cardboard/Wood Snapping Soundscape ("Chasquidos")
            float snapPitch = 1.30f + (float) (ThreadLocalRandom.current().nextDouble() * 0.40);
            world.playSound(voxelLoc, Sound.BLOCK_WOOD_BREAK, SoundCategory.PLAYERS, 2.0f, snapPitch);
            world.playSound(voxelLoc, Sound.BLOCK_BAMBOO_WOOD_BREAK, SoundCategory.PLAYERS, 1.8f, snapPitch + 0.2f);
            world.playSound(voxelLoc, Sound.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1.8f, 1.25f);
            world.playSound(voxelLoc, Sound.BLOCK_DECORATED_POT_SHATTER, SoundCategory.PLAYERS, 1.6f, 1.15f);
            world.playSound(voxelLoc, Sound.BLOCK_CHAIN_BREAK, SoundCategory.PLAYERS, 1.4f, 1.50f);

            // Real Block Break Debris Burst
            world.spawnParticle(Particle.BLOCK, voxelLoc, 30, 0.25, 0.25, 0.25, 0.12, toShatter.material.createBlockData());
            world.spawnParticle(Particle.CRIT, voxelLoc, 15, 0.3, 0.3, 0.3, 0.18);
            world.spawnParticle(Particle.DUST, voxelLoc, 20, 0.3, 0.3, 0.3, new Particle.DustOptions(Color.fromRGB(255, 200, 50), 1.2f));

            // Internal Sweets & Candy Leaking from new Hole Cavity
            world.spawnParticle(Particle.ITEM, voxelLoc, 8, 0.2, 0.2, 0.2, 0.10, new ItemStack(Material.COOKIE));
            world.spawnParticle(Particle.ITEM, voxelLoc, 8, 0.2, 0.2, 0.2, 0.10, new ItemStack(Material.SUGAR));
            world.spawnParticle(Particle.ITEM, voxelLoc, 5, 0.2, 0.2, 0.2, 0.10, new ItemStack(Material.HONEYCOMB));

            if (toShatter.display != null && toShatter.display.isValid()) {
                toShatter.display.remove();
            }

            this.wobblePitch += (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.50;
            this.wobbleRoll += (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.50;
        }

        // 2. Realistic Angular Deformation & Warping on remaining intact voxels
        float deformFactor = (float) currentDamagePct;
        for (VoxelData v : voxelList) {
            if (v.shattered || v.display == null || !v.display.isValid()) continue;

            // Give each intact voxel an outward bend / sag / tilt proportional to damage
            v.deformYaw = (float) Math.sin(v.unscaledOffset.getX() * 5 + animTicks) * deformFactor * 0.40f;
            v.deformPitch = (float) Math.cos(v.unscaledOffset.getY() * 5 + animTicks) * deformFactor * 0.40f;
            v.deformRoll = (float) Math.sin(v.unscaledOffset.getZ() * 5 + animTicks) * deformFactor * 0.40f;

            // Outward bulge vector (gaping opening)
            double bulge = 1.0 + (deformFactor * 0.35);
            v.currentOffset = v.unscaledOffset.clone().multiply(bulge * currentScale);

            // Apply angular transformation to BlockDisplay
            float scale = v.baseScale * currentScale;
            v.display.setInterpolationDuration(2);
            v.display.setTransformation(new Transformation(
                    new Vector3f(-scale / 2.0f, -scale / 2.0f, -scale / 2.0f),
                    new AxisAngle4f(v.deformPitch, 1, 0, 0),
                    new Vector3f(scale, scale, scale),
                    new AxisAngle4f(v.deformRoll, 0, 0, 1)
            ));
        }
    }

    public void setScaleAnimated(float targetScale, int durationTicks) {
        this.currentScale = targetScale;

        if (hitbox != null && hitbox.isValid()) {
            hitbox.setInteractionWidth(Math.max(0.6f, 1.8f * targetScale));
            hitbox.setInteractionHeight(Math.max(0.8f, 2.2f * targetScale));
        }

        for (VoxelData voxel : voxelList) {
            if (voxel.shattered || voxel.display == null || !voxel.display.isValid()) continue;

            float newScale = voxel.baseScale * targetScale;
            voxel.display.setInterpolationDuration(durationTicks);
            voxel.display.setTransformation(new Transformation(
                    new Vector3f(-newScale / 2.0f, -newScale / 2.0f, -newScale / 2.0f),
                    new AxisAngle4f(voxel.deformPitch, 1, 0, 0),
                    new Vector3f(newScale, newScale, newScale),
                    new AxisAngle4f(voxel.deformRoll, 0, 0, 1)
            ));

            voxel.currentOffset = voxel.unscaledOffset.clone().multiply(targetScale * (1.0 + currentDamagePct * 0.35));
        }
    }

    public void setBaseLocation(Location newBase) {
        if (newBase != null && newBase.getWorld() != null) {
            this.baseLocation.setWorld(newBase.getWorld());
            this.baseLocation.setX(newBase.getX());
            this.baseLocation.setY(newBase.getY());
            this.baseLocation.setZ(newBase.getZ());
        }
    }

    public float getCurrentScale() { return currentScale; }

    public void applyHitWobble(Vector hitDirection) {
        double impulse = 0.50;
        this.wobblePitch = -hitDirection.getZ() * impulse;
        this.wobbleRoll = hitDirection.getX() * impulse;
    }

    public void updatePosition(Location newCenter, double yaw, double pitch, double roll) {
        this.lastCenter = newCenter.clone();
        this.lastYaw = yaw;
        this.lastPitch = pitch;
        this.lastRoll = roll;
        this.animTicks++;

        pitch += wobblePitch;
        roll += wobbleRoll;
        wobblePitch = -wobblePitch * wobbleDecay;
        wobbleRoll = -wobbleRoll * wobbleDecay;
        if (Math.abs(wobblePitch) < 0.01) wobblePitch = 0.0;
        if (Math.abs(wobbleRoll) < 0.01) wobbleRoll = 0.0;

        if (nameTag != null && nameTag.isValid()) {
            nameTag.teleport(newCenter.clone().add(0, 1.65, 0));
        }
        if (infoTag != null && infoTag.isValid()) {
            infoTag.teleport(newCenter.clone().add(0, 1.35, 0));
        }
        if (hitbox != null && hitbox.isValid()) {
            hitbox.teleport(newCenter.clone().add(0, -0.6, 0));
        }

        // Rotate and teleport only active intact voxels
        for (VoxelData voxel : voxelList) {
            if (voxel.shattered || voxel.display == null || !voxel.display.isValid()) continue;

            Vector rotated = rotateVector(voxel.currentOffset, yaw, pitch, roll);
            Location voxelLoc = newCenter.clone().add(rotated);

            voxel.display.teleport(voxelLoc);
            voxel.display.setInterpolationDuration(1);
        }

        // Ambient sweets & confetti continuously drifting out of exposed cavities
        if (currentDamagePct >= 0.25 && animTicks % 4 == 0 && newCenter.getWorld() != null) {
            World world = newCenter.getWorld();
            double ox = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.6;
            double oy = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.4;
            double oz = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.6;
            Location leakLoc = newCenter.clone().add(ox, oy, oz);

            if (ThreadLocalRandom.current().nextDouble() < 0.45) {
                world.spawnParticle(Particle.ITEM, leakLoc, 1, 0.03, -0.06, 0.03, 0.02, new ItemStack(Material.SUGAR));
            } else if (ThreadLocalRandom.current().nextDouble() < 0.30) {
                world.spawnParticle(Particle.ITEM, leakLoc, 1, 0.03, -0.06, 0.03, 0.02, new ItemStack(Material.COOKIE));
            } else {
                world.spawnParticle(Particle.CHERRY_LEAVES, leakLoc, 1, 0.05, -0.04, 0.05, 0.01);
            }
        }

        // Render 3D Catenary Particle Hanging Rope
        renderHangingRope(newCenter);
    }

    private void renderHangingRope(Location piñataTop) {
        if (piñataTop.getWorld() == null) return;

        Location anchor = baseLocation.clone().add(0, 4.0, 0);
        Location topPoint = piñataTop.clone().add(0, 0.85, 0);

        int ropePoints = 14;
        org.bukkit.Particle.DustOptions ropeDust = new org.bukkit.Particle.DustOptions(Color.fromRGB(222, 184, 135), 0.75f);
        org.bukkit.Particle.DustOptions knotDust = new org.bukkit.Particle.DustOptions(Color.fromRGB(255, 215, 0), 1.0f);

        for (int i = 0; i <= ropePoints; i++) {
            double t = (double) i / ropePoints;
            double lx = anchor.getX() + (topPoint.getX() - anchor.getX()) * t;
            double lz = anchor.getZ() + (topPoint.getZ() - anchor.getZ()) * t;
            double ly = anchor.getY() + (topPoint.getY() - anchor.getY()) * t;

            double sag = Math.sin(t * Math.PI) * 0.12;
            Location ropeLoc = new Location(anchor.getWorld(), lx, ly - sag, lz);

            if (i == ropePoints) {
                anchor.getWorld().spawnParticle(org.bukkit.Particle.DUST, ropeLoc, 1, 0, 0, 0, knotDust);
            } else {
                anchor.getWorld().spawnParticle(org.bukkit.Particle.DUST, ropeLoc, 1, 0, 0, 0, ropeDust);
            }
        }
    }

    public void updateHealthDisplay(int current, int max) {
        if (infoTag != null && infoTag.isValid()) {
            int pct = (int) Math.ceil(((double) current / max) * 100);
            String col = (pct > 60) ? "<gradient:#22C55E:#38BDF8><bold>" : (pct > 30 ? "<gradient:#FCD34D:#F59E0B><bold>" : "<gradient:#EF4444:#DC2626><bold>");
            infoTag.setText(ColorUtils.colorize(col + "❤ " + pct + "%</bold></gradient> <dark_gray>•</dark_gray> <white>" + current + " / " + max + " HP</white>"));
        }
    }

    private Vector rotateVector(Vector v, double yaw, double pitch, double roll) {
        double x = v.getX();
        double y = v.getY();
        double z = v.getZ();

        double cosY = Math.cos(yaw);
        double sinY = Math.sin(yaw);
        double x1 = x * cosY - z * sinY;
        double z1 = x * sinY + z * cosY;

        double cosP = Math.cos(pitch);
        double sinP = Math.sin(pitch);
        double y2 = y * cosP - z1 * sinP;
        double z2 = y * sinP + z1 * cosP;

        double cosR = Math.cos(roll);
        double sinR = Math.sin(roll);
        double x3 = x1 * cosR - y2 * sinR;
        double y3 = x1 * sinR + y2 * cosR;

        return new Vector(x3, y3, z2);
    }

    public void destroy() {
        for (VoxelData voxel : voxelList) {
            if (voxel.display != null && voxel.display.isValid()) {
                voxel.display.remove();
            }
        }
        voxelList.clear();

        if (nameTag != null && nameTag.isValid()) nameTag.remove();
        if (infoTag != null && infoTag.isValid()) infoTag.remove();
        if (hitbox != null && hitbox.isValid()) hitbox.remove();
    }

    public Interaction getHitbox() { return hitbox; }
    public Location getBaseLocation() { return baseLocation; }
}
