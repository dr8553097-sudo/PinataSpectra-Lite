# 💰 07. Metas Comunitarias: NuVotifier & Pozo Vault

Una de las características más potentes de **PinataSpectra Lite** es su capacidad para unir a la comunidad de tu servidor hacia un objetivo colectivo mediante dos sistemas:

---

## 🗳️ 1. Sistema de Meta de Votos (`vote-goal`)

Si tu servidor utiliza **NuVotifier** o **Votifier**, PinataSpectra Lite intercepta automáticamente los votos entrantes y alimenta una barra de progreso comunitaria.

```yaml
# En config.yml:
vote-goal:
  enabled: true
  # Votos requeridos para invocar automáticamente la piñata
  target-votes: 25
  # Perfil de piñata a invocar al completar la meta
  reward-profile: "FESTIVE_LLAMA"
  # Reiniciar el contador de votos a 0 tras la fiesta
  reset-on-trigger: true
  # Anunciar en el chat cada X votos conseguidos
  broadcast-interval: 5
```

### Comandos de Usuario:
* **`/vote`** o **`/pinata vote`**: Muestra el progreso actual de votos (`18 / 25 [72%]`) y los enlaces de votación del servidor.

---

## 🪙 2. Pozo de Donaciones Comunitario (`pinata-pool`)

Permite a los jugadores donar dinero de su economía de **Vault** hacia un pozo colectivo. Cuando se recauda el monto objetivo, la Gran Piñata se invoca de inmediato en el spawn predeterminado.

```yaml
# En config.yml:
pinata-pool:
  enabled: true
  # Monto total de dinero de Vault requerido
  target-money: 5000.0
  # Dinero acumulado actualmente (se guarda entre reinicios)
  current-money: 0.0
  # Perfil a invocar al llegar a la meta
  reward-profile: "FESTIVE_LLAMA"
```

### Comandos de Usuario:
* **`/pinata pool`**: Muestra la recaudación actual, el porcentaje completado y la meta.
* **`/pinata pool <cantidad>`** (o `/pinata donate <cantidad>`): Contribuye dinero de la cuenta del jugador al pozo comunitario.

---

<div align="center">

[**← 06. Loot & Recompensas**](06-Loot-Drops-y-Recompensas.md) | [**08. Auto-Scheduler →**](08-Scheduler-y-Eventos-Automaticos.md)

</div>
