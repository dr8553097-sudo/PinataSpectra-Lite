# ⚔️ 04. Máquina de Fases & Combate Boss

PinataSpectra Lite no es una simple piñata pasiva. Incorpora una **máquina de estados de combate** que evoluciona dinámicamente según el porcentaje de vida restante, acompañada de efectos de sonido avanzados y bandas sonoras NoteBlock adaptativas.

---

## 🎭 Las 3 Fases Dinámicas del Combate

```
 100% HP ─────────────── 66% HP ─────────────── 33% HP ─────────────── 0% HP
    │                       │                       │                     │
    ▼                       ▼                       ▼                     ▼
 [ FASE 1: CARNAVAL ]    [ FASE 2: EVASIÓN ]     [ FASE 3: CAOS WARP ]  [ GRAN FINAL ]
  • Melodía Festiva       • Se encoge al 65%      • Saltos Dimensionales • Podio MVP
  • Flotación estable     • Duplica velocidad     • Ondas sónicas        • Lluvia dulces
  • Campanillas de oro    • Sparks eléctricos     • Resonancia amatista  • Explosión FX
```

---

### 🪅 Fase 1: Marcha Festiva de Carnaval (100% - 66% HP)
* **Comportamiento:** La piñata flota tranquilamente en su punto de origen con balanceo suave.
* **Música NoteBlock:** Tema alegre de fiesta tradicional (instrumentos *Flute* y *Bell*).
* **Efectos:** Chispas doradas y notas musicales flotantes al recibir impactos.

---

### ⚡ Fase 2: Micro-Evasión Táctica (66% - 33% HP)
* **Activación:** Al cruzar el umbral del 66% de salud restante.
* **Mecánica:**
  * La piñata se encoge inmediatamente al **65% de su tamaño original**.
  * Su velocidad de movimiento e inercia se duplica, haciendo más difícil encadenar golpes rápidos.
* **Música NoteBlock:** Melodía acelerada y energética (instrumentos *Xylophone* y *Pitched Bass*).
* **Efectos:** Destello eléctrico (`ELECTRIC_SPARK`), sonido de baliza activada y anuncio global en el chat.

---

### 🌀 Fase 3: Desplazamiento Caótico Dimensional (33% - 0% HP)
* **Activación:** Al llegar al 33% de salud restante.
* **Mecánicas:**
  * **Teletransporte Acrobático (Warp Shift):** La piñata realiza saltos dimensionales periódicos en un radio de 12 bloques alrededor del área.
  * **Anuncio de Coordenadas:** Al teletransportarse, publica las nuevas coordenadas `X, Y, Z` en el chat y ActionBar para que los jugadores la persigan.
  * **Anclaje de Seguridad:** Al saltar, recalcula inmediatamente la altura segura sobre el suelo (`findSafeGroundY`).
* **Música NoteBlock:** Banda sonora de clímax y tensión (*Chime*, *Guitar* y *Resonancia de Amatista*).
* **Efectos:** Ondas de pulso sónico, distorsión de ancla de reaparición (`RESPAWN_ANCHOR_CHARGE`) y partículas de portal.

---

## 🎶 Soundscape Dinámico & Noteblocks de Minecraft

PinataSpectra Lite utiliza secuencias de notas musicales nativas de Minecraft transmitidas mediante paquetes de sonido posicionales (`player.playSound`), lo que significa que la música se escucha en 3D alrededor de la arena del evento sin requerir paquetes de texturas.

---

<div align="center">

[**← 03. Modelos 3D**](03-Modelos-3D-Voxel-y-Fisicas.md) | [**05. Cómo Crear una Piñata →**](05-Guia-Como-Crear-una-Pinata.md)

</div>
