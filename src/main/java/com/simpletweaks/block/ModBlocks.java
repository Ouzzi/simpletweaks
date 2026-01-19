package com.simpletweaks.block;

import com.simpletweaks.Simpletweaks;
import com.simpletweaks.block.custom.DiamondPressurePlateBlock;
import com.simpletweaks.block.custom.ElytraPadBlock;
import com.simpletweaks.block.custom.LaunchpadBlock;
import com.simpletweaks.block.custom.SpawnTeleporterBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {

    // Helper Methode, um Keys sauber zu erzeugen (vermeidet statische Init-Probleme)
    private static RegistryKey<Block> keyOf(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Simpletweaks.MOD_ID, name));
    }

    // Block Definition
    public static final Block SPAWN_TELEPORTER = registerBlock("spawn_teleporter",
            new SpawnTeleporterBlock(AbstractBlock.Settings.create()
                    .registryKey(keyOf("spawn_teleporter"))
                    .luminance(state -> 10)
                    .nonOpaque()
            ));

    public static final Block LAUNCHPAD = registerBlock("launchpad",
            new LaunchpadBlock(AbstractBlock.Settings.create()
                    .registryKey(keyOf("launchpad"))
                    .luminance(state -> 5)
                    .nonOpaque()
            ));

    public static final Block DIAMOND_PRESSURE_PLATE = registerBlock("diamond_pressure_plate",
            new DiamondPressurePlateBlock(BlockSetType.IRON,
                    AbstractBlock.Settings.create()
                            .registryKey(keyOf("diamond_pressure_plate")) // WICHTIG: Key setzen!
                            .mapColor(MapColor.DIAMOND_BLUE)
                            .noCollision()
                            .strength(0.5F)
                            .pistonBehavior(PistonBehavior.DESTROY)
            ));

    // TIER 1
    public static final Block ELYTRA_PAD = registerBlock("elytra_pad",
            new ElytraPadBlock(AbstractBlock.Settings.create()
                    .registryKey(keyOf("elytra_pad")) // WICHTIG: Key setzen!
                    .mapColor(MapColor.CYAN)
                    .strength(1.5f)
                    .sounds(BlockSoundGroup.METAL)
                    .nonOpaque(),
                    1 // Tier
            ));

    // TIER 2
    public static final Block REINFORCED_ELYTRA_PAD = registerBlock("reinforced_elytra_pad",
            new ElytraPadBlock(AbstractBlock.Settings.create()
                    .registryKey(keyOf("reinforced_elytra_pad")) // WICHTIG: Key setzen!
                    .mapColor(MapColor.DIAMOND_BLUE)
                    .strength(2.0f)
                    .sounds(BlockSoundGroup.METAL)
                    .nonOpaque(),
                    2 // Tier
            ));

    // TIER 3
    public static final Block NETHERITE_ELYTRA_PAD = registerBlock("netherite_elytra_pad",
            new ElytraPadBlock(AbstractBlock.Settings.create()
                    .registryKey(keyOf("netherite_elytra_pad")) // WICHTIG: Key setzen!
                    .mapColor(MapColor.BLACK)
                    .strength(4.0f)
                    .sounds(BlockSoundGroup.NETHERITE)
                    .nonOpaque(),
                    3 // Tier
            ));

    // TIER 4
    public static final Block FINE_ELYTRA_PAD = registerBlock("fine_elytra_pad",
            new ElytraPadBlock(AbstractBlock.Settings.create()
                    .registryKey(keyOf("fine_elytra_pad")) // WICHTIG: Key setzen!
                    .mapColor(MapColor.GOLD)
                    .strength(4.0f)
                    .sounds(BlockSoundGroup.NETHERITE)
                    .luminance(state -> 10)
                    .nonOpaque(),
                    4 // Tier
            ));


    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(Simpletweaks.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Simpletweaks.MOD_ID, name));

        Registry.register(Registries.ITEM, Identifier.of(Simpletweaks.MOD_ID, name),
                new BlockItem(block, new Item.Settings().registryKey(itemKey)));
    }

    public static void registerModBlocks() {
        Simpletweaks.LOGGER.info("Registering Mod Blocks for " + Simpletweaks.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(SPAWN_TELEPORTER);
            entries.add(LAUNCHPAD);
            entries.add(DIAMOND_PRESSURE_PLATE);
            entries.add(ELYTRA_PAD);
            entries.add(REINFORCED_ELYTRA_PAD);
            entries.add(NETHERITE_ELYTRA_PAD);
            entries.add(FINE_ELYTRA_PAD);
        });
    }
}