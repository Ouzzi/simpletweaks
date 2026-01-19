package com.simpletweaks.datagen;

import com.simpletweaks.block.ModBlocks;
import com.simpletweaks.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {

        valueLookupBuilder(ItemTags.TRIMMABLE_ARMOR)
                .add(ModBlocks.DIAMOND_PRESSURE_PLATE.asItem())
                .add(ModBlocks.ELYTRA_PAD.asItem())
                .add(ModBlocks.REINFORCED_ELYTRA_PAD.asItem())
                .add(ModBlocks.NETHERITE_ELYTRA_PAD.asItem())
                .add(ModBlocks.FINE_ELYTRA_PAD.asItem());

        valueLookupBuilder(ItemTags.TRIM_MATERIALS)
                .add(Items.DIAMOND_BLOCK.asItem())
                .add(Items.NETHER_STAR.asItem());


    }
}
