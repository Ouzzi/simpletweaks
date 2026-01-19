package com.simpletweaks.block.entity;

import com.simpletweaks.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

public class SpawnTeleporterBlockEntity extends BlockEntity {
    private UUID ownerUuid;

    // Runtime data (nicht gespeichert)
    private final Map<UUID, Integer> timeStanding = new HashMap<>();
    private final Map<UUID, Vec3d> lastPositions = new HashMap<>();

    public SpawnTeleporterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPAWN_TELEPORTER_BE, pos, state);
    }

    public void setOwner(UUID uuid) {
        this.ownerUuid = uuid;
        markDirty();
        // WICHTIG: Client informieren, dass sich Daten geändert haben!
        if (world != null && !world.isClient()) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    // --- NEU: Getter für den Mixin ---
    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public boolean isOwner(PlayerEntity player) {
        return ownerUuid != null && ownerUuid.equals(player.getUuid());
    }

    // --- DATEN SPEICHERN (Server) ---
    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        if (ownerUuid != null) {
            view.put("Owner", Uuids.INT_STREAM_CODEC, ownerUuid);
        }
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        view.read("Owner", Uuids.INT_STREAM_CODEC).ifPresent(uuid -> this.ownerUuid = uuid);
    }

    // --- SYNC MIT CLIENT (Neu hinzugefügt!) ---

    // 1. Paket senden, wenn sich der Block ändert (z.B. beim Platzieren)
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    // 2. Daten bereitstellen, wenn der Chunk geladen wird
    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    // ------------------------------------------

    public static void tick(World world, BlockPos pos, BlockState state, SpawnTeleporterBlockEntity be) {
        if (world.isClient()) return;

        Box box = new Box(pos).offset(0, 0.5, 0).expand(0.1, 1.5, 0.1);
        List<ServerPlayerEntity> players = world.getEntitiesByClass(ServerPlayerEntity.class, box, p -> true);

        be.timeStanding.keySet().removeIf(uuid -> players.stream().noneMatch(p -> p.getUuid().equals(uuid)));
        be.lastPositions.keySet().removeIf(uuid -> players.stream().noneMatch(p -> p.getUuid().equals(uuid)));

        for (ServerPlayerEntity player : players) {
            UUID id = player.getUuid();
            Vec3d currentPos = player.getEntityPos();
            Vec3d lastPos = be.lastPositions.get(id);

            boolean moved = lastPos != null && lastPos.squaredDistanceTo(currentPos) > 0.0001;

            if (moved) {
                be.timeStanding.put(id, 0);
                player.sendMessage(Text.literal("Teleport cancelled (moved)").formatted(Formatting.RED), true);
            } else {
                int ticks = be.timeStanding.getOrDefault(id, 0) + 1;
                be.timeStanding.put(id, ticks);

                // FEATURE 3: Partikel beim Warten
                if (world instanceof ServerWorld serverWorld) {
                    // Portal-Partikel um den Spieler herum
                    serverWorld.spawnParticles(
                        ParticleTypes.PORTAL,
                        player.getX(), player.getY() + 0.5, player.getZ(),
                        5,      // Anzahl
                        0.3, 0.5, 0.3, // Streuung (Breite/Höhe)
                        0.1     // Geschwindigkeit
                    );
                }

                if (ticks % 20 == 0 && ticks < 100) {
                    player.sendMessage(Text.literal("Teleporting in " + (5 - (ticks / 20)) + "...").formatted(Formatting.BLUE), true);
                    world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), SoundCategory.BLOCKS, 0.2f, 1.5f + (ticks/200f));
                }

                if (ticks >= 100) {
                    be.timeStanding.put(id, 0);
                    teleportToSpawn(world, player);
                }
            }
            be.lastPositions.put(id, currentPos);
        }
    }

    private static void teleportToSpawn(World world, ServerPlayerEntity player) {
        if (world instanceof ServerWorld serverWorld) {
            assert serverWorld.getServer() != null;
            ServerWorld overworld = serverWorld.getServer().getOverworld();
            BlockPos spawnPos = overworld.getSpawnPoint().getPos();

            world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1f, 1f);

            player.teleport(overworld, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, Set.<PositionFlag>of(), 0f, 0f, false);

            overworld.playSound(null, spawnPos, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.5f, 1f);
            player.sendMessage(Text.literal("Welcome to Spawn!").formatted(Formatting.GOLD, Formatting.BOLD), true);
        }
    }
}