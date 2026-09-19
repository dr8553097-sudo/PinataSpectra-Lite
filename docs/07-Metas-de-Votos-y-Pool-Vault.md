# 🗳️ 07. Metas de Votos & Pool Vault

PinataSpectra-Lite incluye dos sistemas de invocación comunitaria automática diseñados para dinamizar y retener a los jugadores en el servidor.

---

## 🗳️ 1. Meta de Votos Comunitaria (Vote Goal)

Al integrar **NuVotifier** o **Votifier**:
- Cada voto de un jugador en listas de servidores incrementa el contador global de votos del servidor.
- Cada X votos (`vote-goal.broadcast-interval`), se anuncia el progreso actual a todo el servidor.
- Al alcanzar la meta fijada (`vote-goal.target-votes: 25`), se activa automáticamente la piñata comunitaria en el punto de spawn configurado.
- Los jugadores pueden consultar el estado de la meta y los enlaces con el comando `/vote`.

---

## 🏦 2. Fondo Comunitario de Dinero (`/pinata pool`)

Permite a los jugadores donar dinero de su saldo de Vault a una bolsa comunitaria:
- **Consulta de Progreso:** Al ejecutar `/pinata pool`, se muestra un mensaje interactivo con el monto actual recaudado, la meta requerida y la barra de progreso porcentual.
- **Aportar Dinero:** Los jugadores pueden donar fondos ejecutando `/pinata pool <cantidad>`.
- Al completarse la meta fijada (`pinata-pool.target-money: 5000.0`), el sistema anuncia la victoria y libera inmediatamente la piñata de fiesta.
- Los fondos donados se guardan y persisten de forma segura entre reinicios del servidor.
