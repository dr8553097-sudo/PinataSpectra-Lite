# 🧩 11. Placeholders, Hooks & Integrations

PinataSpectra Lite integrates seamlessly with major Minecraft server plugins.

---

## 📊 PlaceholderAPI (PAPI) Variables

If **PlaceholderAPI** is installed, you can use these placeholders in scoreboards (e.g. FeatherBoard), holograms (DecentHolograms), or tablists (TAB):

| Placeholder | Description | Example Output |
|---|---|---|
| `%pinataspectra_active%` | Whether a piñata is currently active (`true` / `false`). | `true` |
| `%pinataspectra_health%` | Current remaining health of active piñata. | `85` |
| `%pinataspectra_max_health%` | Total calculated maximum health. | `150` |
| `%pinataspectra_phase%` | Current active combat phase (1, 2, or 3). | `2` |
| `%pinataspectra_votes_current%` | Accumulated community votes. | `18` |
| `%pinataspectra_votes_target%` | Total required votes for party. | `25` |
| `%pinataspectra_pool_current%` | Raised money in Vault community pool. | `$3,450.00` |
| `%pinataspectra_pool_target%` | Target monetary goal for pool. | `$5,000.00` |

---

## 💬 Discord Webhooks

The `DiscordWebhookService` sends rich embed notifications to your Discord channels when the piñata spawns or is defeated:

```yaml
# In config.yml:
discord:
  enabled: true
  webhook-url: "https://discord.com/api/webhooks/YOUR_WEBHOOK_URL_HERE"
  avatar-url: "https://minotar.net/avatar/MHF_Llama"
  username: "PinataSpectra Bot"
```

---

<div align="center">

[**← 10. Commands & Permissions**](10-Commands-and-Permissions.md) | [**12. Troubleshooting & FAQ →**](12-Troubleshooting-and-FAQ.md)

</div>
