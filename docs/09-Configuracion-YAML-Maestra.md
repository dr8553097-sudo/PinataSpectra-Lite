# 📜 09. Configuración YAML Maestra & Multi-Idioma

La configuración de PinataSpectra Lite está estructurada de forma limpia y organizada con compatibilidad nativa con **MiniMessage** (gradientes, colores HEX `#RRGGBB` y formatos modernos).

---

## 🌐 1. Idioma y Opciones Generales (`config.yml`)

```yaml
# ==============================================================================
#  🪅 PINATASPECTRA LITE — CONFIGURACIÓN PRINCIPAL
# ==============================================================================

settings:
  # Idioma del plugin: 'EN' (English) o 'ES' (Español)
  language: "ES"
  # Perfil por defecto al usar /pinata spawn sin argumentos
  default-profile: "FESTIVE_LLAMA"
  # Comprobar actualizaciones al iniciar el servidor
  check-for-updates: true
  # Mostrar banner de inicio en la consola
  show-pro-banner: true

# Escalado dinámico de vida según jugadores conectados
health-scaling:
  enabled: true
  # Golpes adicionales añadidos a la piñata por cada jugador conectado
  bonus-per-player: 15

# Ubicaciones de spawn guardadas mediante /pinata setspawn <nombre>
spawns:
  default:
    world: "world"
    x: 0.5
    y: 75.0
    z: 0.5
    yaw: 0.0
    pitch: 0.0
```

---

## 💬 2. Mensajes y Traducciones (`messages.yml` & `messages_es.yml`)

Puedes alternar el idioma instantáneamente en el juego mediante el comando:
* `/pinata lang ES`
* `/pinata lang EN`

El plugin cambiará dinámicamente entre `messages_es.yml` y `messages.yml` recargando los componentes en memoria sin necesidad de reiniciar el servidor.

---

<div align="center">

[**← 08. Auto-Scheduler**](08-Scheduler-y-Eventos-Automaticos.md) | [**10. Comandos & Permisos →**](10-Comandos-y-Permisos.md)

</div>
