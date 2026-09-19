# 📜 09. Configuración YAML Maestra

Desglose de cada una de las secciones presentes en el archivo principal `config.yml`.

---

## 🔧 Archivo `config.yml`

```yaml
settings:
  language: "EN"               # Idioma: EN (Inglés) o ES (Español)
  default-profile: "FESTIVE_LLAMA" # Perfil por defecto al escribir /pinata spawn
  check-for-updates: true      # Comprobar actualizaciones en inicio
  show-pro-banner: true        # Mostrar banner ASCII en consola

health-scaling:
  enabled: true                # Escalar vida según jugadores online
  bonus-per-player: 15         # Golpes adicionales por cada jugador conectado

mechanics:
  phases:
    enabled: true              # Activar fases dinámicas (Micro-Evasión y Chaotic Shifter)
    phase2-scale: 0.65         # Escala de tamaño en Fase 2
    phase3-blink-hits: 3       # Golpes necesarios en Fase 3 para activar teletransporte
  teleport:
    min-distance: 12.0         # Rango mínimo de teletransporte (bloques)
    max-distance: 22.0         # Rango máximo de teletransporte (bloques)
    cooldown-seconds: 2.0      # Tiempo mínimo entre teletransportes
  boss-attacks:
    ground-slam:
      enabled: true            # Ataque de onda expansiva
      hit-interval: 15         # Intervalo de golpes para disparar el ataque
      radius: 6.0              # Radio de impacto
      knockback-force: 1.1     # Fuerza de empuje

pinata-bat:
  material: "STICK"
  display-name: "<gradient:#EC4899:#FCD34D><bold>🪅 FESTIVE PIÑATA BAT</bold></gradient>"
  bonus-damage: 1              # Daño extra por golpe al usar el bate

physics:
  pendulum-speed: 1.15         # Multiplicador de velocidad de balanceo
  despawn-timeout-seconds: 240 # Tiempo máximo de vida antes de desaparecer (0 = desactivado)

music:
  enabled: true                # Activar orquesta musical de NoteBlocks
  volume: 1.2                  # Volumen maestro
  radius: 45.0                 # Radio audible de música (bloques)

rewards:
  mvp-1st-money: 1000.0        # Dinero Vault al jugador con más golpes
  mvp-2nd-money: 500.0         # Dinero Vault al 2do lugar
  mvp-3rd-money: 250.0         # Dinero Vault al 3er lugar
  participation-enabled: true
  participation-min-hits: 10   # Mínimo de golpes para bono de participación
  participation-money: 150.0   # Monto del bono de participación
```
