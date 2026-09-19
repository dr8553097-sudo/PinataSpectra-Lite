# ⚙️ 02. Instalación & Requisitos

## 📋 Requisitos del Servidor

- **Plataforma:** [Paper](https://papermc.io), [Purpur](https://purpurmc.org) o Spigot.
- **Versión de Minecraft:** 1.20.4, 1.20.6, 1.21.x, 1.22+ hasta 1.26+.
- **Java Runtime:** Java 21 LTS o Java 25 LTS requerido.

---

## 🔌 Plugins Opcionales (Recomendados)

1. **[Vault](https://www.spigotmc.org/resources/vault.34315/):** Permite recompensas monetarias para el MVP, Top 3 golpeadores y participantes comunitarios.
2. **[NuVotifier](https://www.spigotmc.org/resources/nuvotifier.13442/):** Activa el sistema de meta de votos para spawnear la piñata automáticamente al alcanzar los votos deseados.

---

## 🚀 Guía de Instalación Paso a Paso

1. Descarga el archivo `PinataSpectra-Lite-1.0.0.jar`.
2. Coloca el archivo JAR dentro del directorio `/plugins/` de tu servidor.
3. Inicia o reinicia el servidor.
4. PinataSpectra-Lite generará automáticamente la carpeta `/plugins/PinataSpectra-Lite/` con los archivos:
   - `config.yml` (Configuración general del motor).
   - `messages.yml` (Textos traducibles con soporte MiniMessage).
   - `pinatas/festive_llama.yml` (Perfil festivo por defecto).
   - `pinatas/custom_party.yml` (Perfil celestial party).
5. Configura una ubicación de spawn con el comando:
   ```bash
   /pinata setspawn default
   ```
6. ¡Listo! Puedes invocar tu primera piñata con `/pinata spawn`.
