# Simpletweaks

**Simpletweaks** is a Fabric mod for **Minecraft 1.21** that adds useful Quality-of-Life features, server management tools, and fun gameplay mechanics.

---

## ✨ Features

### 🚀 Spawn Elytra Mechanic
A unique system for server lobbies or spawn areas.

* **Temporary Flight:** Players automatically receive a special Elytra within the spawn area (configurable radius).
* **Boost System:** Press `Space` while gliding to trigger a speed boost.
* **HUD Integration:** While wearing the Elytra, a **blue boost bar** replaces the XP bar to show remaining boost energy.
* **Timer & Zone:** If a player leaves the spawn area, a timer starts. Upon expiration or landing outside the zone, the Elytra is removed automatically.
* **Safety:**
    * The Elytra is **unremovable** (vanishes if moved) and **unbreakable**.
    * **No fall damage** and **no kinetic damage** (wall impact) while wearing the Spawn Elytra.
* **Re-Entry:** Re-entering the spawn area refills or re-equips the Elytra.

### 💨 "Yeet" Item Throw
Throw items further than ever before!

* **Charge:** Hold `Sneak (Shift)` + `Drop (Q)` to charge the throw.
* **Display:** A chat bar indicates the current throw strength.
* **Fire:** Release the Drop key to launch the item (or `Ctrl`+`Q` for the whole stack) at high velocity in your viewing direction.

### 🚶 Auto-Walk
* Press the configured key (Default: `R`) to walk forward automatically.
* Perfect for long journeys, Elytra flights, or AFK travel.
* A chat message confirms toggling On/Off.

### 🤫 Mob Muting (Name Tags)
Annoying animal noises?

* Rename a mob with a Name Tag ending in a configured suffix (Default: `_mute` or `_shhh`).
* The mob is immediately muted (`isSilent = true`).
* *Example:* "Cow_mute"

### 💀 PvP Player Heads
* When a player is killed by another player, they drop their head (including skin).
* Uses the modern **1.21 ProfileComponent logic** for correct skin rendering.

### 🌍 Dimension Management
* Globally disable access to the Nether or End via config or commands.
* Useful for servers that want to unlock dimensions later.
* If a player tries to use a portal, they are blocked and receive a message.

### 📍 World Spawn Management
* **Exact Spawn:** Force players to spawn exactly at the spawn coordinates (or on a bed cushion), instead of within a random radius around it.
* **Custom Spawn:** Set custom coordinates for the world spawn, independent of the vanilla spawn point.

---

## 🛠 Commands

> ⚠️ All commands require OP privileges (Level 2 or 4).

### Cleanup (Lag Removal)
* `/killboats` - Removes all empty boats within a radius of 100 blocks.
* `/killcarts` - Removes all empty Minecarts within a radius of 100 blocks.

### Configuration (Ingame)
Change settings directly in-game without restarting:

**Manage Dimensions:**
```mcfunction
/simpletweaks dimension nether <true|false>
/simpletweaks dimension end <true|false>
```

**Spawn Elytra Settings:**
```mcfunction
/simpletweaks spawn elytra toggle <true|false>
/simpletweaks spawn elytra radius <blocks>
```

**Set Spawn Elytra Center:**
```mcfunction
/simpletweaks spawn elytra center worldspawn   # Uses the World Spawn
/simpletweaks spawn elytra center here         # Uses your current position
/simpletweaks spawn elytra center set <x> <z>  # Manual coordinates
```

## ⚙️ Configuration

The mod uses **Cloth Config** and **Mod Menu**. You can change all settings comfortably in-game via the Mod Menu (Client) or via the `config/simpletweaks.json` file.

**Categories:**
* **Balancing:** Stack size for firework rockets (Nerf Elytra).
* **Vaults:** Cooldown for Vault looting.
* **PvP:** Toggle head drops on/off.
* **Dimensions:** Control Nether/End access.
* **Spawn:** Elytra radius, flight duration, boost strength, spawn center.
* **World Spawn:** Force exact spawn, custom coordinates.
* **Commands:** Enable/disable individual commands.
* **Tweaks:** AutoWalk, Yeet strength, Mute suffixes.

---

## 📦 Installation

1. Install the **Fabric Loader** for Minecraft 1.21.
2. Download the mod and place it in the `mods` folder.
3. Ensure dependencies are installed.

**Required Dependencies:**
* Fabric API
* Cloth Config API

**Recommended:**
* Mod Menu (To edit the config in-game).

---

## 📝 License
This mod was developed for **Fabric 1.21**.