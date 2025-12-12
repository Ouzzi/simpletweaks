package com.simpletweaks.block.entity;

import com.simpletweaks.Simpletweaks;
import com.simpletweaks.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder; // WICHTIGER IMPORT
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static BlockEntityType<SpawnTeleporterBlockEntity> SPAWN_TELEPORTER_BE;

    public static void registerBlockEntities() {
        SPAWN_TELEPORTER_BE = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(Simpletweaks.MOD_ID, "spawn_teleporter_be"),
                FabricBlockEntityTypeBuilder.create(SpawnTeleporterBlockEntity::new, ModBlocks.SPAWN_TELEPORTER).build()
        );
    }
}