package com.simpletweaks.datagen;

import com.simpletweaks.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.recipe.SmithingTransformRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected @NotNull RecipeGenerator getRecipeGenerator(RegistryWrapper.@NotNull WrapperLookup wrapperLookup, @NotNull RecipeExporter recipeExporter) {
        return new RecipeGenerator(wrapperLookup, recipeExporter) {
            @Override
            public void generate() {
                Ingredient templates = Ingredient.ofItems(
                        Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                        Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE,
                        Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE,
                        Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE,
                        Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE,
                        Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE,
                        Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE,
                        Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE,
                        Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE,
                        Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE,
                        Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE,
                        Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE,
                        Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE,
                        Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE,
                        Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE,
                        Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE,
                        Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE,
                        Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE,
                        Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE
                );

                // 1. Smithing: Spawn Teleporter
                SmithingTransformRecipeJsonBuilder.create( // later weaker pad
                                templates,
                                Ingredient.ofItems(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE),
                                Ingredient.ofItems(Blocks.DIAMOND_BLOCK),
                                RecipeCategory.TOOLS,
                                ModBlocks.SPAWN_TELEPORTER.asItem()
                        )
                        .criterion(hasItem(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE), conditionsFromItem(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE))
                        .offerTo(exporter, getRecipeName(ModBlocks.SPAWN_TELEPORTER) + "_smithing");
                SmithingTransformRecipeJsonBuilder.create( // later stronger pad
                                Ingredient.ofItems(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                                Ingredient.ofItems(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE),
                                Ingredient.ofItems(Items.NETHERITE_INGOT),
                                RecipeCategory.TOOLS,
                                ModBlocks.SPAWN_TELEPORTER.asItem()
                        )
                        .criterion(hasItem(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE), conditionsFromItem(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE))
                        .offerTo(exporter, getRecipeName(ModBlocks.SPAWN_TELEPORTER) + "_smithing_alternative");

                // 2. Smithing: Launchpad
                SmithingTransformRecipeJsonBuilder.create( // later weaker pad
                                templates,
                                Ingredient.ofItems(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE),
                                Ingredient.ofItems(Blocks.DIAMOND_BLOCK),
                                RecipeCategory.TOOLS,
                                ModBlocks.LAUNCHPAD.asItem()
                        )
                        .criterion(hasItem(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE), conditionsFromItem(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE))
                        .offerTo(exporter, getRecipeName(ModBlocks.LAUNCHPAD) + "_smithing");
                SmithingTransformRecipeJsonBuilder.create( // later stronger pad
                                Ingredient.ofItems(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                                Ingredient.ofItems(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE),
                                Ingredient.ofItems(Items.NETHERITE_INGOT),
                                RecipeCategory.TOOLS,
                                ModBlocks.LAUNCHPAD.asItem()
                        )
                        .criterion(hasItem(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE), conditionsFromItem(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE))
                        .offerTo(exporter, getRecipeName(ModBlocks.LAUNCHPAD) + "_smithing_alternative");

                // 3. Crafting: Pressure Plates
                createShaped(RecipeCategory.REDSTONE, ModBlocks.DIAMOND_PRESSURE_PLATE)
                        .pattern("DD")
                        .input('D', Items.DIAMOND)
                        .criterion(hasItem(Items.DIAMOND), conditionsFromItem(Items.DIAMOND))
                        .offerTo(exporter);

                createShaped(RecipeCategory.REDSTONE, ModBlocks.NETHERITE_PRESSURE_PLATE)
                        .pattern("NN")
                        .input('N', Items.NETHERITE_INGOT)
                        .criterion(hasItem(Items.NETHERITE_INGOT), conditionsFromItem(Items.NETHERITE_INGOT))
                        .offerTo(exporter);

                // 4. Smithing: Elytra Pad (Tiered)
                SmithingTransformRecipeJsonBuilder.create(
                            templates,
                            Ingredient.ofItems(ModBlocks.DIAMOND_PRESSURE_PLATE),
                            Ingredient.ofItems(Items.DIAMOND),
                            RecipeCategory.TOOLS,
                            ModBlocks.ELYTRA_PAD.asItem())
                        .criterion("has_diamond_pressure_plate", conditionsFromItem(ModBlocks.DIAMOND_PRESSURE_PLATE))
                        .offerTo(exporter, "elytra_pad_smithing");
                SmithingTransformRecipeJsonBuilder.create(
                            templates,
                            Ingredient.ofItems(ModBlocks.ELYTRA_PAD),
                            Ingredient.ofItems(Blocks.DIAMOND_BLOCK),
                            RecipeCategory.TOOLS,
                            ModBlocks.REINFORCED_ELYTRA_PAD.asItem())
                        .criterion("has_elytra_pad", conditionsFromItem(ModBlocks.ELYTRA_PAD))
                        .offerTo(exporter, "reinforced_elytra_pad_smithing");
                SmithingTransformRecipeJsonBuilder.create(
                            Ingredient.ofItems(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                            Ingredient.ofItems(ModBlocks.REINFORCED_ELYTRA_PAD),
                            Ingredient.ofItems(Items.NETHERITE_INGOT),
                            RecipeCategory.TOOLS,
                            ModBlocks.NETHERITE_ELYTRA_PAD.asItem())
                        .criterion("has_reinforced_elytra_pad", conditionsFromItem(ModBlocks.REINFORCED_ELYTRA_PAD))
                        .offerTo(exporter, "netherite_elytra_pad_smithing");
                SmithingTransformRecipeJsonBuilder.create(
                            Ingredient.ofItems(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                            Ingredient.ofItems(ModBlocks.NETHERITE_ELYTRA_PAD),
                            Ingredient.ofItems(Items.NETHER_STAR),
                            RecipeCategory.TOOLS,
                            ModBlocks.FINE_ELYTRA_PAD.asItem())
                        .criterion("has_netherite_elytra_pad", conditionsFromItem(ModBlocks.NETHERITE_ELYTRA_PAD))
                        .offerTo(exporter, "fine_elytra_pad_smithing");

                // Smithing/Crafting: Flypads (Tiered)
                SmithingTransformRecipeJsonBuilder.create(
                                Ingredient.ofItems(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                                Ingredient.ofItems(ModBlocks.FINE_ELYTRA_PAD), // Base: Tier 4 Elytra Pad
                                Ingredient.ofItems(Items.NETHERITE_INGOT),     // Addition
                                RecipeCategory.TOOLS,
                                ModBlocks.FLYPAD.asItem())
                        .criterion("has_fine_elytra_pad", conditionsFromItem(ModBlocks.FINE_ELYTRA_PAD))
                        .offerTo(exporter, "flypad_tier1_smithing");
                SmithingTransformRecipeJsonBuilder.create(
                                Ingredient.ofItems(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                                Ingredient.ofItems(ModBlocks.FLYPAD),
                                Ingredient.ofItems(Blocks.NETHERITE_BLOCK),
                                RecipeCategory.TOOLS,
                                ModBlocks.REINFORCED_FLYPAD.asItem())
                        .criterion("has_flypad", conditionsFromItem(ModBlocks.FLYPAD))
                        .offerTo(exporter, "flypad_tier2_smithing");
                createShaped(RecipeCategory.TOOLS, ModBlocks.NETHERITE_FLYPAD)
                        .pattern("DBD")
                        .pattern("ESE")
                        .pattern("KFK")
                        .input('D', Blocks.DIAMOND_BLOCK)
                        .input('S', Items.NETHER_STAR)
                        .input('K', Items.OMINOUS_TRIAL_KEY)
                        .input('F', ModBlocks.REINFORCED_FLYPAD)
                        .input('B', Blocks.NETHERITE_BLOCK)
                        .criterion("has_reinforced_flypad", conditionsFromItem(ModBlocks.REINFORCED_FLYPAD))
                        .offerTo(exporter);
                createShaped(RecipeCategory.TOOLS, ModBlocks.STELLAR_FLYPAD)
                        .pattern("KKK")
                        .pattern("ESE")
                        .pattern("FFF")
                        .input('K', Items.OMINOUS_TRIAL_KEY)
                        .input('F', ModBlocks.NETHERITE_FLYPAD)
                        .input('E', Items.ENCHANTED_GOLDEN_APPLE)
                        .input('S', Items.NETHER_STAR)
                        .criterion("has_netherite_flypad", conditionsFromItem(ModBlocks.NETHERITE_FLYPAD))
                        .offerTo(exporter);

            }
        };
    }

    @Override
    public String getName() {
        return "SimpleTweaks Recipes";
    }
}