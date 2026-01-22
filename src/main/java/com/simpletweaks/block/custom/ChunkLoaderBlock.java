package com.simpletweaks.block.custom;

import com.mojang.serialization.MapCodec;
import com.simpletweaks.block.entity.ChunkLoaderBlockEntity;
import com.simpletweaks.block.entity.ModBlockEntities;
import net.minecraft.block.*;
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

public class ChunkLoaderBlock extends BlockWithEntity {
    public static final MapCodec<ChunkLoaderBlock> CODEC = createCodec(ChunkLoaderBlock::new);
    protected static final VoxelShape SHAPE = Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 2.0D, 15.0D);

    public ChunkLoaderBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() { return CODEC; }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) { return SHAPE; }
    @Override
    public BlockRenderType getRenderType(BlockState state) { return BlockRenderType.MODEL; }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ChunkLoaderBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.CHUNK_LOADER_BE, ChunkLoaderBlockEntity::tick);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient() && placer instanceof PlayerEntity player) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof ChunkLoaderBlockEntity loader) {
                loader.setOwner(player.getUuid());
            }
        }
    }

    @Override
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        if (player.isCreative()) return super.calcBlockBreakingDelta(state, player, world, pos);
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof ChunkLoaderBlockEntity loader) {
            if (loader.isOwner(player)) return 1.0f / 30.0f;
            else return 1.0f / 200.0f;
        }
        return super.calcBlockBreakingDelta(state, player, world, pos);
    }
}