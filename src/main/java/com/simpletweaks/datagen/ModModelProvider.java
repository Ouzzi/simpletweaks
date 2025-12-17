package com.simpletweaks.datagen;

import com.simpletweaks.Simpletweaks;
import com.simpletweaks.block.ModBlocks;
import com.simpletweaks.item.ModItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.*;
import net.minecraft.util.Identifier;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        // --- SPAWN TELEPORTER BLOCK ---
        TextureMap teleporterTexture = new TextureMap().put(TextureKey.TEXTURE, Identifier.of(Simpletweaks.MOD_ID, "block/spawn_teleporter"));
        Identifier modelId = Models.PRESSURE_PLATE_UP.upload(ModBlocks.SPAWN_TELEPORTER, teleporterTexture, blockStateModelGenerator.modelCollector);
        blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(ModBlocks.SPAWN_TELEPORTER, BlockStateModelGenerator.createWeightedVariant(modelId)));
        blockStateModelGenerator.registerParentedItemModel(ModBlocks.SPAWN_TELEPORTER, modelId);

        // --- LAUNCHPAD BLOCK ---
        TextureMap launchpadTexture = new TextureMap().put(TextureKey.TEXTURE, Identifier.of(Simpletweaks.MOD_ID, "block/launchpad"));
        Identifier launchpadModelId = Models.PRESSURE_PLATE_UP.upload(ModBlocks.LAUNCHPAD, launchpadTexture, blockStateModelGenerator.modelCollector);
        blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(ModBlocks.LAUNCHPAD, BlockStateModelGenerator.createWeightedVariant(launchpadModelId)));
        blockStateModelGenerator.registerParentedItemModel(ModBlocks.LAUNCHPAD, launchpadModelId);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        // --- ITEMS (Flat 2D) ---
        // Spawn Elytra (schon vorhanden)
        itemModelGenerator.register(ModItems.SPAWN_ELYTRA, Models.GENERATED);
        itemModelGenerator.register(ModItems.BRICK_SNOWBALL, Models.GENERATED);
        itemModelGenerator.register(ModItems.LASER_POINTER, Models.GENERATED);
    }
}