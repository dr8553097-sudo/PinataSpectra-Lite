# ⏳ 08. Auto-Scheduler & Sincronizador de Configuraciones

PinataSpectra Lite incluye automatización inteligente para que nunca tengas que preocuparte por iniciar eventos a mano o lidiar con archivos corruptos tras actualizar versiones.

---

## ⏰ Programador Automático de Eventos (`scheduler`)

Puedes programar eventos periódicos que se ejecuten automáticamente cada cierto intervalo de tiempo con alertas previas para reunir a todos los jugadores en el punto de encuentro:

```yaml
# En config.yml:
scheduler:
  enabled: true
  # Intervalo de tiempo entre eventos en minutos (ej: cada 2 horas = 120)
  interval-minutes: 120
  # Perfil de piñata a invocar
  profile: "FESTIVE_LLAMA"
  # Nombre del spawn guardado con /pinata setspawn
  spawn-location: "default"
  # Jugadores mínimos conectados para iniciar el evento
  min-players: 3
  # Avisos previos en minutos antes del inicio
  warnings:
    - 15
    - 5
    - 1
```

---

## 🔄 Sincronizador Inteligente: `ConfigUpdaterEngine`

Uno de los problemas más molestos al actualizar plugins de Minecraft es que las nuevas opciones borran tus configuraciones personalizadas o requieren borrar carpetas.

**En PinataSpectra Lite, esto nunca pasa:**

* Al iniciar o al ejecutar `/pinata reload`, el motor `ConfigUpdaterEngine`:
  1. Lee el archivo JAR oficial en busca de nuevas opciones, comentarios o claves de traducción.
  2. Las inyecta de forma quirúrgica en tus archivos del disco (`config.yml`, `messages.yml`, `messages_es.yml`, `pinatas/*.yml`).
  3. **Preserva el 100% de tus valores personalizados, ítems de drop y coordenadas guardadas sin sobrescribir nada.**

---

<div align="center">

[**← 07. Metas de Votos & Pool**](07-Metas-de-Votos-y-Pool-Vault.md) | [**09. Configuración YAML Maestra →**](09-Configuracion-YAML-Maestra.md)

</div>
