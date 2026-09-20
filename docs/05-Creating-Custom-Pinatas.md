# 🛠️ 05. Guide: Creating Custom Piñata Profiles

In **PinataSpectra Lite**, each piñata profile is stored in its own clean YAML file inside the `plugins/PinataSpectra-Lite/pinatas/` directory.

This modular structure lets you design multiple event types (e.g. Halloween, Christmas, Mining Party, Community Milestone) with dedicated loot and stats.

---

## 📝 Creating a New Profile (`pinatas/my_pinata.yml`)

Create a new `.yml` file inside `pinatas/`. Below is a complete, annotated template:

```yaml
# ==============================================================================
#           🪅 PINATASPECTRA LITE — CUSTOM PROFILE: FESTIVE KING
# ==============================================================================

# Unique uppercase identifier
id: "FESTIVE_KING"

# Display name for holograms and announcements (Supports MiniMessage & Gradients)
display-name: "<gradient:#EC4899:#FCD34D><bold>👑 FESTIVE LLAMA KING</bold></gradient>"

# Base health (number of hits required to defeat)
base-health: 120

# 3D model scale (X, Y, Z)
scale:
  x: 2.2
  y: 2.2
  z: 2.2

# Custom skull texture (Base64 string from Minecraft-Heads.com)
head-texture: "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTRmMGNmNzA4MGY2ZDFkYTUxMDYzOWU5MjU5Zjg3M2M1OTk5ZTRmYTRjNjQ2ZDAwNzM5ODZlYjExNTM5MmY5YSJ9fX0="

# Combat & Evasion Mechanics
mechanics:
  # Enable size reduction and evasion in Phase 2
  micro-evasion: true
  # Enable dimensional jumps in Phase 3
  teleport-shifter: true
  # Maximum teleport leap radius in blocks
  teleport-radius: 12

# Drop Table & Rewards
drops:
  - material: "DIAMOND"
    amount: 8
    chance: 1.0
    direct-to-inventory: true
    name: "<gradient:#38BDF8:#818CF8><bold>Gem of Victory</bold></gradient>"
    lore:
      - "<gray>Awarded to community heroes"
      - "<#FCD34D>PinataSpectra Community Party"

  - material: "GOLDEN_APPLE"
    amount: 16
    chance: 0.85
    direct-to-inventory: false

  - material: "EXPERIENCE_BOTTLE"
    amount: 32
    chance: 1.0
    direct-to-inventory: false
```

---

## 🔍 Key Configuration Parameters

1. **`head-texture`:** Copy the *Value (Base64)* skin string from skull databases such as [Minecraft-Heads.com](https://minecraft-heads.com).
2. **`direct-to-inventory`:**
   * `true`: Delivered directly into the player's inventory if space is available.
   * `false`: Explodes in a festive radial spray across the arena on victory.
3. **`scale`:** Sets the scale on the `x`, `y`, and `z` axes. Standard recommended scale is `1.8` to `2.5`.

---

## 🔄 Live Reload

Save your file and run:

```bash
/pinata reload
```

Your new profile is immediately loaded and ready to spawn:
`/pinata spawn FESTIVE_KING`

---

<div align="center">

[**← 04. Combat Phases**](04-Combat-Phases-and-Boss-Mechanics.md) | [**06. Loot & Rewards →**](06-Loot-Drops-and-Rewards.md)

</div>
