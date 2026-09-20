# 🧊 03. 3D Voxel Models & Pendulum Physics

PinataSpectra Lite introduces a procedural rendering and kinematic simulation system that makes hitting a piñata feel responsive, dynamic, and festive.

---

## 🦄 3D Rendering via Native Display Entities

Unlike resource-pack-dependent models that require forced client downloads, PinataSpectra Lite utilizes native **Minecraft `ItemDisplay` entities**:

* **Base64 Custom Skulls:** Supports any custom head texture from community skin databases (`head-texture`).
* **Smooth Transformation Interpolation:** All rotations, scale shifts, and positions are synchronized using `Transformation` and tick interpolation (`setInterpolationDuration`), producing **60+ FPS motion with zero stutter**.
* **Precise Interaction Box:** An `Interaction` hitbox stays locked to the piñata's exact coordinates, guaranteeing clean hit registration.

---

## ⚡ Physics Simulation & Harmonic Sway

The math engine calculates multiple kinematic forces throughout the event:

1. **Gentle Idle Sway (Harmonic Pendulum):**
   * While waiting for players to attack, the piñata gently oscillates in a sinusoidal pattern:
     $$\Delta Y = A \cdot \sin(\omega t)$$
   * This simulates the natural elasticity of a suspended festive rope.

2. **Reactive Impact Recoil Vector:**
   * When struck by a bat or weapon, the engine calculates a displacement vector opposite to the attacker's line of sight:
     $$\vec{v}_{\text{recoil}} = \text{normalize}(\vec{P}_{\text{piñata}} - \vec{P}_{\text{player}}) \cdot K_{\text{force}}$$
   * The piñata tilts and swings in the air, absorbing momentum from the blow.

---

## 💥 Dynamic Fracture & Voxel Cavity Degradation Engine

PinataSpectra Lite features a **physics-driven degradation model** where the piñata physically cracks and breaks open as players deal damage:

```
  100% HP ──► Pristine 25-Voxel 3D Star
   75% HP ──► Star Tips Snap Off (Tier 3 Voxels) + Wooden Cracks
   50% HP ──► Outer Shell Fractures (Tier 2 Voxels) + Hollow Holes Form
   25% HP ──► Deep Exposed Cavities + Sweets & Paper Drifting Out
    0% HP ──► Climax Supernova Radial Detonation & Loot Burst
```

* **Physical Hole Formation:** As health declines, the 25 individual `BlockDisplay` voxels shatter sequentially from the outer star tips inward. Each shattered voxel is completely removed from the scene, exposing real physical gaps and cavities through the body of the piñata.
* **Crisp Cardboard & Wood Snapping Audio ("Chasquidos"):** Every fracture triggers layered snapping soundscapes (`BLOCK_WOOD_BREAK`, `BLOCK_BAMBOO_WOOD_BREAK`, `BLOCK_DECORATED_POT_SHATTER`, and `ENTITY_ITEM_BREAK`) with pitch modulation scaling up as the structure weakens.
* **Internal Sweet & Debris Leakage:** Once holes form, internal candy (`COOKIE`, `SUGAR`, `HONEYCOMB`) and paper flakes drift out of the hollow core between strikes.

---

## ⛰️ Smart Ground Clamping (`findSafeGroundY`)

A common issue in warping events is entities glitching underground or floating 20 blocks up when teleporting onto hilly terrain.

```
       [Suspended Piñata]
                │
                │  ← Fixed Base Height Offset (+2.35 blocks)
                ▼
  ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓ (Actual Terrain / Mountain / Stairs)
```

* The raycasting algorithm casts downward from the target position.
* It identifies the first real solid block (`isSolid()`), ignoring tall grass, flowers, and water.
* It locks the elevation at a safe margin of **+2.35 blocks**, ensuring players can always reach the piñata on foot regardless of arena topography.

---

<div align="center">

[**← 02. Installation**](02-Installation-and-Requirements.md) | [**04. Combat Phases & Boss Mechanics →**](04-Combat-Phases-and-Boss-Mechanics.md)

</div>
