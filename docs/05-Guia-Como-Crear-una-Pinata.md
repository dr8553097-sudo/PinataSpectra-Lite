# 🛠️ 05. Guía: Cómo Crear una Piñata Custom

Crear nuevos perfiles de piñatas en PinataSpectra-Lite es sumamente sencillo. Todos los perfiles se almacenan como archivos `.yml` dentro de la carpeta `/plugins/PinataSpectra-Lite/pinatas/`.

---

## 📝 Estructura de un Perfil YAML

Crea un archivo nuevo, por ejemplo `pinatas/volcanic_dragon.yml`:

```yaml
# ==============================================================================
#             🪅 PINATASPECTRA LITE — VOLCANIC DRAGON PROFILE
# ==============================================================================
id: "VOLCANIC_DRAGON"
display-name: "<gradient:#EF4444:#F59E0B:#FCD34D><bold>✦ Furia Volcánica ✦</bold></gradient>"
health: 500

# Bloques del modelo 3D Voxel
primary-block: "RED_CONCRETE"
secondary-block: "ORANGE_CONCRETE"
ribbon-blocks:
  - "RED_WOOL"
  - "ORANGE_WOOL"
  - "YELLOW_WOOL"

# Efectos visuales y de partículas
glow-color: "#EF4444"
aura-particle: "FLAME"
hit-particle: "LAVA"
trail-particle: "SMOKE"

# Sonidos
ambient-sound: "BLOCK_AMETHYST_BLOCK_CHIME"
hit-sound: "BLOCK_WOOD_HIT"
death-sound: "UI_TOAST_CHALLENGE_COMPLETE"

# Mecánicas de Combate
teleport-chance: 0.50
teleport-interval-seconds: 4
movement-speed: 1.30

# Tabla de Drops
drops:
  hit-drops:
    candy_common:
      material: "COOKIE"
      amount: 1
      chance: 25.0
      name: "<#F59E0B>★ Galleta Ígnea ★"
    candy_gold:
      material: "GOLD_NUGGET"
      amount: 3
      chance: 15.0
      name: "<#FFD700>★ Pepita del Dragón ★"

  finale-drops:
    totem:
      material: "TOTEM_OF_UNDYING"
      amount: 2
      chance: 90.0
      name: "<#F59E0B>★ Tótem de Lava Inmortal ★"
    netherite:
      material: "NETHERITE_INGOT"
      amount: 2
      chance: 70.0
      name: "<#71717A>★ Lingote de Netherite Puro ★"
    exp:
      material: "EXPERIENCE_BOTTLE"
      amount: 64
      chance: 100.0
```

---

## 🔄 Recarga en Vivo

Una vez guardado el archivo, ejecuta en la consola o en el juego:
```bash
/pinata reload
```
Y luego invócala con:
```bash
/pinata spawn VOLCANIC_DRAGON
```
