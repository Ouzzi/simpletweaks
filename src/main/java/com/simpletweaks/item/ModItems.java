package com.simpletweaks.item;

import com.simpletweaks.Simpletweaks;
import com.simpletweaks.item.custom.SpawnElytraItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModItems {
    public static final Item SPAWN_ELYTRA = registerItem("spawn_elytra", settings -> new SpawnElytraItem(settings.maxCount(1).fireproof()));

    private static Item registerItem(String name, Function<Item.Settings, Item> function) {
        return Registry.register(Registries.ITEM, Identifier.of(Simpletweaks.MOD_ID, name),
                function.apply(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Simpletweaks.MOD_ID, name)))));
    }

    public static void registerModItems() {
        Simpletweaks.LOGGER.info("Registering Mod Items for " + Simpletweaks.MOD_ID);
    }
}