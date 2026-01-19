package com.simpletweaks.block.entity;

import com.simpletweaks.Simpletweaks;
import com.simpletweaks.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder; // WICHTIGER IMPORT
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    // ... andere BEs (SpawnTeleporter etc.) ...
    public static final BlockEntityType<SpawnTeleporterBlockEntity> SPAWN_TELEPORTER_BE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(Simpletweaks.MOD_ID, "spawn_teleporter_be"),
            FabricBlockEntityTypeBuilder.create(SpawnTeleporterBlockEntity::new, ModBlocks.SPAWN_TELEPORTER).build()
    );

    public static final BlockEntityType<LaunchpadBlockEntity> LAUNCHPAD_BE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(Simpletweaks.MOD_ID, "launchpad_be"),
            FabricBlockEntityTypeBuilder.create(LaunchpadBlockEntity::new, ModBlocks.LAUNCHPAD).build()

    );

    // --- FIX HIER: Alle 4 Blöcke eintragen ---
    public static final BlockEntityType<ElytraPadBlockEntity> ELYTRA_PAD_BE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(Simpletweaks.MOD_ID, "elytra_pad_be"),
            FabricBlockEntityTypeBuilder.create(ElytraPadBlockEntity::new,
                    ModBlocks.ELYTRA_PAD,            // Tier 1
                    ModBlocks.REINFORCED_ELYTRA_PAD, // Tier 2
                    ModBlocks.NETHERITE_ELYTRA_PAD,  // Tier 3
                    ModBlocks.FINE_ELYTRA_PAD        // Tier 4
            ).build()
    );

    public static void registerBlockEntities() {
        Simpletweaks.LOGGER.info("Registering Block Entities for " + Simpletweaks.MOD_ID);
    }
}