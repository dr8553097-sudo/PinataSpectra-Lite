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
