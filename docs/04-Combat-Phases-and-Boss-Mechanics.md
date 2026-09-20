# ⚔️ 04. Combat Phases & Boss Mechanics

PinataSpectra Lite is far more than a passive target. It features an active **combat state machine** that evolves dynamically as the piñata takes damage, accompanied by advanced Minecraft SFX and adaptive NoteBlock songs.

---

## 🎭 The 3 Dynamic Combat Phases

```
 100% HP ─────────────── 66% HP ─────────────── 33% HP ─────────────── 0% HP
    │                       │                       │                     │
    ▼                       ▼                       ▼                     ▼
 [ PHASE 1: CARNIVAL ]   [ PHASE 2: EVASION ]    [ PHASE 3: WARP SHIFT ] [ GRAND CLIMAX ]
  • Festive Melody        • Shrinks to 65% scale  • Dimensional leaps    • MVP Podium
  • Stable floating       • Double move speed     • Sonic pulse waves    • Candy shower
  • Gold chime sparks     • Electric sparks       • Amethyst resonance   • Explosion FX
```

---

### 🪅 Phase 1: Festive Carnival March (100% - 66% HP)
* **Behavior:** The piñata floats in place with gentle harmonic swaying.
* **NoteBlock Song:** Upbeat traditional carnival melody (*Flute* and *Bell* instruments).
* **Effects:** Gold sparkles and floating musical notes on hit.

---

### ⚡ Phase 2: Micro-Evasion Mode (66% - 33% HP)
* **Trigger:** Activates once health drops below 66%.
* **Mechanics:**
  * The piñata shrinks to **65% of its original size**.
  * Movement speed and momentum double, making rapid combo hits more challenging.
* **NoteBlock Song:** Fast-paced energetic soundtrack (*Xylophone* and *Pitched Bass*).
* **Effects:** Electric sparks (`ELECTRIC_SPARK`), beacon activation sound, and server-wide broadcast.

---

### 🌀 Phase 3: Chaotic Warp Shifter (33% - 0% HP)
* **Trigger:** Activates once health drops below 33%.
* **Mechanics:**
  * **Acrobatic Warping:** Periodically teleports in a 12-block radius around the arena.
  * **Coordinate Broadcasts:** Announces its new `X, Y, Z` coordinates in chat and ActionBar for players to chase down.
  * **Terrain Safety Clamping:** Immediately recalculates safe ground height (`findSafeGroundY`) upon landing.
* **NoteBlock Song:** High-tension climax symphony (*Chime*, *Guitar*, and *Amethyst Resonance*).
* **Effects:** Sonic shockwaves, respawn anchor distortion (`RESPAWN_ANCHOR_CHARGE`), and portal particles.

---

## 🎶 Dynamic Positional Soundscape

PinataSpectra Lite uses native Minecraft sound packets broadcasted in 3D positional audio (`player.playSound`), ensuring all players hear the music and effects naturally within the event radius.

---

<div align="center">

[**← 03. 3D Voxel Models**](03-3D-Voxel-Models-and-Physics.md) | [**05. Creating Custom Piñatas →**](05-Creating-Custom-Pinatas.md)

</div>
