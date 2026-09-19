package net.dafealru.pinataspectralite.pinata;

import net.dafealru.pinataspectralite.PinataPartyLite;
import net.dafealru.pinataspectralite.utils.ColorUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class PinataModel {

    public static final NamespacedKey PDC_KEY = new NamespacedKey("pinataspectralite", "pinata_display");

    private final PinataPartyLite plugin;
    private final Location baseLocation;
    private final PinataProfile profile;
    private final List<BlockDisplay> voxelBlocks = new ArrayList<>();
    private final List<Vector> relativeOffsets = new ArrayList<>();

    private Interaction hitbox;
    private TextDisplay nameTag;
    private TextDisplay infoTag;

    // Wobble physics
    private double wobblePitch = 0.0;
    private double wobbleRoll = 0.0;
    private double wobbleDecay = 0.88;
    private float currentScale = 1.0f;
    private final List<Float> baseVoxelScales = new ArrayList<>();
    private final List<Vector> unscaledOffsets = new ArrayList<>();

    public PinataModel(PinataPartyLite plugin, Location baseLocation, PinataProfile profile) {
        this.plugin = plugin;
        this.baseLocation = baseLocation.clone();
        this.profile = profile;
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

        // 3. Build 3D Voxel Star
        buildStarVoxelGrid();
    }

    private void buildStarVoxelGrid() {
        Material primary = profile.getPrimaryBlock();
        Material secondary = profile.getSecondaryBlock();
        List<Material> ribbons = profile.getRibbonBlocks();

        // Core 3x3x3 voxels
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    int dist = Math.abs(x) + Math.abs(y) + Math.abs(z);
                    if (dist <= 2) {
                        Material mat = (dist == 0) ? secondary : primary;
                        spawnVoxel(new Vector(x * 0.28, y * 0.28, z * 0.28), mat, 0.28f);
                    }
                }
            }
        }

        // 6 Star Points (Up, Down, North, South, East, West)
        double pt = 0.56;
        spawnVoxel(new Vector(0, pt, 0), ribbons.get(0 % ribbons.size()), 0.24f);
        spawnVoxel(new Vector(0, -pt, 0), ribbons.get(1 % ribbons.size()), 0.24f);
        spawnVoxel(new Vector(pt, 0, 0), ribbons.get(2 % ribbons.size()), 0.24f);
        spawnVoxel(new Vector(-pt, 0, 0), ribbons.get(0 % ribbons.size()), 0.24f);
        spawnVoxel(new Vector(0, 0, pt), ribbons.get(1 % ribbons.size()), 0.24f);
        spawnVoxel(new Vector(0, 0, -pt), ribbons.get(2 % ribbons.size()), 0.24f);
    }

    private void spawnVoxel(Vector offset, Material material, float scale) {
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

        voxelBlocks.add(bd);
        relativeOffsets.add(offset.clone());
        unscaledOffsets.add(offset.clone());
        baseVoxelScales.add(scale);
    }

    public void setScaleAnimated(float targetScale, int durationTicks) {
        this.currentScale = targetScale;

        // Resize hitbox
        if (hitbox != null && hitbox.isValid()) {
            hitbox.setInteractionWidth(Math.max(0.6f, 1.6f * targetScale));
            hitbox.setInteractionHeight(Math.max(0.8f, 2.0f * targetScale));
        }

        // Rescale all 3D block displays
        for (int i = 0; i < voxelBlocks.size(); i++) {
            BlockDisplay bd = voxelBlocks.get(i);
            if (!bd.isValid()) continue;

            float baseScale = baseVoxelScales.get(i);
            float newScale = baseScale * targetScale;

            bd.setInterpolationDuration(durationTicks);
            bd.setTransformation(new Transformation(
                    new Vector3f(-newScale / 2.0f, -newScale / 2.0f, -newScale / 2.0f),
                    new AxisAngle4f(0, 0, 1, 0),
                    new Vector3f(newScale, newScale, newScale),
                    new AxisAngle4f(0, 0, 1, 0)
            ));

            // Update scaled offsets
            Vector unscaled = unscaledOffsets.get(i);
            relativeOffsets.set(i, unscaled.clone().multiply(targetScale));
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

        // Rotate voxel relative offsets around center
        for (int i = 0; i < voxelBlocks.size(); i++) {
            BlockDisplay bd = voxelBlocks.get(i);
            if (!bd.isValid()) continue;

            Vector orig = relativeOffsets.get(i);
            Vector rotated = rotateVector(orig, yaw, pitch, roll);
            Location voxelLoc = newCenter.clone().add(rotated);

            bd.teleport(voxelLoc);
            bd.setInterpolationDuration(1);
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
        for (BlockDisplay bd : voxelBlocks) {
            if (bd != null && bd.isValid()) bd.remove();
        }
        voxelBlocks.clear();
        relativeOffsets.clear();

        if (nameTag != null && nameTag.isValid()) nameTag.remove();
        if (infoTag != null && infoTag.isValid()) infoTag.remove();
        if (hitbox != null && hitbox.isValid()) hitbox.remove();
    }

    public Interaction getHitbox() { return hitbox; }
    public Location getBaseLocation() { return baseLocation; }
}
