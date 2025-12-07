package com.simpletweaks;

import com.simpletweaks.client.AutoWalkHandler;
import com.simpletweaks.command.ModCommands;
import com.simpletweaks.config.SimpletweaksConfig;
import com.simpletweaks.event.PlayerHeadDropHandler;
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
        LOGGER.info("Starting Simplemoney initialization...");

        AutoConfig.register(SimpletweaksConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(SimpletweaksConfig.class).getConfig();


        PlayerHeadDropHandler.register();
        ModCommands.register();
        AutoWalkHandler.register();
	}

    public static SimpletweaksConfig getConfig() { return CONFIG; }
}

// TODO: Bug -> when autowalk toggled off while moving, the player keeps moving until walk forward pressed again
// TODO:
// - kill charts befehl
// - yeet posibilliy, sneak and q to yeet item sneak and shift yeet stack

