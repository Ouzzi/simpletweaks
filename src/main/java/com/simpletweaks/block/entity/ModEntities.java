package com.simpletweaks.block.entity;

import com.simpletweaks.Simpletweaks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys; // WICHTIG: Dieser Import muss da sein
import net.minecraft.util.Identifier;

public class ModEntities {



    public static void registerModEntities() {
        Simpletweaks.LOGGER.info("Registering Mod Entities for " + Simpletweaks.MOD_ID);
    }
}