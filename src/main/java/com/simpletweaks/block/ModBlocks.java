package com.simpletweaks.block;

import com.simpletweaks.Simpletweaks;
import com.simpletweaks.block.custom.*;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
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

    private static RegistryKey<Block> keyOf(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Simpletweaks.MOD_ID, name));
    }

    // Settings für "Unzerstörbare Pads" (Piston Block, kein X-Ray)
    private static AbstractBlock.Settings sturdyPadSettings() {
        return AbstractBlock.Settings.create()
                .nonOpaque()
                .solidBlock(Blocks::never)
                .suffocates(Blocks::never)
                .blockVision(Blocks::never)
                .pistonBehavior(PistonBehavior.BLOCK); // WICHTIG: Piston kann es nicht bewegen/zerstören
    }

    // Settings für Kupfer (Zerstörbar)
    private static AbstractBlock.Settings fragilePadSettings() {
        return AbstractBlock.Settings.create()
                .nonOpaque()
                .solidBlock(Blocks::never)
                .suffocates(Blocks::never)
                .blockVision(Blocks::never)
                .pistonBehavior(PistonBehavior.DESTROY); // Standard Verhalten
    }

    // --- BLÖCKE ---

    // Spawn Teleporter (Sturdy)
    public static final Block SPAWN_TELEPORTER = registerBlock("spawn_teleporter",
            new SpawnTeleporterBlock(sturdyPadSettings().registryKey(keyOf("spawn_teleporter")).luminance(state -> 10), 1));
    public static final Block SPAWN_TELEPORTER_TIER_2 = registerBlock("spawn_teleporter_tier_2",
            new SpawnTeleporterBlock(sturdyPadSettings().registryKey(keyOf("spawn_teleporter_tier_2")).luminance(state -> 12).mapColor(MapColor.DIAMOND_BLUE), 2));
    public static final Block SPAWN_TELEPORTER_TIER_3 = registerBlock("spawn_teleporter_tier_3",
            new SpawnTeleporterBlock(sturdyPadSettings().registryKey(keyOf("spawn_teleporter_tier_3")).luminance(state -> 14).mapColor(MapColor.EMERALD_GREEN), 3));
    public static final Block SPAWN_TELEPORTER_TIER_4 = registerBlock("spawn_teleporter_tier_4",
            new SpawnTeleporterBlock(sturdyPadSettings().registryKey(keyOf("spawn_teleporter_tier_4")).luminance(state -> 15).mapColor(MapColor.GOLD), 4));

    // Launchpad (Sturdy)
    public static final Block LAUNCHPAD = registerBlock("launchpad",
            new LaunchpadBlock(sturdyPadSettings().registryKey(keyOf("launchpad")).luminance(state -> 5)));

    // Diamond Pressure Plate (Sturdy)
    public static final Block DIAMOND_PRESSURE_PLATE = registerBlock("diamond_pressure_plate",
            new DiamondPressurePlateBlock(BlockSetType.IRON,
                    AbstractBlock.Settings.create() // Muss neu erstellt werden, da Basisklasse PressurePlate ist
                            .registryKey(keyOf("diamond_pressure_plate"))
                            .mapColor(MapColor.DIAMOND_BLUE)
                            .noCollision()
                            .strength(0.5F)
                            .pistonBehavior(PistonBehavior.BLOCK) // Fix: Nicht zerstörbar durch Piston
            ));

    // Netherite Pressure Plate (Sturdy)
    public static final Block NETHERITE_PRESSURE_PLATE = registerBlock("netherite_pressure_plate",
            new NetheritePressurePlateBlock(BlockSetType.IRON,
                    AbstractBlock.Settings.create()
                            .registryKey(keyOf("netherite_pressure_plate"))
                            .mapColor(MapColor.BLACK)
                            .noCollision()
                            .strength(4.0F)
                            .pistonBehavior(PistonBehavior.BLOCK) // Fix
            ));

    // Elytra Pads (Sturdy)
    public static final Block ELYTRA_PAD = registerBlock("elytra_pad",
            new ElytraPadBlock(sturdyPadSettings().registryKey(keyOf("elytra_pad")).mapColor(MapColor.CYAN).strength(1.5f).sounds(BlockSoundGroup.METAL), 1));
    public static final Block REINFORCED_ELYTRA_PAD = registerBlock("reinforced_elytra_pad",
            new ElytraPadBlock(sturdyPadSettings().registryKey(keyOf("reinforced_elytra_pad")).mapColor(MapColor.DIAMOND_BLUE).strength(2.0f).sounds(BlockSoundGroup.METAL), 2));
    public static final Block NETHERITE_ELYTRA_PAD = registerBlock("netherite_elytra_pad",
            new ElytraPadBlock(sturdyPadSettings().registryKey(keyOf("netherite_elytra_pad")).mapColor(MapColor.BLACK).strength(4.0f).sounds(BlockSoundGroup.NETHERITE), 3));
    public static final Block FINE_ELYTRA_PAD = registerBlock("fine_elytra_pad",
            new ElytraPadBlock(sturdyPadSettings().registryKey(keyOf("fine_elytra_pad")).mapColor(MapColor.GOLD).strength(4.0f).sounds(BlockSoundGroup.NETHERITE).luminance(state -> 10), 4));

    // Flypads (Sturdy)
    public static final Block FLYPAD = registerBlock("flypad",
            new FlypadBlock(sturdyPadSettings().registryKey(keyOf("flypad")).mapColor(MapColor.EMERALD_GREEN).strength(2.0f), 1));
    public static final Block REINFORCED_FLYPAD = registerBlock("reinforced_flypad",
            new FlypadBlock(sturdyPadSettings().registryKey(keyOf("reinforced_flypad")).mapColor(MapColor.DIAMOND_BLUE).strength(3.0f), 2));
    public static final Block NETHERITE_FLYPAD = registerBlock("netherite_flypad",
            new FlypadBlock(sturdyPadSettings().registryKey(keyOf("netherite_flypad")).mapColor(MapColor.BLACK).strength(5.0f), 3));
    public static final Block STELLAR_FLYPAD = registerBlock("stellar_flypad",
            new FlypadBlock(sturdyPadSettings().registryKey(keyOf("stellar_flypad")).mapColor(MapColor.PURPLE).strength(5.0f).luminance(s -> 15), 4));

    // Chunk Loader (Sturdy)
    public static final Block CHUNK_LOADER = registerBlock("chunk_loader",
            new ChunkLoaderBlock(sturdyPadSettings().registryKey(keyOf("chunk_loader")).mapColor(MapColor.DIAMOND_BLUE).strength(4.0f).luminance(state -> 7)));

    // Copper Plates (Fragile / Destroyable)
    public static final Block COPPER_PRESSURE_PLATE = registerBlock("copper_pressure_plate",
            new CopperPressurePlateBlock(Oxidizable.OxidationLevel.UNAFFECTED, fragilePadSettings().registryKey(keyOf("copper_pressure_plate")).mapColor(MapColor.ORANGE).strength(1.5f).noCollision()));
    public static final Block EXPOSED_COPPER_PRESSURE_PLATE = registerBlock("exposed_copper_pressure_plate",
            new CopperPressurePlateBlock(Oxidizable.OxidationLevel.EXPOSED, fragilePadSettings().registryKey(keyOf("exposed_copper_pressure_plate")).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).strength(1.5f).noCollision()));
    public static final Block WEATHERED_COPPER_PRESSURE_PLATE = registerBlock("weathered_copper_pressure_plate",
            new CopperPressurePlateBlock(Oxidizable.OxidationLevel.WEATHERED, fragilePadSettings().registryKey(keyOf("weathered_copper_pressure_plate")).mapColor(MapColor.TEAL).strength(1.5f).noCollision()));
    public static final Block OXIDIZED_COPPER_PRESSURE_PLATE = registerBlock("oxidized_copper_pressure_plate",
            new CopperPressurePlateBlock(Oxidizable.OxidationLevel.OXIDIZED, fragilePadSettings().registryKey(keyOf("oxidized_copper_pressure_plate")).mapColor(MapColor.EMERALD_GREEN).strength(1.5f).noCollision()));


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
            entries.add(SPAWN_TELEPORTER); entries.add(SPAWN_TELEPORTER_TIER_2); entries.add(SPAWN_TELEPORTER_TIER_3); entries.add(SPAWN_TELEPORTER_TIER_4);
            entries.add(LAUNCHPAD);
            entries.add(DIAMOND_PRESSURE_PLATE);
            entries.add(ELYTRA_PAD); entries.add(REINFORCED_ELYTRA_PAD); entries.add(NETHERITE_ELYTRA_PAD); entries.add(FINE_ELYTRA_PAD);
            entries.add(NETHERITE_PRESSURE_PLATE);
            entries.add(FLYPAD); entries.add(REINFORCED_FLYPAD); entries.add(NETHERITE_FLYPAD); entries.add(STELLAR_FLYPAD);
            entries.add(COPPER_PRESSURE_PLATE); entries.add(EXPOSED_COPPER_PRESSURE_PLATE); entries.add(WEATHERED_COPPER_PRESSURE_PLATE); entries.add(OXIDIZED_COPPER_PRESSURE_PLATE);
            entries.add(CHUNK_LOADER);
        });
    }
}