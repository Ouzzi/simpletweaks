package com.simpletweaks.entity;

import com.simpletweaks.Simpletweaks;
import com.simpletweaks.block.entity.projectile.BrickProjectileEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys; // WICHTIG: Dieser Import muss da sein
import net.minecraft.util.Identifier;

public class ModEntities {

    // 1. Key definieren
    public static final RegistryKey<EntityType<?>> BRICK_PROJECTILE_KEY = RegistryKey.of(
            RegistryKeys.ENTITY_TYPE, // FIX: RegistryKeys statt Registries
            Identifier.of(Simpletweaks.MOD_ID, "brick_projectile")
    );

    // 2. Entity registrieren
    public static final EntityType<BrickProjectileEntity> BRICK_PROJECTILE = Registry.register(
            Registries.ENTITY_TYPE,
            BRICK_PROJECTILE_KEY,
            EntityType.Builder.create(
                            (EntityType.EntityFactory<BrickProjectileEntity>) BrickProjectileEntity::new,
                            SpawnGroup.MISC
                    )
                    .dimensions(0.25f, 0.25f)
                    .maxTrackingRange(4)
                    .trackingTickInterval(10)
                    .build(BRICK_PROJECTILE_KEY)
    );

    public static void registerModEntities() {
        Simpletweaks.LOGGER.info("Registering Mod Entities for " + Simpletweaks.MOD_ID);
    }
}