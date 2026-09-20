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
import org.bukkit.block.data.BlockData;
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
 * Features real-time Display Entities, harmonic pendulum rotation,
 * dynamic realistic cracking & fracture degradation, hole cavity creation, and snapping SFX.
 */
public class PinataModel {

    public static final NamespacedKey PDC_KEY = new NamespacedKey("pinataspectralite", "pinata_display");

    private final PinataPartyLite plugin;
    private final Location baseLocation;
    private final PinataProfile profile;

    private static class VoxelData {
        final BlockDisplay display;
        final Vector unscaledOffset;
        Vector relativeOffset;
        float baseScale;
        final int tier; // 3 = star tips (break first), 2 = edges, 1 = faces, 0 = core center (remains last)
        final Material material;
        boolean shattered = false;

        VoxelData(BlockDisplay display, Vector offset, float baseScale, int tier, Material material) {
            this.display = display;
            this.unscaledOffset = offset.clone();
            this.relativeOffset = offset.clone();
            this.baseScale = baseScale;
            this.tier = tier;
            this.material = material;
        }
    }

    private final List<VoxelData> voxelList = new ArrayList<>();

    private Interaction hitbox;
    private TextDisplay nameTag;
    private TextDisplay infoTag;

    // Wobble physics
    private double wobblePitch = 0.0;
    private double wobbleRoll = 0.0;
    private double wobbleDecay = 0.88;
    private float currentScale = 1.0f;

    // Cached last transforms
    private Location lastCenter;
    private double lastYaw = 0.0;
    private double lastPitch = 0.0;
    private double lastRoll = 0.0;
    private int animTicks = 0;

    public PinataModel(PinataPartyLite plugin, Location baseLocation, PinataProfile profile) {
        this.plugin = plugin;
        this.baseLocation = baseLocation.clone();
        this.profile = profile;
        this.lastCenter = baseLocation.clone();
    }

    public void spawn() {
        if (baseLocation.getWorld() == null) return;

        // 1. Text Displays (Nametag & Health)
        nameTag = (TextDisplay) baseLocation.getWorld().spawnEntity(baseLocation.clone().add(0, 1.4, 0), EntityType.TEXT_DISPLAY);
        nameTag.getPersistentDataContainer().set(PDC_KEY, PersistentDataType.BYTE, (byte) 1);
        nameTag.setBillboard(Display.Billboard.CENTER);
        nameTag.setShadowed(true);
        nameTag.setText(ColorUtils.colorize(profile.getDisplayName()));

        infoTag = (TextDisplay) baseLocation.getWorld().spawnEntity(baseLocation.clone().add(0, 1.1, 0), EntityType.TEXT_DISPLAY);
        infoTag.getPersistentDataContainer().set(PDC_KEY, PersistentDataType.BYTE, (byte) 1);
        infoTag.setBillboard(Display.Billboard.CENTER);
        infoTag.setShadowed(true);
        infoTag.setText(ColorUtils.colorize("&e❤ &f" + profile.getHealth() + " / " + profile.getHealth() + " HP"));

        // 2. Physical Hitbox (Expanded for elevated piñata)
        hitbox = (Interaction) baseLocation.getWorld().spawnEntity(baseLocation.clone().add(0, -0.8, 0), EntityType.INTERACTION);
        hitbox.getPersistentDataContainer().set(PDC_KEY, PersistentDataType.BYTE, (byte) 1);
        hitbox.setInteractionWidth(2.0f);
        hitbox.setInteractionHeight(2.4f);

        // 3. Build Tiered 3D Voxel Star
        buildStarVoxelGrid();
    }

    private void buildStarVoxelGrid() {
        Material primary = profile.getPrimaryBlock();
        Material secondary = profile.getSecondaryBlock();
        List<Material> ribbons = profile.getRibbonBlocks();

        // 3x3x3 Core Voxels
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    int dist = Math.abs(x) + Math.abs(y) + Math.abs(z);
                    if (dist <= 2) {
                        Material mat = (dist == 0) ? secondary : primary;
                        int tier = (dist == 0) ? 0 : ((dist == 1) ? 1 : 2);
                        spawnVoxel(new Vector(x * 0.28, y * 0.28, z * 0.28), mat, 0.28f, tier);
                    }
                }
            }
        }

        // 6 Star Points (Up, Down, North, South, East, West) — Tier 3 (Break first with loud snaps)
        double pt = 0.56;
        spawnVoxel(new Vector(0, pt, 0), ribbons.get(0 % ribbons.size()), 0.24f, 3);
        spawnVoxel(new Vector(0, -pt, 0), ribbons.get(1 % ribbons.size()), 0.24f, 3);
        spawnVoxel(new Vector(pt, 0, 0), ribbons.get(2 % ribbons.size()), 0.24f, 3);
        spawnVoxel(new Vector(-pt, 0, 0), ribbons.get(0 % ribbons.size()), 0.24f, 3);
        spawnVoxel(new Vector(0, 0, pt), ribbons.get(1 % ribbons.size()), 0.24f, 3);
        spawnVoxel(new Vector(0, 0, -pt), ribbons.get(2 % ribbons.size()), 0.24f, 3);
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
     * Realistic Fracture & Hole Cavity Engine:
     * As the piñata takes damage, outer voxels crack, shatter with snapping sounds,
     * and break apart completely leaving realistic physical holes into the piñata body.
     */
    public void applyFractureAndHoles(int currentHealth, int maxHealth, Vector hitDirection, Location currentCenter) {
        if (voxelList.isEmpty() || currentCenter == null || currentCenter.getWorld() == null) return;

        double healthPct = Math.max(0.0, (double) currentHealth / maxHealth);
        int totalVoxels = voxelList.size();
        int targetIntact = Math.max(1, (int) Math.round(totalVoxels * healthPct));

        long intactCount = voxelList.stream().filter(v -> !v.shattered).count();

        World world = currentCenter.getWorld();

        while (intactCount > targetIntact) {
            // Find highest active tier (3 -> 2 -> 1)
            int highestTier = -1;
            for (VoxelData v : voxelList) {
                if (!v.shattered && v.tier > highestTier) {
                    highestTier = v.tier;
                }
            }

            if (highestTier <= 0) break; // Keep center core intact until death

            // Pick an intact voxel from the highest tier
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

            // Calculate precise world location of the shattering voxel
            Vector rotatedOffset = rotateVector(toShatter.relativeOffset, lastYaw, lastPitch, lastRoll);
            Location voxelLoc = currentCenter.clone().add(rotatedOffset);

            // 1. Crisp Wood/Cardboard Snapping Soundscape ("Chasquidos Realistas")
            float snapPitch = 1.35f + (float) (ThreadLocalRandom.current().nextDouble() * 0.35);
            world.playSound(voxelLoc, Sound.BLOCK_WOOD_BREAK, SoundCategory.PLAYERS, 1.8f, snapPitch);
            world.playSound(voxelLoc, Sound.BLOCK_BAMBOO_WOOD_BREAK, SoundCategory.PLAYERS, 1.6f, snapPitch + 0.2f);
            world.playSound(voxelLoc, Sound.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1.6f, 1.25f);
            world.playSound(voxelLoc, Sound.BLOCK_DECORATED_POT_SHATTER, SoundCategory.PLAYERS, 1.4f, 1.15f);
            world.playSound(voxelLoc, Sound.BLOCK_CHAIN_BREAK, SoundCategory.PLAYERS, 1.2f, 1.45f);

            // 2. Realistic Physical Shard Explosion & Confetti Particles
            world.spawnParticle(Particle.BLOCK, voxelLoc, 24, 0.2, 0.2, 0.2, 0.10, toShatter.material.createBlockData());
            world.spawnParticle(Particle.CRIT, voxelLoc, 12, 0.25, 0.25, 0.25, 0.15);
            world.spawnParticle(Particle.DUST, voxelLoc, 16, 0.25, 0.25, 0.25, new Particle.DustOptions(Color.fromRGB(255, 200, 50), 1.1f));

            // 3. Sweets & Candies Leaking from newly created Hole Cavity
            world.spawnParticle(Particle.ITEM, voxelLoc, 6, 0.15, 0.15, 0.15, 0.08, new ItemStack(Material.COOKIE));
            world.spawnParticle(Particle.ITEM, voxelLoc, 6, 0.15, 0.15, 0.15, 0.08, new ItemStack(Material.SUGAR));
            world.spawnParticle(Particle.ITEM, voxelLoc, 4, 0.15, 0.15, 0.15, 0.08, new ItemStack(Material.HONEYCOMB));

            // 4. Cleanly remove the BlockDisplay entity
            if (toShatter.display != null && toShatter.display.isValid()) {
                toShatter.display.remove();
            }

            // 5. Dynamic Recoil Impulse from fractured segment
            this.wobblePitch += (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.40;
            this.wobbleRoll += (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.40;
        }
    }

    public void setScaleAnimated(float targetScale, int durationTicks) {
        this.currentScale = targetScale;

        // Resize hitbox
        if (hitbox != null && hitbox.isValid()) {
            hitbox.setInteractionWidth(Math.max(0.6f, 1.6f * targetScale));
            hitbox.setInteractionHeight(Math.max(0.8f, 2.0f * targetScale));
        }

        // Rescale all 3D block displays
        for (VoxelData voxel : voxelList) {
            if (voxel.shattered || voxel.display == null || !voxel.display.isValid()) continue;

            float newScale = voxel.baseScale * targetScale;

            voxel.display.setInterpolationDuration(durationTicks);
            voxel.display.setTransformation(new Transformation(
                    new Vector3f(-newScale / 2.0f, -newScale / 2.0f, -newScale / 2.0f),
                    new AxisAngle4f(0, 0, 1, 0),
                    new Vector3f(newScale, newScale, newScale),
                    new AxisAngle4f(0, 0, 1, 0)
            ));

            // Update scaled offsets
            voxel.relativeOffset = voxel.unscaledOffset.clone().multiply(targetScale);
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
        // Elastic impulse based on player attack angle
        double impulse = 0.45;
        this.wobblePitch = -hitDirection.getZ() * impulse;
        this.wobbleRoll = hitDirection.getX() * impulse;
    }

    public void updatePosition(Location newCenter, double yaw, double pitch, double roll) {
        this.lastCenter = newCenter.clone();
        this.lastYaw = yaw;
        this.lastPitch = pitch;
        this.lastRoll = roll;
        this.animTicks++;

        // Decay wobble oscillation
        pitch += wobblePitch;
        roll += wobbleRoll;
        wobblePitch = -wobblePitch * wobbleDecay;
        wobbleRoll = -wobbleRoll * wobbleDecay;
        if (Math.abs(wobblePitch) < 0.01) wobblePitch = 0.0;
        if (Math.abs(wobbleRoll) < 0.01) wobbleRoll = 0.0;

        if (nameTag != null && nameTag.isValid()) {
            nameTag.teleport(newCenter.clone().add(0, 1.4, 0));
        }
        if (infoTag != null && infoTag.isValid()) {
            infoTag.teleport(newCenter.clone().add(0, 1.1, 0));
        }
        if (hitbox != null && hitbox.isValid()) {
            hitbox.teleport(newCenter.clone().add(0, -0.6, 0));
        }

        // Rotate only active (non-shattered) voxels around center
        for (VoxelData voxel : voxelList) {
            if (voxel.shattered || voxel.display == null || !voxel.display.isValid()) continue;

            Vector rotated = rotateVector(voxel.relativeOffset, yaw, pitch, roll);
            Location voxelLoc = newCenter.clone().add(rotated);

            voxel.display.teleport(voxelLoc);
            voxel.display.setInterpolationDuration(1);
        }

        // Ambient candy and paper particles drifting from exposed holes
        long shatteredCount = voxelList.stream().filter(v -> v.shattered).count();
        if (shatteredCount >= 4 && animTicks % 5 == 0 && newCenter.getWorld() != null) {
            World world = newCenter.getWorld();
            double ox = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.4;
            double oy = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.3;
            double oz = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.4;
            Location leakLoc = newCenter.clone().add(ox, oy, oz);

            if (ThreadLocalRandom.current().nextDouble() < 0.4) {
                world.spawnParticle(Particle.ITEM, leakLoc, 1, 0.02, -0.05, 0.02, 0.02, new ItemStack(Material.SUGAR));
            } else {
                world.spawnParticle(Particle.CHERRY_LEAVES, leakLoc, 1, 0.05, -0.04, 0.05, 0.01);
            }
        }

        // Render 3D Catenary Particle Hanging Rope
        renderHangingRope(newCenter);
    }

    private void renderHangingRope(Location piñataTop) {
        if (piñataTop.getWorld() == null) return;

        // Ceiling anchor point directly above base origin
        Location anchor = baseLocation.clone().add(0, 3.8, 0);
        Location topPoint = piñataTop.clone().add(0, 0.7, 0);

        int ropePoints = 14;
        org.bukkit.Particle.DustOptions ropeDust = new org.bukkit.Particle.DustOptions(Color.fromRGB(222, 184, 135), 0.75f);
        org.bukkit.Particle.DustOptions knotDust = new org.bukkit.Particle.DustOptions(Color.fromRGB(255, 215, 0), 1.0f);

        for (int i = 0; i <= ropePoints; i++) {
            double t = (double) i / ropePoints;
            // Linear interpolation
            double lx = anchor.getX() + (topPoint.getX() - anchor.getX()) * t;
            double lz = anchor.getZ() + (topPoint.getZ() - anchor.getZ()) * t;
            double ly = anchor.getY() + (topPoint.getY() - anchor.getY()) * t;

            // Catenary sag / curve math (parabolic sag in middle)
            double sag = Math.sin(t * Math.PI) * 0.12;
            Location ropeLoc = new Location(anchor.getWorld(), lx, ly - sag, lz);

            if (i == ropePoints) {
                // Golden ribbon knot on piñata top
                anchor.getWorld().spawnParticle(org.bukkit.Particle.DUST, ropeLoc, 1, 0, 0, 0, knotDust);
            } else {
                anchor.getWorld().spawnParticle(org.bukkit.Particle.DUST, ropeLoc, 1, 0, 0, 0, ropeDust);
            }
        }
    }

    public void updateHealthDisplay(int current, int max) {
        if (infoTag != null && infoTag.isValid()) {
            double pct = (double) current / max;
            String col = (pct > 0.6) ? "&a" : (pct > 0.3 ? "&e" : "&c");
            infoTag.setText(ColorUtils.colorize("&e❤ " + col + current + " &7/ &f" + max + " HP"));
        }
    }

    private Vector rotateVector(Vector v, double yaw, double pitch, double roll) {
        double x = v.getX();
        double y = v.getY();
        double z = v.getZ();

        // Yaw (Y axis)
        double cosY = Math.cos(yaw);
        double sinY = Math.sin(yaw);
        double x1 = x * cosY - z * sinY;
        double z1 = x * sinY + z * cosY;

        // Pitch (X axis)
        double cosP = Math.cos(pitch);
        double sinP = Math.sin(pitch);
        double y2 = y * cosP - z1 * sinP;
        double z2 = y * sinP + z1 * cosP;

        // Roll (Z axis)
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
