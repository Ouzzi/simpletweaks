package com.simpletweaks.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.UUID;

public class ChunkLoaderBlockEntity extends BlockEntity {
    private UUID owner = null;
    private boolean isLoaded = false;

    public ChunkLoaderBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHUNK_LOADER_BE, pos, state);
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        markDirty();
        if (world != null && !world.isClient()) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    public boolean isOwner(PlayerEntity player) {
        return owner != null && owner.equals(player.getUuid());
    }

    // --- Chunk Loading Logic ---
    public static void tick(World world, BlockPos pos, BlockState state, ChunkLoaderBlockEntity be) {
        if (world.isClient() || be.isLoaded) return;

        if (world instanceof ServerWorld serverWorld) {
            ChunkPos chunkPos = new ChunkPos(pos);
            // Chunk als "Forced" markieren -> Bleibt geladen
            serverWorld.setChunkForced(chunkPos.x, chunkPos.z, true);
            be.isLoaded = true;
        }
    }

    @Override
    public void markRemoved() {
        // Wenn der Block abgebaut wird, Chunk entladen
        if (world instanceof ServerWorld serverWorld) {
            ChunkPos chunkPos = new ChunkPos(pos);
            serverWorld.setChunkForced(chunkPos.x, chunkPos.z, false);
        }
        super.markRemoved();
    }

    // --- Data & Sync ---
    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        if (owner != null) view.put("Owner", Uuids.INT_STREAM_CODEC, owner);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        view.read("Owner", Uuids.INT_STREAM_CODEC).ifPresent(uuid -> this.owner = uuid);
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}