# ⏳ 08. Auto-Scheduler & Config Synchronizer

PinataSpectra Lite includes built-in automation so you never have to manually run spawn commands or worry about configuration files breaking when updating versions.

---

## ⏰ Automated Event Scheduler (`scheduler`)

Schedule recurring community piñata events at regular minute intervals, complete with advance warnings to rally players to the arena:

```yaml
# In config.yml:
scheduler:
  enabled: true
  # Interval between events in minutes (e.g. 120 = every 2 hours)
  interval-minutes: 120
  # Piñata profile to spawn
  profile: "FESTIVE_LLAMA"
  # Saved spawn location name (set via /pinata setspawn)
  spawn-location: "default"
  # Minimum online players required to start
  min-players: 3
  # Advance warning alerts in minutes
  warnings:
    - 15
    - 5
    - 1
```

---

## 🔄 Smart Lossless Config Synchronizer: `ConfigUpdaterEngine`

Updating Minecraft plugins often overwrites customized settings or forces manual file resets.

**In PinataSpectra Lite, this never happens:**

* On startup or when running `/pinata reload`, the `ConfigUpdaterEngine`:
  1. Inspects the embedded JAR resources for newly introduced keys, comments, and default values.
  2. Injects them cleanly into your existing disk files (`config.yml`, `messages.yml`, `messages_es.yml`, and `pinatas/*.yml`).
  3. **Preserves 100% of your custom drops, health values, language choices, and saved spawn points.**

---

<div align="center">

[**← 07. Vote Goals & Vault Pool**](07-Community-Vote-Goal-and-Vault-Pool.md) | [**09. Master Configuration →**](09-Master-Configuration-Reference.md)

</div>
