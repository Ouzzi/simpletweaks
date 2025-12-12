# Simpletweaks

**Simpletweaks** is a Fabric mod for **Minecraft 1.21** that adds useful Quality-of-Life features, server management tools, and fun gameplay mechanics.

---

## ✨ Features

### 🚀 Spawn Elytra Mechanic
A unique system designed for server lobbies or spawn areas.
* **Temporary Flight:** Players automatically receive a special "Spawn Elytra" within the configured spawn radius.
* **Boost System:** Press `Space` while gliding to trigger a speed boost.
* **HUD Integration:** Replaces the vanilla XP bar (or renders above it) with a custom **blue boost bar** and a timer.
* **Timer & Zone:** If a player leaves the spawn area, a countdown starts. Upon expiration or landing outside the zone, the Elytra is removed.
* **Safety:** The Spawn Elytra prevents **fall damage** and **kinetic damage** (flying into walls).
* **Auto-Equip:** Automatically equips the Elytra if the chest slot is empty when entering the spawn area.

![Spawn Elytra In Inventory](https://cdn.modrinth.com/data/cached_images/f366ca4cd22cb7a445599f62257e9a0ac1e7aa10_0.webp)

### 🌌 Spawn Teleporter
A personal teleportation pad to get you back to the world spawn quickly.
* **First Join Gift:** Players receive a configurable amount (Default: 1) on their first join.
* **Usage:** Place the pad and stand still on it for **5 seconds** to teleport to the world spawn.
* **Ownership:** Only the player who placed the teleporter can break it. It is indestructible to others.
* **Crafting:** Can be crafted in a Smithing Table: `Netherite Upgrade` + `Gold Pressure Plate` + `Netherite Ingot`.

### 🧱 Throwable Bricks
Turn construction materials into dangerous projectiles!
* **Vanilla Items:** Standard **Bricks** and **Nether Bricks** can now be thrown by right-clicking.
* **Damage:** They deal significant damage on impact (Default: 2 Hearts). Configurable.
* **Physics:** Thrown bricks can shatter **Glass** and **Glass Panes** (but not Tinted Glass). This can be disabled in the config.
* **Drop:** The brick item drops on the ground after impact.

### ❄️🧱 Brick Snowball
A nasty surprise wrapped in snow.
* **Crafting:** Combine **3 Snowballs** and **1 Brick** to craft.
* **Effect:** Throws like a snowball but deals physical damage (Default: 1 Heart) and breaks glass.
* **Visuals:** Emits a mix of snowball and brick particles upon impact.

### 💨 "Yeet" Item Throw
Throw items further than ever before!
* **Action:** Hold `Sneak (Shift)` + `Drop (Q)` to throw an item.
* **Effect:** The item is launched at high velocity in your viewing direction.
* **Configurable:** Adjust the throw strength in the config or via commands.

### 🚶 Auto-Walk
* **Toggle:** Press the configured key (Default: `R`) to walk forward automatically.
* **Feedback:** A chat message confirms toggling On/Off ("Auto-Walk: ON").
* **Usage:** Perfect for long journeys, Elytra flights, or AFK travel.

### 🤫 Mob Muting (Name Tags)
Silence annoying mobs without commands.
* **How to:** Rename a Name Tag to end with a configured suffix (Default: `_mute` or `_shhh`).
* **Effect:** Applying it to a mob makes it completely silent (`isSilent = true`).
* **Example:** "Cow_mute"

### 💀 PvP Player Heads
* When a player is killed by another player, they drop their head.
* Includes the player's skin data (using 1.21 ProfileComponents).

### 🌾 Farmland Protection
* Players wearing boots with **Feather Falling** enchantment will not trample farmland when jumping on it.

### ⚔️ Sharpness Cuts Grass
* Weapons (Swords/Axes) with **Sharpness III** or higher automatically cut through grass and flowers when attacking entities, preventing obstructed hits.

### 🪜 Faster Ladders
* Increases the vertical climbing speed on ladders (Configurable).

### 🌍 Dimension Management
* Globally disable access to the **Nether** or **End**.
* Players trying to enter a disabled dimension are blocked and notified.

### 📍 World Spawn Management
* **Exact Spawn:** Force players to spawn exactly at the specific block coordinates (or on a bed cushion), removing the vanilla spawn radius fuzzing.
* **Custom Spawn:** Set custom world spawn coordinates independent of the vanilla world data.

### ⚖️ Balancing
* **Rocket Stack Size:** Nerf Elytra flight by reducing the stack size of firework rockets (e.g., to 16).
* **Vault Cooldown:** Set a cooldown (in days) for looting Vaults to prevent farming.

---

## 🛠 Commands

> ⚠️ All commands require OP privileges (Level 2 or 4).

![Commands](https://cdn.modrinth.com/data/cached_images/581d2d3364c23cfd984cead6de7480edbe49da64_0.webp)

### 🧹 Cleanup Commands
Remove entities to reduce lag. Supports different modes:
* `standard`: Removes normal entities only.
* `empty`: Removes normal entities AND empty storage variants (e.g., Chest Boats).
* `all`: Removes everything (including filled chests, TNT, etc.).

**Boats:**

    /killboats
    /killboats standard
    /killboats empty
    /killboats all

**Minecarts:**

    /killcarts
    /killcarts standard
    /killcarts empty
    /killcarts all

### ⚙️ Configuration Commands
Change settings directly in-game without restarting.

**Spawn Elytra:**

    /simpletweaks spawn elytra toggle <true|false>
    /simpletweaks spawn elytra radius <blocks>
    /simpletweaks spawn elytra flightTime <seconds>
    /simpletweaks spawn elytra maxBoosts <amount>
    /simpletweaks spawn elytra boostStrength <value>

**Spawn Elytra Center:**

    /simpletweaks spawn elytra center worldspawn
    /simpletweaks spawn elytra center here
    /simpletweaks spawn elytra center set <x> <z>

**World Spawn:**

    /simpletweaks worldspawn forceExact <true|false>
    /simpletweaks worldspawn here
    /simpletweaks worldspawn set <x> <y> <z>

**Dimensions:**

    /simpletweaks dimension nether <true|false>
    /simpletweaks dimension end <true|false>

**Tweaks & Features:**

    /simpletweaks tweaks autowalk <true|false>
    /simpletweaks tweaks yeet toggle <true|false>
    /simpletweaks tweaks yeet strength <value>
    /simpletweaks tweaks farmlandProtect <true|false>
    /simpletweaks tweaks ladderSpeed <value>
    /simpletweaks tweaks sharpnessCut <true|false>

**Mute Suffixes Management:**

    /simpletweaks tweaks muteSuffixes list
    /simpletweaks tweaks muteSuffixes add <suffix>
    /simpletweaks tweaks muteSuffixes remove <suffix>
    /simpletweaks tweaks muteSuffixes clear

**Balancing & PvP:**

    /simpletweaks balancing rocketStackSize <amount>
    /simpletweaks vaults cooldown <days>
    /simpletweaks pvp headDrops <true|false>

**Enable/Disable Commands:**

    /simpletweaks commands enableKillBoats <true|false>
    /simpletweaks commands enableKillCarts <true|false>



![Customization In Mod Menu](https://cdn.modrinth.com/data/cached_images/bdb4eeb974a3185fa55c16813ed25586906a88b6_0.webp)

---

## 📦 Installation

1. Install **Fabric Loader** for Minecraft 1.21.
2. Download `simpletweaks` and place it in the `mods` folder.
3. Install required dependencies.

**Dependencies:**
* [Fabric API](https://modrinth.com/mod/fabric-api)
* [Cloth Config API](https://modrinth.com/mod/cloth-config)

**Optional:**
* [Mod Menu](https://modrinth.com/mod/modmenu) (To edit the config via graphical interface).