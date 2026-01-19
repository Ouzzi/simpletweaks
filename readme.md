# 🛠️ Simpletweaks
![Fabric](https://img.shields.io/badge/Loader-Fabric-be7653?style=for-the-badge&logo=fabric)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21.x-brightgreen?style=for-the-badge&logo=minecraft)
![License](https://img.shields.io/badge/License-Apache%202.0-blue?style=for-the-badge)

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

### 🪽 Elytra Pads
Expand your flight network beyond spawn!
* **Function:** Standing on an Elytra Pad equips the player with a Spawn Elytra (or recharges it) and applies a glowing effect.
* **Tiers:** Upgradable pads with increasing radius and height range.
    * **Tier 1:** 5x15 Range (Crafted with Diamond Pressure Plate)
    * **Tier 2:** 15x31 Range (Reinforced)
    * **Tier 3:** 31x63 Range (Netherite)
    * **Tier 4:** 63x127 Range (Fine/Nether Star)
* **First Join Gift:** Players receive a Tier 1 Elytra Pad on their first join to start building their hub.

### 💎 Diamond Pressure Plate
A highly specialized redstone component.
* **Player-Only:** This pressure plate activates **only** when a player steps on it. Mobs and items are ignored.
* **Crafting:** 2 Diamonds in a horizontal row.
* **Usage:** Perfect for secure doors, traps, or triggering Elytra Pads without accidental mob activation.

### 🌌 Spawn Teleporter
A personal teleportation pad to get you back to the world spawn quickly.
* **First Join Gift:** Players receive a configurable amount (Default: 1) on their first join.
* **Usage:** Place the pad and stand still on it for **5 seconds** to teleport to the world spawn.
* **Ownership:** Only the player who placed the teleporter can break it. It is indestructible to others.
* **Crafting:** Can be crafted in a Smithing Table: `Netherite Upgrade` + `Gold Pressure Plate` + `Netherite Ingot`.

### 🌪️ Launchpad
A powerful jump pad powered by wind.
* **Crafting:** `Wind Charge` + `Heavy Weighted Pressure Plate` + `Wind Charge` (Vertical pattern).
* **Charging:** Right-click with **Wind Charges** to fuel the pad (Max: 16 charges).
* **Launch:** Stand on the pad for 3 seconds to be launched high into the air.
* **Variable Power:** The more charges stored, the stronger the launch!
* **Visuals:** Emits wind particles when charged and displays the current power level via sound and particles.

### 🕊️ Flypads (Creative Flight)
Experience true freedom within your base!
* **Function:** Grants **Creative Flight** to players within its radius. Players glow to indicate the effect is active.
* **Tiers:** Same range upgrades as Elytra Pads:
    * **Tier 1:** 5x15 Range (Smithing: Netherite Upgrade + Netherite Ingot + Tier 4 Elytra Pad)
    * **Tier 2:** 15x31 Range (Smithing: Netherite Upgrade + Netherite Block + Tier 1 Flypad)
    * **Tier 3:** 31x63 Range (Crafting: Netherite Block, Nether Star, Tier 2 Flypad, Ominous Keys)
    * **Tier 4:** 63x127 Range (Crafting: Nether Star, Tier 3 Flypad, Ominous Keys) - Massive range!

### ⬛ Netherite Pressure Plate
The ultimate security filter.
* **Item Filter:** If placed on top of a **Barrel**, this plate only activates if the player holding an item that matches one inside the barrel.
* **Smart Detection:** Without a barrel, it acts as a heavy-duty player detector.
* **Grief Protection:** Like other pads, the owner can break it instantly, while others take significantly longer.


## 🛡️ Admin & Server Tools

* **📍 Exact Spawn:** Force players to spawn exactly on a block (or pillow) - no random radius.
* **🌍 Dimensions:** Globally disable the Nether or End until you are ready.
* **🧹 Lag Killer:** Remove empty boats and minecarts with a single command.

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

**🔧 More Gameplay Tweaks:**

    /simpletweaks tweaks hoeHarvest <true|false>
    /simpletweaks tweaks locator <true|false>
    /simpletweaks tweaks xpClumps enable <true|false>
    /simpletweaks tweaks xpClumps scale <true|false>

**🧱 Throwable Bricks:**

    /simpletweaks tweaks bricks enable <true|false>
    /simpletweaks tweaks bricks breakGlass <true|false>
    /simpletweaks tweaks bricks damage <value>
    /simpletweaks tweaks bricks snowballDamage <value>
**👶 Baby Suffixes:**

    /simpletweaks tweaks babySuffixes list
    /simpletweaks tweaks babySuffixes add <suffix>
    /simpletweaks tweaks babySuffixes remove <suffix>
**⚡ Speed Lines:**

    /simpletweaks visuals speedLines enable <true|false>
    /simpletweaks visuals speedLines color <hex_value>
    /simpletweaks visuals speedLines alpha <0.0-1.0>
    /simpletweaks visuals speedLines amount <value>
    /simpletweaks visuals speedLines threshold <value>

**📥 Pickup Notifier:**

    /simpletweaks visuals pickupNotifier enable <true|false>
    /simpletweaks visuals pickupNotifier scale <value>
    /simpletweaks visuals pickupNotifier duration <ticks>
    /simpletweaks visuals pickupNotifier opacity <0.0-1.0>
    /simpletweaks visuals pickupNotifier showXp <true|false>
    /simpletweaks visuals pickupNotifier vanillaStyle <true|false>

**Notifier Layout & Position:**

    /simpletweaks visuals pickupNotifier offset <x> <y>
    /simpletweaks visuals pickupNotifier side <left|right>
    /simpletweaks visuals pickupNotifier layout <icon_name_count|...>
    /simpletweaks visuals pickupNotifier elements item <true|false>
    /simpletweaks visuals pickupNotifier elements name <true|false>
    /simpletweaks visuals pickupNotifier elements count <true|false>

**🖥️ HUD & Overlay Extras:**

    /simpletweaks visuals statusBars <true|false>
    /simpletweaks visuals chatHeads <true|false>
    /simpletweaks visuals elytraHelper enable <true|false>
    /simpletweaks visuals elytraHelper angles <up_angle> <down_angle>
    /simpletweaks visuals elytraHelper tolerance <value>
    /simpletweaks visuals elytraHelper sensitivity <value>

**📍 Spawn Teleporter:**

    /simpletweaks spawn teleporterCount <amount>



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