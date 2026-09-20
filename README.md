<p align="center">
  <img src="assets/banner.png" alt="PinataSpectra Lite Banner" width="100%" style="max-width: 850px;">
</p>

<p align="center">
  <strong><em>"Where Physics Meets Fantasy — The Free Standard 3D Piñata Engine for Modern Minecraft Networks."</em></strong>
</p>

<p align="center">
  <a href="https://dr8553097-sudo.github.io/PinataSpectra-Lite/"><img src="https://img.shields.io/badge/📚_Official_Wiki-Interactive_Docs-00f0ff.svg?style=for-the-badge" alt="Wiki"></a>
  <a href="https://github.com/dr8553097-sudo/PinataSpectra-Lite"><img src="https://img.shields.io/badge/Edition-COMMUNITY_LITE-22c55e.svg?style=for-the-badge" alt="Edition"></a>
  <a href="https://papermc.io"><img src="https://img.shields.io/badge/Paper%20%2F%20Purpur-1.20%20--%201.26+-00D26A.svg?style=for-the-badge" alt="Platform"></a>
  <a href="https://www.java.com"><img src="https://img.shields.io/badge/Java-21%20%2F%2025-ED8B00.svg?style=for-the-badge" alt="Java"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-GPLv3_Open_Source-blue.svg?style=for-the-badge" alt="License"></a>
  <a href="https://dr8553097-sudo.github.io"><img src="https://img.shields.io/badge/Portfolio-Dafealru-a855f7.svg?style=for-the-badge" alt="Portfolio"></a>
</p>

---

## 🍃 About PinataSpectra Lite

**PinataSpectra Lite** is the official free and open-source community edition of the procedural 3D Piñata boss event engine for Minecraft. Engineered from scratch to eliminate outdated plugins reliant on dozens of invisible `ArmorStands` that cause packet lag and FPS drops, **PinataSpectra Lite** harnesses native Minecraft 1.20 - 1.26+ `Display Entities` and `Interaction` hitboxes to deliver a fluid, harmonic, and 100% resource-pack-free festive boss experience.

> [!TIP]
> **100% Open Source (GPLv3):** Transparent, auditable, and secure. Zero obfuscation, zero backdoors, and community contribution ready.

---

## ⚖️ Comprehensive Feature Matrix: Lite vs. Sovereign PRO vs. Legacy

| Architectural Feature | 🍃 PinataSpectra Lite (This Repo) | 🪅 PinataSpectra Sovereign PRO | 📦 Legacy / Traditional Plugins |
|---|:---:|:---:|:---:|
| **License & Code** | **GPLv3 Open Source** | Commercial Proprietary | Closed / Abandoned |
| **Entity Architecture** | Native `ItemDisplay` + `Interaction` | Native `ItemDisplay` + `Interaction` + LOD | 15-30 Invisible `ArmorStands` (High Packets) |
| **3D Voxel Models** | 1 Base Form (Festive Llama) | **8 Mythic Forms** (Mecha, Dragon, Crown, etc.) | None / Static floating head |
| **Combat Phases** | **3 Dynamic Phases** (Evolving Audio) | **4 Epic Phases** (Orbital Shields & Minions) | Single static health bar |
| **Soundtrack & SFX** | 3 Phase NoteBlock Songs + Advanced SFX | 4 Orchestral Themes + Cosmic SFX | Generic hit sounds |
| **Terrain Clamping** | **Smart Ground Clamping (`findSafeGroundY`)**| **3D Catenary Raycast with Harmonic Rope** | Falls into void or clips into terrain |
| **Death Cinematics** | 1 Classic Festive Item Explosion | **7 Cosmic Supernovas (Black Hole)** | Generic item drop at feet |
| **Custom Bats** | 1 Festive Bat with MiniMessage | **7 Mythic Bats** (Mjolnir with Lightning) | Regular wooden stick |
| **Language System** | **Dynamic Live Switching (`/pinata lang EN\|ES`)** | **Dynamic Live Switching + Auto-Detect** | Single hardcoded file |
| **Community Goals** | NuVotifier Goal + Vault Community Pool | NuVotifier + Vault + Global BossBar | None / Manual only |
| **Database Engines** | Local SQLite | **SQLite + MySQL + MariaDB + Redis** | Slow YAML Flatfile |
| **Studio & GUI Editors** | In-game Visual Editor GUI | **6 Live Visual In-Game Studio GUIs** | Manual YAML editing only |

---

## 🌟 The Creator's Heart & Vision

> ### *"Crafted with Precision. Born from Passion. Dedicated to Shared Joy."*
> 
> *"Every unforgettable Minecraft memory is built on shared moments of joy, laughter, and collective triumph. I didn't create **PinataSpectra** merely to write high-performance math equations or optimize packet pipelines — I built it because I believe that server events should feel truly **magical**.*
> 
> *Growing up in multiplayer communities, the most cherished nights were always those rare occasions when dozens of players gathered in a central square — laughing, swinging at a floating piñata beneath a sky full of fireworks, cheering as candies rained down, and forging genuine friendships in the festive chaos.*
> 
> *PinataSpectra Lite brings this magic to every survival, network, and community server around the globe for free, with clean, honest, and high-performance open-source code."*
> 
> — **Dafealru ([dr8553097-sudo](https://github.com/dr8553097-sudo))**  
> *Lead Developer & Architect of PinataSpectra*

---

## ⚡ Core Systems Overview

### 🧊 1. Zero Resource Pack 3D Voxel Engine
* Native GPU-accelerated rendering using Paper/Purpur Display Entities.
* Zero client-side mods or mandatory resource packs.
* Pixel-perfect `Interaction` hitbox centered on the piñata model.

### 🎶 2. Dynamic 3-Phase Soundscape & NoteBlock Music
* **Phase 1 (100% - 66% HP):** Festive Carnival March (Flute & Bell notes).
* **Phase 2 (66% - 33% HP):** Micro-Evasion Mode (Piñata shrinks to 65% scale, doubles speed, energetic Xylophone tempo).
* **Phase 3 (33% - 0% HP):** Chaotic Warp Shifter (Dimensional leaps, sonic pulses, Amethyst resonance, coordinate broadcasts).

### ⛰️ 3. Smart Terrain Height Clamping (`findSafeGroundY`)
* Dynamic vertical raycasting guarantees the piñata always floats safely at **+2.35 blocks** above the real solid terrain, even after warping onto hills, mountains, or structures.

### 🌐 4. Live Multi-Language Engine
* Switch languages on the fly without restarting the server:
  * `/pinata lang EN` — English
  * `/pinata lang ES` — Spanish
* Seamless automatic config synchronization via `ConfigUpdaterEngine`.

---

## 📖 Official 12-Chapter Documentation

Explore the [**Interactive Web Portal**](https://dr8553097-sudo.github.io/PinataSpectra-Lite/) or read the local guides:

| Chapter | Title | Summary |
|---|---|---|
| 🏛️ **01** | [Introduction & Philosophy](docs/01-Introduction-and-Philosophy.md) | Architectural vision, zero-ArmorStand rendering. |
| ⚙️ **02** | [Installation & Requirements](docs/02-Installation-and-Requirements.md) | Java 21/25, Paper/Purpur compatibility & hooks. |
| 🧊 **03** | [3D Voxel Models & Physics](docs/03-3D-Voxel-Models-and-Physics.md) | Harmonic pendulum sway and procedural recoil. |
| ⚔️ **04** | [Combat Phases & Boss Mechanics](docs/04-Combat-Phases-and-Boss-Mechanics.md) | 3-phase transitions & adaptive NoteBlock soundtracks. |
| 🛠️ **05** | [Creating Custom Piñatas](docs/05-Creating-Custom-Pinatas.md) | Step-by-step guide to configuring `.yml` profiles in `pinatas/`. |
| 🎁 **06** | [Loot Drops & Rewards](docs/06-Loot-Drops-and-Rewards.md) | Dual loot delivery, MVP Podium Top 1/2/3 & consolation prizes. |
| 💰 **07** | [Vote Goals & Vault Pool](docs/07-Community-Vote-Goal-and-Vault-Pool.md) | NuVotifier integration & community donation pool (`/pinata pool`). |
| ⏳ **08** | [Auto-Scheduler & Sync](docs/08-Auto-Scheduler-and-Config-Sync.md) | Automated event intervals & `ConfigUpdaterEngine`. |
| 📜 **09** | [Master Configuration Reference](docs/09-Master-Configuration-Reference.md) | Comprehensive breakdown of `config.yml` and message files. |
| 🎮 **10** | [Commands & Permissions](docs/10-Commands-and-Permissions.md) | Complete list of commands, subcommands, and permission nodes. |
| 🧩 **11** | [Placeholders & Integrations](docs/11-Placeholders-and-Integrations.md) | PlaceholderAPI variables, Vault economy, and Discord Webhooks. |
| 💡 **12** | [Troubleshooting & FAQ](docs/12-Troubleshooting-and-FAQ.md) | Common diagnostics, WorldGuard flag fixes, and `/pinata clean`. |

---

## 👑 Looking for the Ultimate Experience? Discover Sovereign PRO

For large networks with 50+ concurrent players looking for the pinnacle of multiplayer event engineering:

* **8 Mythic 3D Voxel Forms:** Mecha Titan, Ender Dragon, Royal Crown, Golden Pegasus, etc.
* **7 Cosmic Supernova Death Cinematics:** Black Hole Singularity, Solar Phoenix, Divine Thunder, etc.
* **Anti-Steal Loot Vaults:** Individual reward security preventing loot stealing during high-player events.
* **7 Mythic Bats:** Mjolnir with authentic lightning strikes, Chaos Bat, Celestial Stave.
* **Cross-Server Sync & Databases:** Native MySQL, MariaDB, and Redis caching.
* **3D Floor Roulette Jackpots:** Animated rotating prize wheels projected onto the ground in real-time.

👉 **[Unlock PinataSpectra Sovereign PRO on BuiltByBit](https://builtbybit.com/pinataspectra)**

---

## 🐛 Bug Reports & Community Contributions

We warmly welcome community feedback, translations, and issue reports:

* 🐞 [Submit a Bug Report](https://github.com/dr8553097-sudo/PinataSpectra-Lite/issues/new?template=bug_report.yml)
* 💡 [Request a Feature](https://github.com/dr8553097-sudo/PinataSpectra-Lite/issues/new?template=feature_request.yml)

---

## 📜 License

Distributed under the **GNU General Public License v3.0 (GPLv3)**. See [`LICENSE`](LICENSE) for details.

<p align="center">
  <sub>Designed and developed with passion by <b><a href="https://dr8553097-sudo.github.io">Dafealru</a></b>.</sub>
</p>
