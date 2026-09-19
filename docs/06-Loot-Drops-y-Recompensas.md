# 🎁 06. Sistema de Loot, Drops & Podio MVP

PinataSpectra Lite cuenta con un sistema de distribución de recompensas diseñado para premiar tanto a los jugadores más competitivos que infligen más daño (MVP) como a los participantes casuales.

---

## 🏆 Podio de Ganadores (Top 3 MVP)

Al ser derrotada la piñata, el motor calcula el porcentaje exacto de contribución de cada jugador y anuncia el podio con formato MiniMessage y fuegos artificiales:

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
           🪅 ¡LA PIÑATA HA SIDO CONQUERADA! 🪅

  👑 TOP MVP DE LA FIESTA › Notch (45 golpes)

  🥇 1º Lugar: Notch ━ 45 golpes (37.5%) • +$2500
  🥈 2º Lugar: Alex  ━ 32 golpes (26.6%) • +$1200
  🥉 3º Lugar: Steve ━ 18 golpes (15.0%) • +$600
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

## 🎁 Sistema de Loot Dual

Las recompensas se dividen en dos categorías complementarias:

### 1. Recompensas Directas al Inventario
* Si un ítem en la configuración de la piñata tiene `direct-to-inventory: true`, se deposita en el inventario del atacante.
* **Protección de Inventario Lleno:** Si el inventario del jugador está lleno, el ítem se dropea a sus pies y recibe una notificación en el ActionBar:  
  `⚠ ¡Tu inventario está lleno! El dulce de piñata cayó a tus pies.`

### 2. Lluvia de Premios en el Gran Clímax Final
* Todos los ítems configurados con `direct-to-inventory: false` salen disparados en un abanico radial desde el centro de la piñata al momento de la explosión final, generando la clásica emoción comunitaria de correr a recoger dulces y tesoros.

---

## 💰 Premios de Consolación (Participation Reward)

Para incentivar a los jugadores con menor nivel o equipamiento a unirse al evento, se puede otorgar un premio de consolación en dinero de Vault según el número de impactos conectados:

```yaml
# En config.yml:
rewards:
  consolation-prize:
    enabled: true
    money-per-hit: 10.0 # Otorga $10 por cada golpe acertado
```

---

<div align="center">

[**← 05. Cómo Crear una Piñata**](05-Guia-Como-Crear-una-Pinata.md) | [**07. Metas de Votos & Pool Vault →**](07-Metas-de-Votos-y-Pool-Vault.md)

</div>
