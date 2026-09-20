# 📜 09. Master Configuration & Multi-Language Reference

PinataSpectra Lite features a clean, human-readable YAML architecture with full native **MiniMessage** support (gradients, HEX `#RRGGBB` colors, and hoverable/clickable text).

---

## 🌐 1. Main Settings (`config.yml`)

```yaml
# ==============================================================================
#  🪅 PINATASPECTRA LITE — MASTER CONFIGURATION
# ==============================================================================

settings:
  # Active plugin language: 'EN' (English) or 'ES' (Spanish)
  language: "EN"
  # Default profile when running /pinata spawn without arguments
  default-profile: "FESTIVE_LLAMA"
  # Automatically check for updates on startup
  check-for-updates: true
  # Show start banner in console
  show-pro-banner: true

# Dynamic health scaling based on online player count
health-scaling:
  enabled: true
  # Extra hits added to base health per online player
  bonus-per-player: 15

# Saved spawn locations set via /pinata setspawn <name>
spawns:
  default:
    world: "world"
    x: 0.5
    y: 75.0
    z: 0.5
    yaw: 0.0
    pitch: 0.0
```

---

## 💬 2. Localization & Message Files (`messages.yml` & `messages_es.yml`)

Switch active languages instantly without rebooting the server:
* `/pinata lang EN`
* `/pinata lang ES`

The plugin dynamically swaps between `messages.yml` and `messages_es.yml` in memory, ensuring immediate zero-downtime translation.

---

<div align="center">

[**← 08. Auto-Scheduler & Sync**](08-Auto-Scheduler-and-Config-Sync.md) | [**10. Commands & Permissions →**](10-Commands-and-Permissions.md)

</div>
