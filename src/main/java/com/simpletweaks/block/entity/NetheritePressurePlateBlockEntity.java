package com.simpletweaks.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class NetheritePressurePlateBlockEntity extends BlockEntity {
    private UUID owner = null;

    public NetheritePressurePlateBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.NETHERITE_PRESSURE_PLATE_BE, pos, state);
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        markDirty();
        // Trigger Sync zum Client, damit dieser sofort weiß, dass er schneller abbauen darf
        if (world != null && !world.isClient()) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    public boolean isOwner(PlayerEntity player) {
        return owner != null && owner.equals(player.getUuid());
    }

    // --- DATEN SPEICHERN (Angepasst an deine API) ---

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        if (owner != null) {
            view.put("Owner", Uuids.INT_STREAM_CODEC, owner);
        }
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        view.read("Owner", Uuids.INT_STREAM_CODEC).ifPresent(uuid -> this.owner = uuid);
    }

    // --- SYNC MIT CLIENT ---

    // Damit der Client (Renderer/Mining-Logic) die Daten bekommt
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}