package com.simpletweaks.config;

import com.simpletweaks.Simpletweaks;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Config(name = Simpletweaks.MOD_ID)
public class SimpletweaksConfig implements ConfigData {

    @ConfigEntry.Gui.CollapsibleObject
    public Balancing balancing = new Balancing();

    @ConfigEntry.Gui.CollapsibleObject
    public Vaults vaults = new Vaults();

    @ConfigEntry.Gui.CollapsibleObject
    public PvpTweaks pvp = new PvpTweaks();

    @ConfigEntry.Gui.CollapsibleObject
    public Dimensions dimensions = new Dimensions();

    @ConfigEntry.Gui.CollapsibleObject
    public Spawn spawn = new Spawn();

    @ConfigEntry.Gui.CollapsibleObject
    public WorldSpawn worldSpawn = new WorldSpawn(); // NEU

    @ConfigEntry.Gui.CollapsibleObject
    public Commands commands = new Commands();

    @ConfigEntry.Gui.CollapsibleObject
    public Tweaks tweaks = new Tweaks();

    public static class Balancing {
        @ConfigEntry.Gui.Tooltip(count = 2)
        public int rocketStackSize = 64;
    }

    public static class Vaults {
        @ConfigEntry.Gui.Tooltip
        public int vaultCooldownDays = 1;
    }

    public static class PvpTweaks {
        @ConfigEntry.Gui.Tooltip
        public boolean playerHeadDrops = true;
    }

    public static class Dimensions {
        @ConfigEntry.Gui.Tooltip
        public boolean allowNether = true;
        @ConfigEntry.Gui.Tooltip
        public boolean allowEnd = true;
    }

    public static class Spawn {
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 64)
        public int spawnTeleporterCount = 1;

        @ConfigEntry.Gui.Tooltip
        public boolean giveElytraOnSpawn = false;
        @ConfigEntry.Gui.Tooltip
        public int spawnElytraRadius = 25;
        @ConfigEntry.Gui.Tooltip
        public boolean useWorldSpawnAsCenter = true;
        @ConfigEntry.Gui.Tooltip
        public int customSpawnElytraX = 0;
        @ConfigEntry.Gui.Tooltip
        public int customSpawnElytraZ = 0;
        @ConfigEntry.Gui.Tooltip
        public int flightTimeSeconds = 300;
        @ConfigEntry.Gui.Tooltip
        public int maxBoosts = 3;
        @ConfigEntry.Gui.Tooltip
        public float boostStrength = 0.6f;
    }

    // NEU: World Spawn Settings
    public static class WorldSpawn {
        @ConfigEntry.Gui.Tooltip
        public boolean forceExactSpawn = true;

        @ConfigEntry.Gui.Tooltip
        public int xCoordSpawnPoint = 0;

        @ConfigEntry.Gui.Tooltip
        public int yCoordSpawnPoint = -1; // -1 = Automatisch (Oberfläche)

        @ConfigEntry.Gui.Tooltip
        public int zCoordSpawnPoint = 0;
    }

    public static class Commands {
        @ConfigEntry.Gui.Tooltip
        public boolean enableKillBoatsCommand = true;
        @ConfigEntry.Gui.Tooltip
        public boolean enableKillCartsCommand = false;
    }

    public static class Tweaks {
        @ConfigEntry.Gui.Tooltip
        public boolean enableAutowalk = true;

        @ConfigEntry.Gui.Tooltip
        public List<String> nametagMuteSuffixes = new ArrayList<>(Arrays.asList("_mute", "_shhh"));

        @ConfigEntry.Gui.Tooltip
        public boolean enableYeet = true;
        @ConfigEntry.Gui.Tooltip
        public float yeetStrength = 3.0f;

        @ConfigEntry.Gui.Tooltip
        public boolean enableThrowableBricks = true;

        @ConfigEntry.Gui.Tooltip
        public boolean throwableBricksBreakBlocks = false;

        @ConfigEntry.Gui.Tooltip
        public float brickDamage = 2.0f;

        @ConfigEntry.Gui.Tooltip
        public float brickSnowballDamage = 2.0f;

        @ConfigEntry.Gui.Tooltip
        public boolean preventFarmlandTrampleWithFeatherFalling = true;

        @ConfigEntry.Gui.Tooltip
        public double ladderClimbingSpeed = 0.4; // Vanilla default is ~0.2

        @ConfigEntry.Gui.Tooltip
        public boolean sharpnessCutsGrass = true;
    }
}