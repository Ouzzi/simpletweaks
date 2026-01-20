package com.simpletweaks.config;

import com.simpletweaks.Simpletweaks;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = Simpletweaks.MOD_ID)
public class SimpletweaksConfig implements ConfigData {

    @ConfigEntry.Gui.CollapsibleObject
    public Balancing balancing = new Balancing();

    @ConfigEntry.Gui.CollapsibleObject
    public Dimensions dimensions = new Dimensions();

    @ConfigEntry.Gui.CollapsibleObject
    public Spawn spawn = new Spawn();

    @ConfigEntry.Gui.CollapsibleObject
    public Commands commands = new Commands();


    @ConfigEntry.Gui.CollapsibleObject
    public Optimization optimization = new Optimization();

    @ConfigEntry.Gui.CollapsibleObject
    public Visuals visuals = new Visuals();


    public static class Balancing {
        @ConfigEntry.Gui.Tooltip(count = 2)
        public int rocketStackSize = 64;
    }

    public static class Dimensions {
        @ConfigEntry.Gui.Tooltip
        public boolean allowNether = true;
        @ConfigEntry.Gui.Tooltip
        public boolean allowEnd = true;
    }

    public static class Spawn {
        @ConfigEntry.Gui.Tooltip
        public boolean forceExactSpawn = true;

        @ConfigEntry.Gui.Tooltip
        public boolean disableFallDamageInSpawn = true;

        @ConfigEntry.Gui.Tooltip
        public int xCoordSpawnPoint = 0;
        @ConfigEntry.Gui.Tooltip
        public int yCoordSpawnPoint = -1;
        @ConfigEntry.Gui.Tooltip
        public int zCoordSpawnPoint = 0;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 64)
        public int spawnTeleporterCount = 1;

        @ConfigEntry.Gui.Tooltip
        public boolean giveElytraOnSpawn = false;
        @ConfigEntry.Gui.Tooltip
        public int spawnElytraRadius = 25;
        @ConfigEntry.Gui.Tooltip
        public boolean useWorldSpawnAsCenter = false;
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

        public int spawn1X = 0, spawn1Y = -1000, spawn1Z = 0; // Y=-1000 als "nicht gesetzt" Marker
        // Tier 2
        public int spawn2X = 0, spawn2Y = -1000, spawn2Z = 0;
        // Tier 3
        public int spawn3X = 0, spawn3Y = -1000, spawn3Z = 0;
        // Tier 4
        public int spawn4X = 0, spawn4Y = -1000, spawn4Z = 0;
    }

    public static class Commands {
        @ConfigEntry.Gui.Tooltip
        public boolean enableKillBoatsCommand = true;
        @ConfigEntry.Gui.Tooltip
        public boolean enableKillCartsCommand = false;
    }


    public static class Optimization {

        @ConfigEntry.Gui.Tooltip
        public boolean enableXpClumps = true;
        @ConfigEntry.Gui.Tooltip
        public boolean scaleXpOrbs = true;

    }


    public static class Visuals {


        @ConfigEntry.Gui.CollapsibleObject
        public LaserPointer laserPointer = new LaserPointer();


        public static class LaserPointer {
            @ConfigEntry.Gui.Tooltip
            public boolean enable = true;

            @ConfigEntry.Gui.Tooltip
            public int color = 0xFF0000; // Rot

            @ConfigEntry.Gui.Tooltip
            public float scale = 0.25f; // Größe des Punktes

            @ConfigEntry.Gui.Tooltip
            public int range = 512; // Reichweite

            @ConfigEntry.Gui.Tooltip
            public boolean showLine = false; // Optional: Linie zeichnen
        }

    }
}