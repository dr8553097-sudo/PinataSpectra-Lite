# 🛠️ 05. Guía Paso a Paso: Cómo Crear una Piñata Personalizada

En **PinataSpectra Lite**, cada tipo de piñata se almacena en su propio archivo YAML individual dentro de la carpeta `plugins/PinataSpectra-Lite/pinatas/`.

Esto te permite crear tantas piñatas temáticas como desees (ej: Halloween, Navidad, Aniversario, Boss de Minería, etc.) de forma modular y ordenada.

---

## 📝 Creando un Nuevo Perfil (`pinatas/mi_pinata.yml`)

Para crear una nueva piñata, simplemente crea un archivo con extensión `.yml` dentro de `pinatas/`. A continuación, un ejemplo completo comentado:

```yaml
# ==============================================================================
#           🪅 PINATASPECTRA LITE — PERFIL PERSONALIZADO: REY FESTIVO
# ==============================================================================

# Identificador único en mayúsculas
id: "REY_FESTIVO"

# Nombre visible en hologramas y anuncios (Soporta MiniMessage y gradientes)
display-name: "<gradient:#EC4899:#FCD34D><bold>👑 LLAMA REY FESTIVO</bold></gradient>"

# Salud base (número de golpes requeridos)
base-health: 120

# Escala visual del modelo 3D (X, Y, Z)
scale:
  x: 2.2
  y: 2.2
  z: 2.2

# Textura de la cabeza (Skin en Base64 de Minecraft-Heads.com)
head-texture: "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTRmMGNmNzA4MGY2ZDFkYTUxMDYzOWU5MjU5Zjg3M2M1OTk5ZTRmYTRjNjQ2ZDAwNzM5ODZlYjExNTM5MmY5YSJ9fX0="

# Configuración de Fases y Dificultad
mechanics:
  # Permite que la piñata esquive y se encoja en Fase 2
  micro-evasion: true
  # Permite teletransporte periódico en Fase 3
  teleport-shifter: true
  # Rango máximo de salto en bloques
  teleport-radius: 12

# Tabla de Drops y Premios
drops:
  - material: "DIAMOND"
    amount: 8
    chance: 1.0
    direct-to-inventory: true
    name: "<gradient:#38BDF8:#818CF8><bold>Gema de la Victoria</bold></gradient>"
    lore:
      - "<gray>Otorgado a los valientes guerreros"
      - "<#FCD34D>Evento de Piñata de la Comunidad"

  - material: "GOLDEN_APPLE"
    amount: 16
    chance: 0.85
    direct-to-inventory: false

  - material: "EXPERIENCE_BOTTLE"
    amount: 32
    chance: 1.0
    direct-to-inventory: false
```

---

## 🔍 Explicación de los Parámetros

1. **`head-texture`:** Puedes obtener texturas ilimitadas desde sitios como [Minecraft-Heads](https://minecraft-heads.com). Copia el valor de la caja *Value (Base64)* y pégalo entre comillas.
2. **`direct-to-inventory`:**
   * `true`: El ítem se entregará directamente al inventario del atacante si tiene espacio libre.
   * `false`: El ítem saldrá disparado en una lluvia de fuegos artificiales al morir la piñata para que todos los jugadores corran a recogerlo.
3. **`scale`:** Controla el tamaño de la piñata en los ejes `x`, `y`, `z`. El tamaño estándar recomendado es `1.8` a `2.5`.

---

## 🔄 Recarga en Vivo

Una vez que guardes tu archivo `.yml` en `pinatas/`, ejecuta en el servidor:

```bash
/pinata reload
```

El motor cargará automáticamente tu nueva piñata y estará lista para ser invocada con:
`/pinata spawn REY_FESTIVO`

---

<div align="center">

[**← 04. Máquina de Fases**](04-Maquina-de-Fases-Combate.md) | [**06. Loot & Recompensas →**](06-Loot-Drops-y-Recompensas.md)

</div>
