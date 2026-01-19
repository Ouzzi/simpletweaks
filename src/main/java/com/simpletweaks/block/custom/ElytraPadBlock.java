package com.simpletweaks.block.custom;

import com.mojang.serialization.MapCodec;
import com.simpletweaks.block.entity.ElytraPadBlockEntity;
import com.simpletweaks.block.entity.ModBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ElytraPadBlock extends BlockWithEntity {
    public static final MapCodec<ElytraPadBlock> CODEC = createCodec(settings -> new ElytraPadBlock(settings, 1)); // Default fallback

    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);

    // Das Feld für das Tier
    private final int tier;

    public ElytraPadBlock(Settings settings, int tier) {
        super(settings);
        this.tier = tier;
    }

    public int getTier() {
        return tier;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() { return CODEC; }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) { return BlockRenderType.MODEL; }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ElytraPadBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.ELYTRA_PAD_BE, ElytraPadBlockEntity::tick);
    }
}