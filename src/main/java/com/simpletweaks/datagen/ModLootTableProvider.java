package com.simpletweaks.datagen;

import com.simpletweaks.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        // Spawn Teleporter
        addDrop(ModBlocks.SPAWN_TELEPORTER);
        addDrop(ModBlocks.SPAWN_TELEPORTER_TIER_2);
        addDrop(ModBlocks.SPAWN_TELEPORTER_TIER_3);
        addDrop(ModBlocks.SPAWN_TELEPORTER_TIER_4);

        // Launchpad
        addDrop(ModBlocks.LAUNCHPAD);

        // Pressure Plates
        addDrop(ModBlocks.DIAMOND_PRESSURE_PLATE);
        addDrop(ModBlocks.NETHERITE_PRESSURE_PLATE);

        // Elytra Pads
        addDrop(ModBlocks.ELYTRA_PAD);
        addDrop(ModBlocks.REINFORCED_ELYTRA_PAD);
        addDrop(ModBlocks.NETHERITE_ELYTRA_PAD);
        addDrop(ModBlocks.FINE_ELYTRA_PAD);

        // Flypads
        addDrop(ModBlocks.FLYPAD);
        addDrop(ModBlocks.REINFORCED_FLYPAD);
        addDrop(ModBlocks.NETHERITE_FLYPAD);
        addDrop(ModBlocks.STELLAR_FLYPAD);

        // Copper Plates
        addDrop(ModBlocks.COPPER_PRESSURE_PLATE);
        addDrop(ModBlocks.EXPOSED_COPPER_PRESSURE_PLATE);
        addDrop(ModBlocks.WEATHERED_COPPER_PRESSURE_PLATE);
        addDrop(ModBlocks.OXIDIZED_COPPER_PRESSURE_PLATE);

        // Chunk Loader
        addDrop(ModBlocks.CHUNK_LOADER);
    }
}