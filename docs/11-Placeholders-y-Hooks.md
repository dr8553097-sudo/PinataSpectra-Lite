# 🧩 11. Placeholders, Hooks & Integraciones

PinataSpectra Lite se integra de forma transparente con los plugins más populares del ecosistema de Minecraft.

---

## 📊 Placeholders de PlaceholderAPI (PAPI)

Si tienes **PlaceholderAPI** instalado, puedes utilizar los siguientes identificadores en tus scoreboards (ej. FeatherBoard, TitleManager), hologramas (DecentHolograms) o tablists (TAB):

| Placeholder | Descripción | Ejemplo de Salida |
|---|---|---|
| `%pinataspectra_active%` | Si hay una piñata viva (`true` / `false`). | `true` |
| `%pinataspectra_health%` | Salud actual de la piñata activa. | `85` |
| `%pinataspectra_max_health%` | Salud total calculada de la piñata activa. | `150` |
| `%pinataspectra_phase%` | Fase de combate actual (1, 2 o 3). | `2` |
| `%pinataspectra_votes_current%` | Votos acumulados hacia la meta. | `18` |
| `%pinataspectra_votes_target%` | Meta total de votos requerida. | `25` |
| `%pinataspectra_pool_current%` | Dinero acumulado en el pozo Vault. | `$3,450.00` |
| `%pinataspectra_pool_target%` | Meta financiera total del pozo. | `$5,000.00` |

---

## 💬 Webhooks de Discord

El servicio `DiscordWebhookService` te permite enviar notificaciones con formato embebido a un canal de Discord cuando la piñata es invocada o derrotada:

```yaml
# En config.yml:
discord:
  enabled: true
  webhook-url: "https://discord.com/api/webhooks/TU_WEBHOOK_AQUI"
  avatar-url: "https://minotar.net/avatar/MHF_Llama"
  username: "PinataSpectra Bot"
```

---

<div align="center">

[**← 10. Comandos & Permisos**](10-Comandos-y-Permisos.md) | [**12. Solución de Problemas & FAQ →**](12-Solucion-de-Problemas-y-FAQ.md)

</div>
