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
        registerFlatBlock(blockStateModelGenerator, ModBlocks.SPAWN_TELEPORTER, "spawn_teleporter");

        // --- LAUNCHPAD BLOCK ---
        registerFlatBlock(blockStateModelGenerator, ModBlocks.LAUNCHPAD, "launchpad");

        // --- DIAMOND PRESSURE PLATE ---
        registerFlatBlock(blockStateModelGenerator, ModBlocks.DIAMOND_PRESSURE_PLATE, "diamond_pressure_plate");

        // --- NETHERITE PRESSURE PLATE ---
        registerFlatBlock(blockStateModelGenerator, ModBlocks.NETHERITE_PRESSURE_PLATE, "netherite_pressure_plate");

        // --- ELYTRA PADS (Tier 1-4) ---
        registerFlatBlock(blockStateModelGenerator, ModBlocks.ELYTRA_PAD, "elytra_pad");
        registerFlatBlock(blockStateModelGenerator, ModBlocks.REINFORCED_ELYTRA_PAD, "reinforced_elytra_pad");
        registerFlatBlock(blockStateModelGenerator, ModBlocks.NETHERITE_ELYTRA_PAD, "netherite_elytra_pad");
        registerFlatBlock(blockStateModelGenerator, ModBlocks.FINE_ELYTRA_PAD, "fine_elytra_pad");

        // --- FLYPADS (Tier 1-4) ---
        registerFlatBlock(blockStateModelGenerator, ModBlocks.FLYPAD, "flypad");
        registerFlatBlock(blockStateModelGenerator, ModBlocks.REINFORCED_FLYPAD, "reinforced_flypad");
        registerFlatBlock(blockStateModelGenerator, ModBlocks.NETHERITE_FLYPAD, "netherite_flypad");
        registerFlatBlock(blockStateModelGenerator, ModBlocks.STELLAR_FLYPAD, "stellar_flypad");
    }

    /**
     * Helfer-Methode für flache Blöcke (wie Druckplatten),
     * registriert BlockState und ItemModel.
     */
    private void registerFlatBlock(BlockStateModelGenerator generator, net.minecraft.block.Block block, String textureName) {
        TextureMap texture = new TextureMap().put(TextureKey.TEXTURE, Identifier.of(Simpletweaks.MOD_ID, "block/" + textureName));
        Identifier modelId = Models.PRESSURE_PLATE_UP.upload(block, texture, generator.modelCollector);

        generator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, BlockStateModelGenerator.createWeightedVariant(modelId)));
        generator.registerParentedItemModel(block, modelId);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        // --- ITEMS (Flat 2D) ---
        itemModelGenerator.register(ModItems.SPAWN_ELYTRA, Models.GENERATED);
        itemModelGenerator.register(ModItems.LASER_POINTER, Models.GENERATED);
        itemModelGenerator.register(ModItems.CLAIM_DEED, Models.GENERATED);
    }
}