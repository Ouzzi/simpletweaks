package com.simpletweaks.component;

import com.simpletweaks.Simpletweaks;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import java.util.function.UnaryOperator;

public class ModDataComponentTypes {

    // Speichert verbleibende Ticks (Integer)
    public static final ComponentType<Integer> FLIGHT_TIME = register("flight_time", builder -> builder.codec(com.mojang.serialization.Codec.INT));

    // Speichert verbleibende Boost-Menge (Float 0.0 - 1.0 für die Bar)
    public static final ComponentType<Float> BOOST_LEVEL = register("boost_level", builder -> builder.codec(com.mojang.serialization.Codec.FLOAT));

    private static <T> ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(Simpletweaks.MOD_ID, name), builderOperator.apply(ComponentType.builder()).build());
    }

    public static void registerDataComponentTypes() {
        Simpletweaks.LOGGER.info("Registering Data Component Types for " + Simpletweaks.MOD_ID);
    }
}