# ⏳ 08. Scheduler & Eventos Automáticos

El **Auto-Scheduler** de PinataSpectra-Lite permite programar eventos automáticos en horarios fijos del día sin necesidad de plugins externos ni comandos cron complejos.

---

## ⚙️ Configuración del Temporizador

En `config.yml`:

```yaml
schedule:
  # Activar el programador de horarios automáticos
  enabled: true
  # Horarios en formato militar 24h (HH:mm) del reloj del servidor
  times:
    - "14:00"
    - "18:30"
    - "22:00"
  # Minutos de aviso previo antes de la invocación
  announce-minutes-before: 5
  # Perfil de piñata a spawnear
  profile: "FESTIVE_LLAMA"
```

---

## 📢 Avisos Previos Globales

5 minutos antes de la hora fijada (o el tiempo configurado en `announce-minutes-before`), el servidor emitirá un anuncio global a todos los jugadores con sonido de campana recordándoles acudir a la zona de eventos para participar.
