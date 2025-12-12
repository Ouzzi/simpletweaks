package com.simpletweaks.block.custom;

import com.mojang.serialization.MapCodec;
import com.simpletweaks.block.entity.ModBlockEntities;
import com.simpletweaks.block.entity.SpawnTeleporterBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SpawnTeleporterBlock extends BlockWithEntity {
    public static final MapCodec<SpawnTeleporterBlock> CODEC = createCodec(SpawnTeleporterBlock::new);

    // Form wie eine Druckplatte (etwas flacher als voller Block)
    protected static final VoxelShape SHAPE = Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D);

    public SpawnTeleporterBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SpawnTeleporterBlockEntity(pos, state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient() && placer instanceof PlayerEntity player) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof SpawnTeleporterBlockEntity teleporter) {
                teleporter.setOwner(player.getUuid());
            }
        }
    }

    @Override
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        // Creative Mode: Immer sofort abbauen (Standardverhalten)
        if (player.isCreative()) {
            return super.calcBlockBreakingDelta(state, player, world, pos);
        }

        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof SpawnTeleporterBlockEntity teleporter) {
            // Besitzer: 2 Sekunden Abbauzeit
            if (teleporter.isOwner(player)) {
                // 1.0f / (2 Sekunden * 20 Ticks) = 1/40 Fortschritt pro Tick
                return 1.0f / 40.0f;
            }
            // Andere: 60 Sekunden Abbauzeit (1 Minute)
            else {
                // 1.0f / (60 Sekunden * 20 Ticks) = 1/1200 Fortschritt pro Tick
                return 1.0f / 1200.0f;
            }
        }

        // Fallback, falls das BlockEntity fehlt (sollte nicht passieren)
        return super.calcBlockBreakingDelta(state, player, world, pos);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.SPAWN_TELEPORTER_BE, SpawnTeleporterBlockEntity::tick);
    }
}