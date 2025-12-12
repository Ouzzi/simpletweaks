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

### 🌪️ Launchpad
A powerful jump pad powered by wind.
* **Crafting:** `Wind Charge` + `Heavy Weighted Pressure Plate` + `Wind Charge` (Vertical pattern).
* **Charging:** Right-click with **Wind Charges** to fuel the pad (Max: 16 charges).
* **Launch:** Stand on the pad for 3 seconds to be launched high into the air.
* **Variable Power:** The more charges stored, the stronger the launch!
* **Visuals:** Emits wind particles when charged and displays the current power level via sound and particles.

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

## 🖥️ HUD & Visual Enhancements

### 🧭 Locator Heads
Find your friends easily!
* **Compass Bar:** Shows a bar at the top of the screen displaying the **heads of nearby players**.
* **Directional:** The heads move left/right based on your viewing angle, acting like a compass.
* **Immersive:** Replaces abstract markers with actual player skins.

### 💬 Chat Heads
See who is talking.
* **Visual Chat:** Displays the player's head next to their chat messages.
* **Opacity:** The heads fade out together with the chat text.

### 🧪 Status Effect Bars
Never guess your potion duration again.
* **Duration Bar:** Adds a colored durability-like bar under status effect icons in the **HUD** and **Inventory**.
* **Visual:** The bar shrinks as the effect wears off, matching the effect's color (e.g., pink for Regeneration).

### 📉 Elytra Pitch Helper
Master the perfect flight.
* **40/40 Guide:** Displays visual guide lines when you are gliding near the optimal pitch of **±40°**.
* **Alignment:** Align the line with your crosshair for maximum speed or altitude gain.

## 🔥 Fun & Quality of Life

* **🟢 XP Clumps:** Groups XP orbs into single entities to reduce lag.
    * **Instant Pickup:** No more waiting for the "ding-ding-ding" cooldown; pick up all XP instantly.
    * **Visual Scaling:** Orbs with more XP appear physically larger.
* **💨 YEET!** Hold `Shift` + `Q` to throw items with great force.
* **🚶 Auto-Walk:** Press `R` (configurable) to walk automatically. Perfect for long trips.
* **🤫 Silence Mobs:** Name tags ending with `_mute` or `_shhh` silence mobs completely.
* **🌾 Crop Protection:** Players with **Feather Falling** boots will not trample farmland.
* **⚔️ Lawn Mower:** Weapons with **Sharpness III+** cut through grass and flowers during combat.
* **🪜 Faster Ladders:** Climb ladders noticeably faster.

## 🛡️ Admin & Server Tools

* **💀 PvP Heads:** Players drop their head (skin included) upon death in PvP.
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