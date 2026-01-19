package com.simpletweaks.item;

import com.simpletweaks.Simpletweaks;
import com.simpletweaks.block.ModBlocks;
import com.simpletweaks.event.ClaimProtectionHandler;
import com.simpletweaks.item.custom.ClaimDeedItem;
import com.simpletweaks.item.custom.LaserPointerItem;
import com.simpletweaks.item.custom.SpawnElytraItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.function.Function;

public class ModItems {
    public static final Item SPAWN_ELYTRA = registerItem("spawn_elytra", settings -> new SpawnElytraItem(settings.maxCount(1).fireproof()));
    public static final Item LASER_POINTER = registerItem("laser_pointer", settings -> new LaserPointerItem(settings.maxCount(1).maxDamage(500).rarity(Rarity.EPIC)));
    public static final Item CLAIM_DEED = registerItem("claim_deed", settings -> new ClaimDeedItem(settings.maxCount(16)));

    private static Item registerItem(String name, Function<Item.Settings, Item> function) {
        return Registry.register(Registries.ITEM, Identifier.of(Simpletweaks.MOD_ID, name),
                function.apply(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Simpletweaks.MOD_ID, name)))));
    }

    public static void registerModItems() {
        Simpletweaks.LOGGER.info("Registering Mod Items for " + Simpletweaks.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(CLAIM_DEED);
            entries.add(LASER_POINTER);
            entries.add(SPAWN_ELYTRA);
        });
        ClaimProtectionHandler.register();
    }
}