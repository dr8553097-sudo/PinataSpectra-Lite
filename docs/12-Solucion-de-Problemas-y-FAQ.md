# ❓ 12. Solución de Problemas & FAQ

Guía de diagnóstico para resolver las dudas y problemas más frecuentes al configurar PinataSpectra-Lite.

---

## ❓ Preguntas Frecuentes (FAQ)

### 1. ¿Por qué los jugadores no escuchan la música de la piñata?
- Verifica que `music.enabled: true` esté activado en `config.yml`.
- La música se reproduce en el canal **Master** de audio de Minecraft. Si el jugador tiene el volumen general silenciado, no escuchará la pista.

### 2. ¿La piñata puede quedarse atascada dentro de una montaña o bloque?
- No. PinataSpectra-Lite cuenta con el algoritmo `findSafeGroundY` que calcula la altura real del suelo sólido y la mantiene siempre suspendida a `+2.35` bloques en el aire.

### 3. ¿El plugin genera lag al golpear la piñata?
- No. Todas las físicas, balanceo y partículas se ejecutan de forma asíncrona o en tareas optimizadas sin bloquear el hilo principal del servidor.
- El modelo utiliza entidades Display que son renderizadas por el cliente de Minecraft en GPU, con cero impacto en el TPS del servidor.

### 4. ¿Cómo reportar un error o sugerir una mejora?
- Puedes abrir un issue directamente en nuestro repositorio de GitHub utilizando las plantillas de [Bug Report](https://github.com/dr8553097-sudo/PinataSpectra-Lite/issues/new/choose) o unirte a nuestro Discord oficial de soporte.
