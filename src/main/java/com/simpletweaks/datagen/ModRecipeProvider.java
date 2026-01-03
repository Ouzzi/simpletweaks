package com.simpletweaks.datagen;

import com.simpletweaks.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.recipe.SmithingTransformRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected @NotNull RecipeGenerator getRecipeGenerator(RegistryWrapper.@NotNull WrapperLookup wrapperLookup, @NotNull RecipeExporter recipeExporter) {
        return new RecipeGenerator(wrapperLookup, recipeExporter) {
            @Override
            public void generate() {
                // Smithing Rezept: Netherite Upgrade + Gold Pressure Plate = Spawn Teleporter
                SmithingTransformRecipeJsonBuilder.create(
                                Ingredient.ofItems(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                                Ingredient.ofItems(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE),
                                Ingredient.ofItems(Items.NETHERITE_INGOT),
                                RecipeCategory.TOOLS,
                                ModBlocks.SPAWN_TELEPORTER.asItem()
                        )
                        .criterion(hasItem(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE), conditionsFromItem(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE))
                        .offerTo(exporter, getRecipeName(ModBlocks.SPAWN_TELEPORTER) + "_smithing");

                // Smithing Rezept: Netherite Upgrade + Heavy Pressure Plate = Launchpad
                SmithingTransformRecipeJsonBuilder.create(
                                Ingredient.ofItems(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                                Ingredient.ofItems(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE),
                                Ingredient.ofItems(Items.NETHERITE_INGOT),
                                RecipeCategory.TOOLS,
                                ModBlocks.LAUNCHPAD.asItem()
                        )
                        .criterion(hasItem(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE), conditionsFromItem(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE))
                        .offerTo(exporter, getRecipeName(ModBlocks.LAUNCHPAD) + "_smithing");
            }
        };
    }

    @Override
    public String getName() {
        return "SimpleTweaks Recipes";
    }
}