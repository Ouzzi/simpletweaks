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
import com.simpletweaks.network.LaserManager;
import com.simpletweaks.network.SpawnElytraNetworking;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

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
        FirstJoinHandler.register();

        PlayerHeadDropHandler.register();
        SpawnHandler.register();
        SpawnElytraNetworking.register();
        ModCommands.register();
        WorldSpawnHandler.register();
        LaserManager.register();

    }

    public static SimpletweaksConfig getConfig() { return CONFIG; }
}

/// TODO:
// - aufteilen in:
// -- simplevisuals
// -- simplequalityoflife
// -- simpleservertweaks
// -- simpleclienttweaks
// -- simplefun


// - more compostable items
// - more waxing options - waxed sand and gravel to prevent falling, waxed ice to prevent melting, waxed concrete powder to prevent turning to concrete, waxesd coral blocks to prevent drying out
// - dispenser place more blocks
// - (later) realworld Transformation:
// -- two shulker shells on chest
// -- moss on cobble
// -- ...

// - Better Item Frames (Later)
// - Chat Calc (Later)

// - modmenu pages
// - toggle all on some groups


// - challenges/progressing system:
// -- you get a book at first join with Introduction and list of challenges like achievements.
// -- when completing challenges you get rewards like items, blocks, xp, money, perks (like faster mining, higher jumps, extra inv, ...)
// -- rewards could be given via commands to be compatible with other mods
// -- rewards must be collected manually via a gui (like advancements)
// -- only big challenges give rewards, small ones are just for fun
// -- fully customizable via config file - enable/disable challenges, custom challenges, custom rewards, ...

// - 4 extra inv for challenges
// - - example eat a dark heart -> wildcard slot
// - - example eat a enchanted golden apple -> food slot
// - - example break 10000 ores -> tool slot
// - - example kill 10000 mobs -> weapon slot


// BUGS/todo:
// - sometimes pickup notifier doesnt show picked up item (sometimes multiple items dont show)


