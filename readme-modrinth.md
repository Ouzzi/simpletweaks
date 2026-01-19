# ✨ Simpletweaks

**Your Server Upgrade for 1.21**

![Icon Banner](https://cdn.modrinth.com/data/cached_images/d44fc9268ce55b9dc8fd0ce7ea60b1f38b9b90ba_0.webp)

**Simpletweaks** is the all-in-one solution to make your server or singleplayer world more lively, cleaner, and fun. No more boring lobbies or annoying micromanagement!

**[📚 Read the Full Documentation](https://github.com/Ouzzi/simpletweaks)**

## 🚀 Spawn & Travel Features

### 🪽 Spawn Elytra
Turn your spawn into a playground!
* **Auto-Equip:** Automatically receive a special, unbreakable Elytra within the spawn area.
* **Boost System:** Press **Space** while gliding to accelerate like a rocket!
* **Custom HUD:** A fancy **blue boost bar** replaces the XP bar to show your energy levels.
* **Safety:** No fall damage and no kinetic damage when hitting walls.

### 🛫 Elytra Pads
Expand your flight network far beyond spawn!
* **Function:** Stand on an Elytra Pad to equip (or recharge) a Spawn Elytra and receive a glowing effect.
* **Tiers:** Upgradable pads with increasing radius and height range.
    * **Tier 1:** 5x15 Range (Crafted with Diamond Pressure Plate)
    * **Tier 2:** 15x31 Range (Reinforced)
    * **Tier 3:** 31x63 Range (Netherite)
    * **Tier 4:** 63x127 Range (Fine/Nether Star)
* **Starter Gift:** Players receive a Tier 1 Pad upon their first join to start building their hub.

### 🌪️ Launchpad
A powerful jump pad powered by wind energy.
* **Crafting:** `Wind Charge` + `Heavy Weighted Pressure Plate` + `Wind Charge` (Vertical).
* **Charging:** Right-click with **Wind Charges** to fuel the tank (Max: 16 charges).
* **Launch:** Stand still for 3 seconds to be launched high into the air.
* **Variable Power:** The fuller the tank, the stronger the boost!
* **Visuals:** Indicates fill level and charge status via wind particles and sounds.

### 🕊️ Flypads
Experience true freedom in your base!
* **Function:** Grants players **Creative Flight** within its radius. A glowing effect indicates the mode is active.
* **Tiers:** Same range upgrades as Elytra Pads:
    * **Tier 1:** 5x15 Range (Smithing: Netherite Upgrade + Netherite Ingot + Tier 4 Elytra Pad)
    * **Tier 2:** 15x31 Range (Smithing: Netherite Upgrade + Netherite Block + Tier 1 Flypad)
    * **Tier 3:** 31x63 Range (Crafting: Netherite Block, Nether Star, Tier 2 Flypad, Ominous Keys)
    * **Tier 4:** 63x127 Range (Crafting: Nether Star, Tier 3 Flypad, Ominous Keys) - Massive range!

### ⬛ Netherite Pressure Plate
The ultimate security filter.
* **Item Filter:** If placed on top of a **Barrel**, this plate only activates if the player has an item in their inventory that matches one inside the barrel (Whitelist).
* **Smart Detection:** Without a barrel, it acts as a heavy-duty player detector.
* **Grief Protection:** Like all pads, the owner can break it instantly, while strangers take significantly longer.

### 💎 Diamond Pressure Plate
A highly specialized redstone component.
* **Player-Only:** This pressure plate activates **only** when a player steps on it. Mobs and items are ignored.
* **Crafting:** 2 Diamonds horizontally.
* **Usage:** Perfect for secure doors, traps, or triggering Elytra Pads without accidental mob activation.

### 🌌 Spawn Teleporter
A personal teleport pad for a quick way home.
* **Move-in Gift:** Players receive a configurable amount (Default: 1) upon their first join.
* **Usage:** Place the pad and stand still on it for **5 seconds** to teleport to the world spawn.
* **Ownership:** Only the player who placed the teleporter can break it. Magic particles appear for the owner.
* **Crafting:** `Netherite Upgrade` + `Gold Pressure Plate` + `Netherite Ingot` in a Smithing Table.

## 🛡️ Admin & Server Tools

* **📍 Exact Spawn:** Force players to spawn exactly on a block (or pillow) – no more random radius!
* **🌍 Dimensions:** Globally disable the Nether or End until you are ready.
* **🧹 Lag Killer:** Remove empty boats and minecarts with a single command.

---

<details>
<summary><strong>📜 Command Overview (Click to expand)</strong></summary>

All commands require OP privileges.

**Cleanup:**

    /killboats [standard|empty|all]
    /killcarts [standard|empty|all]

**Configuration (In-game):**

    /simpletweaks spawn elytra ...
    /simpletweaks tweaks ...
    /simpletweaks worldspawn ...
    /simpletweaks dimension ...

</details>

---

### 📦 Installation
Requires **Fabric Loader**, **Fabric API**, and **Cloth Config API**.
Optional: **Mod Menu** for easy configuration.