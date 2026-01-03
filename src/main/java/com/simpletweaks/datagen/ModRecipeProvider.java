package com.simpletweaks.datagen;

import com.simpletweaks.Simpletweaks;
import com.simpletweaks.block.ModBlocks;
import com.simpletweaks.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.block.Blocks;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.recipe.SmithingTransformRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
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

                // Smithing Rezept: Launchpad
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