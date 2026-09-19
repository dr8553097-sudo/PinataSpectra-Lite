# 🕹️ 10. Comandos & Permisos

## 💻 Tabla de Comandos

| Comando | Permiso Requerido | Descripción |
| :--- | :--- | :--- |
| `/pinata spawn [perfil]` | `pinataspectra.admin` | Invoca una piñata en la ubicación actual o punto guardado. |
| `/pinata kill` | `pinataspectra.admin` | Elimina la piñata activa inmediatamente sin entregar drops. |
| `/pinata setspawn <nombre>` | `pinataspectra.admin` | Guarda un punto de aparición con el nombre indicado. |
| `/pinata reload` | `pinataspectra.admin` | Recarga todas las configuraciones YAML, perfiles y mensajes. |
| `/pinata studio [perfil]` | `pinataspectra.admin` | Abre el menú GUI interactivo para editar parámetros de la piñata. |
| `/pinata bat give <jugador>` | `pinataspectra.admin` | Entrega un Bate Festivo con daño aumentado. |
| `/pinata pool [cantidad]` | `pinataspectra.use` | Consulta o aporta dinero al fondo comunitario de fiesta. |
| `/pinata pro` | *Ninguno* | Muestra la comparativa interactiva con la edición Sovereign PRO. |
| `/vote` | `pinataspectra.use` | Muestra el estado de la meta de votos y enlaces para votar. |
| `/clean` | `pinataspectra.admin` | Purga entidades o displays huérfanos del mundo. |

---

## 🔐 Nodos de Permiso

- `pinataspectra.admin`: Concede acceso total a todos los comandos administrativos y de configuración.
- `pinataspectra.use`: Permite ver la información de `/vote` y donar con `/pinata pool`.
- `pinataspectra.hit.2`: Multiplicador VIP que cuenta cada impacto como **2 golpes**.
- `pinataspectra.hit.3`: Multiplicador VIP que cuenta cada impacto como **3 golpes**.
- `pinataspectra.hit.5`: Multiplicador VIP que cuenta cada impacto como **5 golpes**.
