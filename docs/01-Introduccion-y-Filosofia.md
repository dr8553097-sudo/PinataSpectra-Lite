# 🍃 01. Introducción & Filosofía de PinataSpectra Lite

**PinataSpectra Lite** es la edición comunitaria y de código abierto del motor de piñatas 3D procedimentales y eventos festivos para servidores **Minecraft (Paper, Purpur y Spigot 1.20 - 1.26+)**.

Fue concebido para democratizar los eventos de alta gama en Minecraft, permitiendo a cualquier comunidad (desde pequeños servidores survival entre amigos hasta modalidades en crecimiento) disfrutar de jefes festivos con físicas fluidas, música por fases y cero lag.

---

## 🎯 ¿Por qué existe PinataSpectra Lite?

Los plugins de piñatas tradicionales de la última década arrastran problemas estructurales que dañan la experiencia de los jugadores:

1. **Saturación por ArmorStands invisibles:**
   * Los plugins antiguos agrupaban entre 15 y 30 `ArmorStands` invisibles por piñata para sostener cabezas o bloques.
   * Esto generaba un diluvio de paquetes de sincronización hacia los clientes, provocando caídas drásticas de FPS en computadoras de gama media/baja.
2. **Hitboxes rotas o imprecisas:**
   * Al golpear armaduras invisibles, los golpes fallaban o no se registraban si el jugador miraba un ángulo no colisionable.
3. **Mecánicas monótonas:**
   * La mayoría de plugins se limitaban a una entidad estática flotando que soltaba ítems al recibir X clics.

---

## ⚡ Los 4 Pilares de la Arquitectura Lite

```
 ┌──────────────────────────────────────────────────────────────┐
 │                  🍃 PINATASPECTRA LITE CORE                  │
 ├──────────────────────────────┬───────────────────────────────┤
 │ 🧊 GPU Native Display        │ 🎶 3-Phase Dynamic Audio      │
 │  • ItemDisplay nativo 1.20+  │  • 3 melodías NoteBlock       │
 │  • Interaction Hitbox exacta │  • Resonancias & SFX avanzados│
 ├──────────────────────────────┼───────────────────────────────┤
 │ ⛰️ Smart Terrain Clamping    │ 🌐 Live Multi-Language Engine │
 │  • findSafeGroundY (+2.35m)  │  • /pinata lang [EN|ES]       │
 │  • Balanceo armónico sin/cos │  • Sincronizador sin pérdidas │
 └──────────────────────────────┴───────────────────────────────┘
```

1. **Display Entities Nativas (1.20+):**
   * Toda la piñata se construye mediante un único `ItemDisplay` maestro acoplado a una entidad `Interaction` de precisión milimétrica.
   * Renderizado directo en GPU por el cliente de Minecraft: **reducción del 85% en uso de CPU y ancho de banda**.

2. **Soundscape Dinámico por Fases:**
   * En lugar de simples clics genéricos, la piñata ejecuta una banda sonora completa en NoteBlocks que cambia de ritmo e instrumentos al avanzar las fases de combate.

3. **Fijación de Altura Inteligente:**
   * Algoritmo de trazado de rayos que calcula el suelo real y mantiene la piñata flotando a una altura óptima (+2.35 bloques), evitando que quede enterrada en cerros o colinas tras teletransportarse.

4. **100% Abierto, Transparente & Seguro (GPLv3):**
   * Sin código ofuscado, sin librerías invasivas y con sincronización inteligente de archivos mediante `ConfigUpdaterEngine`.

---

## 👑 Comparativa con la Edición Sovereign PRO

| Característica | 🍃 PinataSpectra Lite | 🪅 PinataSpectra Sovereign PRO |
|---|:---:|:---:|
| **Código & Licencia** | GPLv3 (Open Source) | Comercial Propietaria |
| **Modelos 3D Voxel** | 1 Forma Base (Festive Llama) | **8 Formas** (Mecha, Dragón, Corona, etc.) |
| **Fases de Combate** | **3 Fases** (Evasión & Caos) | **4 Fases** (Escudos orbitales & Esbirros) |
| **Efectos de Muerte** | Explosión Festiva Clásica | **7 Supernovas Cósmicas (Black Hole)** |
| **Bates Míticos** | 1 Bate Festivo con MiniMessage | **7 Bates Míticos** (Mjolnir con Rayos) |
| **Seguridad de Loot** | Dropeo directo + Piso | **Anti-Steal Vault** (Protección anti-robo) |
| **Bases de Datos** | SQLite Local | **MySQL + MariaDB + Redis** |
| **Jackpots** | Ninguno | **Ruletas 3D en el suelo en tiempo real** |

---

<div align="center">

[**02. Instalación & Requisitos →**](02-Instalacion-y-Requisitos.md)

</div>
