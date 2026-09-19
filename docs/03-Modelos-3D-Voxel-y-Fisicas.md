# 🦄 03. Modelos 3D Voxel & Físicas

## 🧊 Geometría Voxel Dinámica

PinataSpectra-Lite renderiza sus piñatas utilizando **Block Displays** nativos de Minecraft combinados con una entidad de hitbox `Interaction`.

- **Cuerpo Central:** Voxel Core de 3x3x3 cubos de colores vibrantes configurables (`primary-block` y `secondary-block`).
- **Puntas Cónicas & Listones:** 6 extensiones cardinales (`+X`, `-X`, `+Y`, `-Y`, `+Z`, `-Z`) con bloques de lana decorativa (`ribbon-blocks`).
- **Cuerda Colgante:** Micro-partículas que unen la piñata al ancla superior virtual, simulando suspensión por cuerda.

---

## ⚡ Péndulo Armónico & Balanceo

El movimiento no es estático ni repetitivo:
- **Ecuaciones de Trayectoria Sinusoidal:**
  ```
  X = sin(t) * SwayFactor
  Z = cos(t * 0.7) * (SwayFactor * 0.6)
  Y = |sin(t * 2)| * 0.38
  ```
- **Reacción Física a Impactos (Hit Wobble):** Cuando un jugador golpea la piñata, el modelo calcula el vector de dirección del impacto y aplica un momento de inercia y torsión angular que decae suavemente (`wobbleDecay = 0.88`).

---

## ⛰️ Fijación Inteligente del Terreno (Smart Ground Clamping)

Para evitar que la piñata quede atrapada dentro del suelo o flote a alturas inalcanzables cuando se teletransporta en colinas o montañas:
1. El algoritmo `findSafeGroundY` escanea verticalmente el terreno hacia abajo hasta hallar el primer bloque sólido no traspasable.
2. Fija automáticamente la base de oscilación a **+2.35 bloques sobre el suelo**.
3. Realiza un recalibramiento continuo cada 2 segundos para adaptarse si el terreno sufre modificaciones.
