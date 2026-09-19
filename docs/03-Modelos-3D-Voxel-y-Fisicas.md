# 🧊 03. Modelos 3D Voxel & Físicas de Balanceo

PinataSpectra Lite introduce un sistema de renderizado y simulación cinemática procedural que hace que golpear una piñata se sienta reactivo, dinámico y festivo.

---

## 🦄 Renderizado 3D Mediante Display Entities

A diferencia de los modelos basados en resource packs externos que requieren descargas forzadas a los clientes, PinataSpectra Lite utiliza **`ItemDisplay` de Minecraft nativo**:

* **Cabezas Custom en Base64:** Soporta cualquier textura de skin de cabeza de Minecraft (`head-texture`).
* **Interpolación Fluida de Transformaciones:** Las rotaciones, escalas y desplazamientos se transmiten con `Transformation` e interpolación de ticks (`setInterpolationDuration`), lo que produce movimientos a **60+ FPS sin tirones**.
* **Interacción Precisa:** La hitbox `Interaction` se sincroniza exactamente en el centro geométrico de la piñata, asegurando que cada espadazo o golpe con el bate cuente.

---

## ⚡ Simulación de Físicas & Balanceo Armónico

El motor matemático calcula tres fuerzas simultáneas durante el ciclo de vida del evento:

1. **Oscilación Flotante Suave (Péndulo Armónico):**
   * Mientras nadie la golpea, la piñata flota en un patrón senoidal suave:
     $$\Delta Y = A \cdot \sin(\omega t)$$
   * Esto simula la suspensión elástica de una cuerda festiva.

2. **Rebote Reactivo al Impacto (Recoil Vector):**
   * Cuando un jugador golpea la piñata con un bate o espada, el motor calcula un vector opuesto a la mirada del atacante:
     $$\vec{v}_{\text{recoil}} = \text{normalize}(\vec{P}_{\text{piñata}} - \vec{P}_{\text{jugador}}) \cdot K_{\text{fuerza}}$$
   * La piñata se inclina y se balancea en el aire absorbiendo la inercia del golpe.

---

## ⛰️ Fijación Inteligente del Terreno (`findSafeGroundY`)

Uno de los problemas más comunes en eventos con teletransporte es que la entidad se entierre bajo tierra o flote a 20 bloques en el cielo al moverse a una montaña.

```
       [Piñata Suspendida]
                │
                │  ← Altura Base Fija (+2.35 bloques)
                ▼
  ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓ (Terreno Real / Montaña / Escalera)
```

* El algoritmo realiza un trazado vertical hacia abajo desde la posición objetivo.
* Encuentra el primer bloque sólido (`isSolid()`), ignorando pasto alto, flores y agua.
* Asigna la altura exacta sumando un margen de seguridad de **+2.35 bloques**, garantizando que los jugadores siempre puedan alcanzarla a pie sin importar la topografía del terreno.

---

<div align="center">

[**← 02. Instalación**](02-Instalacion-y-Requisitos.md) | [**04. Máquina de Fases & Combate →**](04-Maquina-de-Fases-Combate.md)

</div>
