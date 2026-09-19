# 🎁 06. Loot, Drops & Recompensas

PinataSpectra-Lite cuenta con un sistema de recompensas multinivel diseñado para premiar tanto la participación individual en cada golpe como la victoria final colectiva.

---

## 🍬 1. Recompensas por Golpe (Hit Drops)

Cada vez que un jugador conecta un golpe exitoso a la piñata:
- Se evalúa la lista `drops.hit-drops` del perfil.
- Si el jugador gana el porcentaje de chance (`chance: X.X%`), se genera el objeto.
- **Smart Delivery Mode (`loot.delivery-mode`):**
  - Si está configurado en `INVENTORY_FIRST`, el premio se deposita directamente en el inventario del jugador. Si el inventario está lleno, se arroja físicamente al suelo a sus pies con protección anti-pérdida.

---

## 🏆 2. Clímax Final (Grand Finale Drops)

Al derrotar a la piñata:
- Se ejecuta la cinemática `FOUNTAIN_CASCADE` que lanza fuentes de partículas de fuegos artificiales y explosiones de confeti.
- Todos los ítems definidos en `drops.finale-drops` salen eyectados parabólicamente por el aire en una lluvia espectacular para que los jugadores cercanos compitan por recogerlos.
- Se reproducen sonidos de desafío completado (`UI_TOAST_CHALLENGE_COMPLETE`).

---

## 💰 3. Recompensas Económicas Vault (MVP & Participación)

Si tienes instalado el plugin **Vault** y un plugin de economía compatible (EssentialsX, CoinsEngine, etc.):
- **Top 1 (MVP Hitter):** `$1000` (configurable en `rewards.mvp-1st-money`).
- **Top 2:** `$500` (configurable en `rewards.mvp-2nd-money`).
- **Top 3:** `$250` (configurable en `rewards.mvp-3rd-money`).
- **Premio de Participación Comunitaria:** Cualquier jugador que conecte al menos 10 golpes recibe una bonificación monetaria de `$150` (`rewards.participation-money`).
