package com.simpletweaks.component;

import com.simpletweaks.Simpletweaks;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import com.mojang.serialization.Codec;
import java.util.function.UnaryOperator;

public class ModDataComponentTypes {

    // Verbleibende Ticks (Integer)
    public static final ComponentType<Integer> FLIGHT_TIME = register("flight_time", builder -> builder.codec(Codec.INT));

    // Verbleibende Boost-Menge (Float 0.0 - 1.0)
    public static final ComponentType<Float> BOOST_LEVEL = register("boost_level", builder -> builder.codec(Codec.FLOAT));

    // NEU: Wann war der Spieler zuletzt auf einem Pad? (Für Grace-Period Logik)
    public static final ComponentType<Long> LAST_PAD_TICK = register("last_pad_tick", builder -> builder.codec(Codec.LONG));

    // NEU: Ist diese Elytra sicher vor Schaden? (Spawn = Ja, Pad = Nein)
    public static final ComponentType<Boolean> IS_SAFE_ELYTRA = register("is_safe_elytra", builder -> builder.codec(Codec.BOOL));

    private static <T> ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(Simpletweaks.MOD_ID, name), builderOperator.apply(ComponentType.builder()).build());
    }

    public static void registerDataComponentTypes() {
        Simpletweaks.LOGGER.info("Registering Data Component Types for " + Simpletweaks.MOD_ID);
    }
}