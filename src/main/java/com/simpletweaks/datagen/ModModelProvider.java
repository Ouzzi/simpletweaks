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
        // 1. Textur-Zuordnung definieren
        TextureMap teleporterTexture = new TextureMap()
                .put(TextureKey.TEXTURE, Identifier.of(Simpletweaks.MOD_ID, "block/spawn_teleporter"));

        // 2. Modell manuell hochladen (erzeugt die JSON im models/block Ordner)
        Identifier modelId = Models.PRESSURE_PLATE_UP.upload(
                ModBlocks.SPAWN_TELEPORTER,
                teleporterTexture,
                blockStateModelGenerator.modelCollector
        );

        // 3. Blockstate registrieren (verweist auf das Modell)
        blockStateModelGenerator.blockStateCollector.accept(
                BlockStateModelGenerator.createSingletonBlockState(
                        ModBlocks.SPAWN_TELEPORTER,
                        BlockStateModelGenerator.createWeightedVariant(modelId)
                )
        );

        // 4. Item-Modell registrieren (damit es im Inventar wie der Block aussieht)
        blockStateModelGenerator.registerParentedItemModel(
                ModBlocks.SPAWN_TELEPORTER,
                modelId
        );
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        // --- ITEMS (Flat 2D) ---
        // Spawn Elytra (schon vorhanden)
        itemModelGenerator.register(ModItems.SPAWN_ELYTRA, Models.GENERATED);
        itemModelGenerator.register(ModItems.BRICK_SNOWBALL, Models.GENERATED);
    }
}