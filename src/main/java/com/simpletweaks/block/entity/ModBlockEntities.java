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
            FabricBlockEntityTypeBuilder.create(SpawnTeleporterBlockEntity::new,
                    ModBlocks.SPAWN_TELEPORTER,
                    ModBlocks.SPAWN_TELEPORTER_TIER_2,
                    ModBlocks.SPAWN_TELEPORTER_TIER_3,
                    ModBlocks.SPAWN_TELEPORTER_TIER_4
            ).build()
    );

    public static final BlockEntityType<LaunchpadBlockEntity> LAUNCHPAD_BE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(Simpletweaks.MOD_ID, "launchpad_be"),
            FabricBlockEntityTypeBuilder.create(LaunchpadBlockEntity::new, ModBlocks.LAUNCHPAD).build()

    );

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

    public static final BlockEntityType<FlypadBlockEntity> FLYPAD_BE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(Simpletweaks.MOD_ID, "flypad_be"),
            FabricBlockEntityTypeBuilder.create(FlypadBlockEntity::new,
                    ModBlocks.FLYPAD,               // Tier 1
                    ModBlocks.REINFORCED_FLYPAD,    // Tier 2
                    ModBlocks.NETHERITE_FLYPAD,     // Tier 3
                    ModBlocks.STELLAR_FLYPAD        // Tier 4
            ).build()
    );

    public static final BlockEntityType<ChunkLoaderBlockEntity> CHUNK_LOADER_BE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(Simpletweaks.MOD_ID, "chunk_loader_be"),
            FabricBlockEntityTypeBuilder.create(ChunkLoaderBlockEntity::new, ModBlocks.CHUNK_LOADER).build()
    );

    public static final BlockEntityType<CopperPressurePlateBlockEntity> COPPER_PRESSURE_PLATE_BE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(Simpletweaks.MOD_ID, "copper_pressure_plate_be"),
            FabricBlockEntityTypeBuilder.create(CopperPressurePlateBlockEntity::new,
                    ModBlocks.COPPER_PRESSURE_PLATE,
                    ModBlocks.EXPOSED_COPPER_PRESSURE_PLATE,
                    ModBlocks.WEATHERED_COPPER_PRESSURE_PLATE,
                    ModBlocks.OXIDIZED_COPPER_PRESSURE_PLATE
                    // Hier auch die gewachsten Varianten einfügen, wenn du sie erstellst
            ).build()
    );

    public static final BlockEntityType<NetheritePressurePlateBlockEntity> NETHERITE_PRESSURE_PLATE_BE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(Simpletweaks.MOD_ID, "netherite_pressure_plate_be"),
            FabricBlockEntityTypeBuilder.create(NetheritePressurePlateBlockEntity::new, ModBlocks.NETHERITE_PRESSURE_PLATE).build()
    );

    public static void registerBlockEntities() {
        Simpletweaks.LOGGER.info("Registering Block Entities for " + Simpletweaks.MOD_ID);
    }
}