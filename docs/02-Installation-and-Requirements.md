# ⚙️ 02. Installation & Requirements

Installing **PinataSpectra Lite** is designed to be *Plug-and-Play*: ready to use in under 60 seconds with zero tedious setup.

---

## 📋 Server Requirements

* **Java Runtime:** **Java 21** or higher (fully tested and optimized for Java 25).
* **Server Software:**
  * ✅ **Paper** (1.20.4, 1.21, 1.22, 1.26+) — *Recommended*
  * ✅ **Purpur** (1.20.4+)
  * ✅ **Folia** (Experimental multithreaded support)
  * ⚠️ *Legacy Spigot:* Not recommended due to missing Display Entity API features.
* **Client Compatibility:** Minecraft 1.20.4 or higher (Zero client mods or resource packs required).

---

## 🔌 Optional Integrations (Hooks)

PinataSpectra Lite automatically detects and hooks into the following plugins if present in your `/plugins` folder:

| Plugin | Purpose in PinataSpectra Lite |
|---|---|
| 💰 **Vault + Economy** (e.g. EssentialsX) | Enables the community donation pool (`/pinata pool`) and monetary rewards. |
| 🗳️ **NuVotifier / Votifier** | Automatically spawns the piñata when reaching a community vote goal (`vote-goal`). |
| 🧩 **PlaceholderAPI (PAPI)** | Exposes live event status, vote progress, and pool balances for scoreboards and tablists. |
| 💬 **Discord Webhooks** | Sends real-time embed announcements to your Discord channels on event start and victory. |

---

## 🚀 Step-by-Step Installation Guide

1. Download `PinataSpectra-Lite-1.0.0.jar` from [GitHub Releases](https://github.com/dr8553097-sudo/PinataSpectra-Lite/releases).
2. Place the `.jar` file inside your server's `plugins/` directory.
3. Start or restart your server.
4. Done! The plugin will generate the `plugins/PinataSpectra-Lite/` folder with default files:
   * `config.yml` (Main settings and combat mechanics).
   * `messages.yml` (Default English messages).
   * `messages_es.yml` (Spanish translations).
   * `pinatas/festive_llama.yml` (Default piñata profile).
   * `pinatas/custom_party.yml` (Customizable template profile).

> [!TIP]
> **Live Language Switching:**  
> You can switch the active server language instantly in-game by typing:  
> `/pinata lang EN` or `/pinata lang ES`

---

<div align="center">

[**← 01. Introduction**](01-Introduction-and-Philosophy.md) | [**03. 3D Voxel Models & Physics →**](03-3D-Voxel-Models-and-Physics.md)

</div>
