package com.simpletweaks.block.entity;

import com.simpletweaks.block.custom.CopperPressurePlateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class CopperPressurePlateBlockEntity extends BlockEntity {
    private UUID owner = null;
    private int ticksActive = 0;

    public CopperPressurePlateBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COPPER_PRESSURE_PLATE_BE, pos, state);
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

    // --- Tick Logic ---
    public static void tick(World world, BlockPos pos, BlockState state, CopperPressurePlateBlockEntity be) {
        if (world.isClient()) return;

        // Prüfen, ob Spieler auf der Platte ist
        Box box = new Box(pos).offset(0, 0, 0).expand(0.0, 0.5, 0.0); // Flache Box direkt über dem Block
        List<PlayerEntity> list = world.getNonSpectatingEntities(PlayerEntity.class, box);

        boolean playerOnPlate = !list.isEmpty();
        boolean isPowered = state.get(CopperPressurePlateBlock.POWERED);

        if (playerOnPlate) {
            // Oxidierungsstufe ermitteln
            int requiredTicks = 20; // Default: 1 Sekunde (Unaffected)
            if (state.getBlock() instanceof Oxidizable oxidizable) {
                requiredTicks = switch (oxidizable.getDegradationLevel()) {
                    case UNAFFECTED -> 20; // 1 Sekunde
                    case EXPOSED -> 40;    // 2 Sekunden
                    case WEATHERED -> 60;  // 3 Sekunden
                    case OXIDIZED -> 80;   // 4 Sekunden
                };
            }

            if (!isPowered) {
                be.ticksActive++;
                // Sound Effekt während des Wartens (optional, wie beim Teleporter)
                if (be.ticksActive % 5 == 0) {
                     float pitch = 0.5f + ((float) be.ticksActive / requiredTicks);
                     world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HAT.value(), SoundCategory.BLOCKS, 0.2f, pitch);
                }

                if (be.ticksActive >= requiredTicks) {
                    // Aktivieren!
                    world.setBlockState(pos, state.with(CopperPressurePlateBlock.POWERED, true), 3);
                    world.playSound(null, pos, SoundEvents.BLOCK_COPPER_PLACE, SoundCategory.BLOCKS, 0.0f, 1.2f);
                    be.ticksActive = 0;
                }
            }
        } else {
            // Niemand mehr da -> Reset
            be.ticksActive = 0;
            if (isPowered) {
                // Deaktivieren
                world.setBlockState(pos, state.with(CopperPressurePlateBlock.POWERED, false), 3);
                world.playSound(null, pos, SoundEvents.BLOCK_COPPER_STEP, SoundCategory.BLOCKS, 0.7f, 0.8f);
            }
        }
    }

    // --- Data Storage ---
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