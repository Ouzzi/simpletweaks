package com.simpletweaks;

import com.simpletweaks.block.ModBlocks;
import com.simpletweaks.block.entity.ModBlockEntities;
import com.simpletweaks.command.ModCommands;
import com.simpletweaks.component.ModDataComponentTypes;
import com.simpletweaks.config.SimpletweaksConfig;
import com.simpletweaks.event.FirstJoinHandler;
import com.simpletweaks.event.PlayerHeadDropHandler;
import com.simpletweaks.event.SpawnHandler;
import com.simpletweaks.event.WorldSpawnHandler;
import com.simpletweaks.item.ModItems;
import com.simpletweaks.network.SpawnElytraNetworking;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Simpletweaks implements ModInitializer {
	public static final String MOD_ID = "simpletweaks";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
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
        FirstJoinHandler.register();

        PlayerHeadDropHandler.register();
        SpawnHandler.register();
        SpawnElytraNetworking.register();
        ModCommands.register();
        WorldSpawnHandler.register();

    }

    public static SimpletweaksConfig getConfig() { return CONFIG; }
}

// TODO:
// - client-side own spawn teleporter normal, teleporter owned by others darker texture
