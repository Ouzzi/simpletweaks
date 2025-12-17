package com.simpletweaks.event;

import com.simpletweaks.Simpletweaks;
import com.simpletweaks.config.SimpletweaksConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.chunk.ChunkStatus;

public class WorldSpawnHandler {

    public static void register() {
        ServerWorldEvents.LOAD.register((server, world) -> {
            if (world.getRegistryKey() == World.OVERWORLD) {
                applyCustomSpawn(world);
            }
        });
    }

    private static void applyCustomSpawn(ServerWorld world) {
        SimpletweaksConfig.Spawn config = Simpletweaks.getConfig().spawn;

        int x = config.xCoordSpawnPoint;
        int y = config.yCoordSpawnPoint;
        int z = config.zCoordSpawnPoint;

        // Automatische Y-Höhe berechnen
        if (y == -1) {
            int chunkX = ChunkSectionPos.getSectionCoord(x);
            int chunkZ = ChunkSectionPos.getSectionCoord(z);

            // Chunk laden erzwingen, um die Höhe sicher zu bestimmen
            world.getChunk(chunkX, chunkZ, ChunkStatus.FULL);

            y = world.getTopY(Heightmap.Type.MOTION_BLOCKING, x, z);
            if (y <= world.getBottomY()) {
                y = 70;
                Simpletweaks.LOGGER.warn("Konnte keine sichere Spawn-Höhe finden. Setze auf 70.");
            }
        }

        BlockPos newPos = new BlockPos(x, y, z);

        // Aktuellen Spawn abrufen (in 1.21.3 über getSpawnPoint())
        BlockPos currentPos = world.getSpawnPoint().getPos();

        // Nur setzen, wenn es sich geändert hat
        if (!currentPos.equals(newPos)) {
            // FIX: WorldProperties.SpawnPoint nutzen
            // Parameter: RegistryKey (Dimension), BlockPos, Yaw, Pitch
            // Wir erstellen ein SpawnPoint-Objekt für die aktuelle Dimension
            WorldProperties.SpawnPoint spawnPoint = WorldProperties.SpawnPoint.create(world.getRegistryKey(), newPos, 0.0f, 0.0f);

            world.setSpawnPoint(spawnPoint);
            Simpletweaks.LOGGER.info("Simpletweaks: Set Custom World Spawn to " + newPos.toShortString());
        }
    }
}