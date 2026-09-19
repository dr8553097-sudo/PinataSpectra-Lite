# ⚔️ 04. Máquina de Fases & Combate Boss

PinataSpectra-Lite transforma el evento tradicional de golpear una piñata en una auténtica batalla de jefe con 3 fases dinámicas.

---

## 📊 Las 3 Fases de Combate

### 🌟 Fase 1: Estándar Festiva (100% - 66% HP)
- **Escala:** Tamaño completo (1.20x).
- **Velocidad:** Péndulo relajado estándar (`speed = 1.0`).
- **Comportamiento:** La piñata baila y emite destellos de confeti y espirales de partículas de doble hélice.
- **Audio:** Sinfonía de carnaval tradicional (Campanas, Flautas y Xilófonos).

---

### ⚡ Fase 2: Micro-Evasión (66% - 33% HP)
- **Activación:** Se activa al caer por debajo del 66% de salud máxima.
- **Efecto de Transición:** Sonido `BLOCK_RESPAWN_ANCHOR_CHARGE` + partículas de chispa eléctrica `ELECTRIC_SPARK`.
- **Escala:** Se encoge a **0.65x** (tamaño miniatura), dificultando el impacto visual y físico.
- **Velocidad:** Duplica su velocidad de balanceo (`phaseSpeedMultiplier = 1.85`).
- **Ataque de Onda:** Cada 15 golpes desata un `Ground Slam Shockwave` que empuja a los atacantes cercanos con sonido de explosión.
- **Audio:** Pista tecno-arcade frenética a 225 BPM con bajos rápidos y notas sincopadas.

---

### 🌀 Fase 3: Chaotic Shifter / Frenzy (33% - 0% HP)
- **Activación:** Se activa al caer por debajo del 33% de salud.
- **Efecto de Transición:** Cuerno de guerra `ITEM_GOAT_HORN_SOUND_0` + explosión sonora `WARDEN_SONIC_BOOM` + portal dimensional.
- **Salto Dimensional (Evasive Blink):** Cada 3 impactos consecutivos, la piñata realiza un teletransporte instantáneo entre 12 y 22 bloques de distancia.
- **Detección de Suelo:** El salto calcula la altura exacta del terreno de destino (`ground + 2.35`), permitiendo persecuciones épicas por montañas o valles.
- **Audio:** Orquesta dramática de clímax con latidos de Warden (`WARDEN_HEARTBEAT`), arpegios veloces y campanas de alarma.
