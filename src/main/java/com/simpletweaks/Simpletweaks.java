package com.simpletweaks;

import com.simpletweaks.block.ModBlocks;
import com.simpletweaks.block.entity.ModBlockEntities;
import com.simpletweaks.command.ModCommands;
import com.simpletweaks.component.ModDataComponentTypes;
import com.simpletweaks.config.SimpletweaksConfig;
import com.simpletweaks.event.ClaimProtectionHandler;
import com.simpletweaks.event.FirstJoinHandler;
import com.simpletweaks.event.SpawnHandler;
import com.simpletweaks.event.WorldSpawnHandler;
import com.simpletweaks.item.ModItems;
import com.simpletweaks.network.LaserManager;
import com.simpletweaks.network.SpawnElytraNetworking;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Simpletweaks implements ModInitializer {
	public static final String MOD_ID = "simpletweaks";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static SimpletweaksConfig CONFIG;

	@Override
	public void onInitialize() {
        LOGGER.info("Starting Simpletweaks initialization...");

        AutoConfig.register(SimpletweaksConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(SimpletweaksConfig.class).getConfig();


        ModDataComponentTypes.registerDataComponentTypes();
        ModItems.registerModItems();

        ModBlocks.registerModBlocks();
        ModBlockEntities.registerBlockEntities();

        // --- EVENTS ---
        FirstJoinHandler.register();
        SpawnHandler.register();
        WorldSpawnHandler.register();

        // WICHTIG: Hier wird der Schutz aktiviert!
        ClaimProtectionHandler.register();

        // --- NETWORKING & COMMANDS ---
        SpawnElytraNetworking.register();
        ModCommands.register();
        LaserManager.register();

        OxidizableBlocksRegistry.registerOxidizableBlockPair(ModBlocks.COPPER_PRESSURE_PLATE, ModBlocks.EXPOSED_COPPER_PRESSURE_PLATE);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(ModBlocks.EXPOSED_COPPER_PRESSURE_PLATE, ModBlocks.WEATHERED_COPPER_PRESSURE_PLATE);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(ModBlocks.WEATHERED_COPPER_PRESSURE_PLATE, ModBlocks.OXIDIZED_COPPER_PRESSURE_PLATE);
    }

    public static SimpletweaksConfig getConfig() { return CONFIG; }

    public static void saveConfig() {
        AutoConfig.getConfigHolder(SimpletweaksConfig.class).save();
    }
}