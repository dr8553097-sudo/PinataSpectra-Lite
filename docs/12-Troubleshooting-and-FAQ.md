# 💡 12. Troubleshooting, Diagnostics & FAQ

A quick guide for diagnosing common setup questions and administrative best practices.

---

## ❓ Frequently Asked Questions (FAQ)

### 1. Why is the piñata not taking damage when players hit it?
* Check if the event arena region has PvP or entity damage disabled in WorldGuard (`pvp: deny` or `damage: deny`).
* Verify that players have the default `pinataspectra.user` permission if custom permissions are enabled.

### 2. How do I clean orphaned entities if the server crashed during an event?
* Run:  
  ```bash
  /pinata clean
  ```
  The `PinataCleanEngine` scans all loaded worlds and safely removes any orphaned `ItemDisplay` or `Interaction` entities without touching normal mobs.

### 3. How do I change the active language?
* Simply run in console or in-game chat:  
  ```bash
  /pinata lang EN
  ```
  or for Spanish:
  ```bash
  /pinata lang ES
  ```
  Changes take effect immediately without requiring a server reboot.

---

## 🐛 Bug Reports & Community Support

If you encounter an unexpected issue or want to suggest new features:
* 🐞 [Submit a Bug Report](https://github.com/dr8553097-sudo/PinataSpectra-Lite/issues/new?template=bug_report.yml)
* 💡 [Suggest a Feature](https://github.com/dr8553097-sudo/PinataSpectra-Lite/issues/new?template=feature_request.yml)

---

<div align="center">

[**← 11. Placeholders & Integrations**](11-Placeholders-and-Integrations.md) | [**Back to Home 🏠**](README.md)

</div>
