# 🎮 10. Comandos, Permisos & Bate Festivo

PinataSpectra Lite incluye una suite completa de comandos administrativos e interactivos con soporte completo de autocompletado en Tab.

---

## 🕹️ Lista de Comandos

| Comando | Permiso | Descripción |
|---|---|---|
| `/pinata help` | *Ninguno* | Muestra el menú de ayuda visual con todos los comandos. |
| `/pinata spawn [perfil] [spawn]` | `pinataspectra.admin` | Invoca una piñata en tu posición o en un spawn guardado. |
| `/pinata kill` | `pinataspectra.admin` | Elimina la piñata activa de inmediato. |
| `/pinata bat [give <jugador>]` | `pinataspectra.admin` | Entrega el Bate Festivo con partículas y sonido personalizado. |
| `/pinata clean` | `pinataspectra.admin` | Purga entidades residuales o colgadas en todos los mundos. |
| `/pinata setspawn <nombre>` | `pinataspectra.admin` | Guarda tu ubicación actual como un punto de spawn fijo. |
| `/pinata lang <EN\|ES>` | `pinataspectra.admin` | Cambia el idioma activo del servidor (Español / Inglés). |
| `/pinata editor` (o `/pinata studio`) | `pinataspectra.admin` | Abre la GUI del editor visual dentro del juego. |
| `/pinata reload` | `pinataspectra.admin` | Recarga las configuraciones, perfiles y mensajes. |
| `/pinata pool [cantidad]` | *Ninguno* | Consulta el pozo comunitario o dona dinero de Vault. |
| `/pinata vote` (o `/vote`) | *Ninguno* | Consulta el progreso de votos hacia la siguiente fiesta. |
| `/pinata comparison` (o `/pinatapro`)| *Ninguno* | Muestra la tabla comparativa con la edición Sovereign PRO. |

---

## 🔑 Nodos de Permisos

* **`pinataspectra.admin`**: Otorga acceso a todos los comandos administrativos (`spawn`, `kill`, `clean`, `bat`, `setspawn`, `lang`, `reload`, `editor`). Por defecto asignado a operadores (`op`).
* **`pinataspectra.user`**: Permiso base para jugadores comunes (`pool`, `vote`, `help`).

---

<div align="center">

[**← 09. Configuración YAML**](09-Configuracion-YAML-Maestra.md) | [**11. Placeholders & Hooks →**](11-Placeholders-y-Hooks.md)

</div>
