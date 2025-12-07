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

    public static class Commands {
        @ConfigEntry.Gui.Tooltip
        public boolean enableKillBoatsCommand = true;
    }

    public static class Tweaks {
        @ConfigEntry.Gui.Tooltip
        public boolean enableAutowalk = true;

        @ConfigEntry.Gui.Tooltip
        public List<String> nametagMuteSuffixes = new ArrayList<>(Arrays.asList("_mute", "_shhh"));
    }
}