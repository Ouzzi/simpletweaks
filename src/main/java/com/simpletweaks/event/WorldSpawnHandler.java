package com.simpletweaks.event;

import com.simpletweaks.Simpletweaks;
import com.simpletweaks.config.SimpletweaksConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties; // WICHTIG

public class WorldSpawnHandler {

    public static void register() {
        ServerWorldEvents.LOAD.register((server, world) -> {
            // Nur für die Oberwelt
            if (world.getRegistryKey() == World.OVERWORLD) {
                applyCustomSpawn(world);
            }
        });
    }

    private static void applyCustomSpawn(ServerWorld world) {
        SimpletweaksConfig.WorldSpawn config = Simpletweaks.getConfig().worldSpawn;

        int x = config.xCoordSpawnPoint;
        int y = config.yCoordSpawnPoint;
        int z = config.zCoordSpawnPoint;

        // Wenn y = -1, suchen wir die Oberfläche (Top Y)
        if (y == -1) {
            y = world.getTopY(Heightmap.Type.MOTION_BLOCKING, x, z);
        }

        // KORREKTUR: Zugriff auf den aktuellen Spawn-Punkt in 1.21
        // world.getSpawnPoint() gibt ein Record zurück, daraus holen wir die Position.
        BlockPos currentSpawnPos = world.getSpawnPoint().getPos();

        if (currentSpawnPos.getX() != x || currentSpawnPos.getY() != y || currentSpawnPos.getZ() != z) {

            // KORREKTUR: Setzen des Spawn-Punkts
            BlockPos newPos = new BlockPos(x, y, z);
            float angle = 0.0f;

            // FIX: Nutze die statische 'create' Methode. Sie benötigt den World RegistryKey, BlockPos, Yaw und Pitch.
            world.setSpawnPoint(WorldProperties.SpawnPoint.create(world.getRegistryKey(), newPos, angle, 0.0f));

            Simpletweaks.LOGGER.info("Set World Spawn to: " + x + ", " + y + ", " + z);
        }
    }
}