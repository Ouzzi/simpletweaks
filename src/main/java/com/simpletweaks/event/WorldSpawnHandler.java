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
            // FIX: Berechne Chunk-Koordinaten
            int chunkX = ChunkSectionPos.getSectionCoord(x);
            int chunkZ = ChunkSectionPos.getSectionCoord(z);

            // FIX: Erzwinge das Laden des Chunks, damit getTopY korrekte Werte liefert
            // Sonst gibt es oft den Tiefstwert (z.B. -64), wenn der Chunk noch nicht generiert ist.
            world.getChunk(chunkX, chunkZ, ChunkStatus.FULL);

            y = world.getTopY(Heightmap.Type.MOTION_BLOCKING, x, z);

            // Sicherheits-Check: Falls immer noch unter der Welt (z.B. Void), setze auf sichere Höhe
            if (y <= world.getBottomY()) {
                y = 70; // Fallback Höhe
                Simpletweaks.LOGGER.warn("Konnte keine sichere Spawn-Höhe finden (Y=" + y + "). Setze Fallback auf 70.");
            }
        }

        // Zugriff auf den aktuellen Spawn-Punkt
        BlockPos currentSpawnPos = world.getSpawnPoint().getPos();

        if (currentSpawnPos.getX() != x || currentSpawnPos.getY() != y || currentSpawnPos.getZ() != z) {
            BlockPos newPos = new BlockPos(x, y, z);
            float angle = 0.0f;

            world.setSpawnPoint(WorldProperties.SpawnPoint.create(world.getRegistryKey(), newPos, angle, 0.0f));
            Simpletweaks.LOGGER.info("Set World Spawn to: " + x + ", " + y + ", " + z);
        }
    }
}