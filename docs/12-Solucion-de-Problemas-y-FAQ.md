# 💡 12. Solución de Problemas, Diagnóstico & FAQ

Guía rápida de resolución de incidencias comunes y buenas prácticas de administración.

---

## ❓ Preguntas Frecuentes (FAQ)

### 1. ¿Por qué la piñata no recibe daño cuando los jugadores la golpean?
* Comprueba si la región del evento tiene bloqueado el daño a entidades o PvP en WorldGuard (`pvp: deny` o `damage: deny`).
* Asegúrate de que los jugadores tengan el permiso `pinataspectra.user` si has configurado restricciones.

### 2. ¿Cómo limpio entidades residuales si el servidor se apagó forzadamente durante un evento?
* Ejecuta:  
  ```bash
  /pinata clean
  ```
  El motor `PinataCleanEngine` escaneará todos los mundos cargados y eliminará de forma segura cualquier `ItemDisplay` o `Interaction` residual huérfano.

### 3. ¿Cómo cambio el idioma a Español?
* Simplemente ejecuta en la consola o en el chat:  
  ```bash
  /pinata lang ES
  ```
  Los mensajes cambiarán inmediatamente sin reiniciar el servidor.

---

## 🐛 Reporte de Errores & Comunidad

Si encuentras algún comportamiento inesperado o deseas sugerir una nueva función:
* 🐞 [Reportar un Error (GitHub Issues)](https://github.com/dr8553097-sudo/PinataSpectra-Lite/issues/new?template=bug_report.yml)
* 💡 [Sugerir una Mejora](https://github.com/dr8553097-sudo/PinataSpectra-Lite/issues/new?template=feature_request.yml)

---

<div align="center">

[**← 11. Placeholders & Hooks**](11-Placeholders-y-Hooks.md) | [**Volver al Inicio 🏠**](README.md)

</div>
