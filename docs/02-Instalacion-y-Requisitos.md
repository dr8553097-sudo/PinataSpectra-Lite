# ⚙️ 02. Instalación & Requisitos

La instalación de **PinataSpectra Lite** está pensada para ser *Plug-and-Play*: lista para usar en menos de 60 segundos sin necesidad de configuraciones tediosas.

---

## 📋 Requisitos del Servidor

* **Java:** **Java 21** o superior (totalmente compatible con Java 25).
* **Plataforma del Servidor:**
  * ✅ **Paper** (1.20.4, 1.21, 1.22, 1.26+) — *Recomendado*
  * ✅ **Purpur** (1.20.4+)
  * ✅ **Folia** (Soporte multihilo experimental)
  * ⚠️ *Spigot tradicional:* No recomendado debido a limitaciones de eventos en Display Entities modernas.
* **Cliente de Minecraft:** Cualquier versión 1.20.4 o superior (No requiere mods ni resource packs).

---

## 🔌 Integraciones Opcionales (Hooks)

PinataSpectra Lite detecta e integra automáticamente los siguientes plugins si están presentes en tu carpeta `/plugins`:

| Plugin | Propósito en PinataSpectra Lite |
|---|---|
| 💰 **Vault + Economy** (ej. EssentialsX) | Habilita el pozo de donaciones comunitaria (`/pinata pool`) y recompensas de dinero. |
| 🗳️ **NuVotifier / Votifier** | Permite invocar automáticamente la piñata al alcanzar una meta de votos (`vote-goal`). |
| 🧩 **PlaceholderAPI (PAPI)** | Expone estadísticas de votos, pozo actual y estado de la piñata en tablists/scoreboards. |
| 💬 **Discord Webhooks** | Envía alertas en tiempo real al canal de tu comunidad de Discord al iniciar o vencer la piñata. |

---

## 🚀 Guía de Instalación Paso a Paso

1. Descarga el archivo `PinataSpectra-Lite-1.0.0.jar` desde [GitHub Releases](https://github.com/dr8553097-sudo/PinataSpectra-Lite/releases) o tu plataforma autorizada.
2. Coloca el archivo `.jar` dentro del directorio `plugins/` de tu servidor.
3. Inicia o reinicia el servidor.
4. ¡Listo! El motor creará automáticamente la carpeta `plugins/PinataSpectra-Lite/` con los archivos:
   * `config.yml` (Opciones generales y mecánicas).
   * `messages.yml` (Mensajes en inglés por defecto).
   * `messages_es.yml` (Mensajes en español).
   * `pinatas/festive_llama.yml` (Perfil predeterminado de piñata).
   * `pinatas/custom_party.yml` (Perfil de ejemplo personalizable).

> [!TIP]
> **Cambio de Idioma Rápido:**  
> Puedes cambiar todos los mensajes del servidor a español ejecutando en el chat:  
> `/pinata lang ES`

---

<div align="center">

[**← 01. Introducción**](01-Introduccion-y-Filosofia.md) | [**03. Modelos 3D & Físicas →**](03-Modelos-3D-Voxel-y-Fisicas.md)

</div>
