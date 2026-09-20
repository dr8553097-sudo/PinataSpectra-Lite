# 🎮 10. Commands, Permissions & Festive Bat

PinataSpectra Lite includes a comprehensive suite of administrative and community commands with full Tab completion.

---

## 🕹️ Command Reference

| Command | Permission | Description |
|---|---|---|
| `/pinata help` | *None* | Displays the visual in-game command menu. |
| `/pinata spawn [profile] [spawn]` | `pinataspectra.admin` | Spawns a piñata at your current location or saved point. |
| `/pinata kill` | `pinataspectra.admin` | Immediately removes the active piñata event. |
| `/pinata bat [give <player>]` | `pinataspectra.admin` | Gives the Festive Bat with custom sounds and lore. |
| `/pinata clean` | `pinataspectra.admin` | Purges orphaned or residual Display Entities across all worlds. |
| `/pinata setspawn <name>` | `pinataspectra.admin` | Saves your current location as a named arena spawn point. |
| `/pinata lang <EN\|ES>` | `pinataspectra.admin` | Switches active server language (English / Spanish). |
| `/pinata editor` (or `/pinata studio`) | `pinataspectra.admin` | Opens the in-game visual editor GUI. |
| `/pinata reload` | `pinataspectra.admin` | Reloads all configurations, profiles, and messages. |
| `/pinata pool [amount]` | *None* | View community pool balance or donate Vault funds. |
| `/pinata vote` (or `/vote`) | *None* | Check community vote progress toward the next party. |
| `/pinata comparison` (or `/pinatapro`)| *None* | Shows the detailed comparison table with Sovereign PRO. |

---

## 🔑 Permission Nodes

* **`pinataspectra.admin`**: Grants access to all administrative commands (`spawn`, `kill`, `clean`, `bat`, `setspawn`, `lang`, `reload`, `editor`). Granted to server operators (`op`) by default.
* **`pinataspectra.user`**: Base permission for player commands (`pool`, `vote`, `help`).

---

<div align="center">

[**← 09. Master Configuration**](09-Master-Configuration-Reference.md) | [**11. Placeholders & Integrations →**](11-Placeholders-and-Integrations.md)

</div>
