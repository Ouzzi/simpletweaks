package com.simpletweaks.block.custom;

import com.mojang.serialization.MapCodec;
import com.simpletweaks.block.entity.ElytraPadBlockEntity;
import com.simpletweaks.block.entity.FlypadBlockEntity;
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

public class FlypadBlock extends BlockWithEntity {
    public static final MapCodec<FlypadBlock> CODEC = createCodec(settings -> new FlypadBlock(settings, 1));
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D); // Etwas höher als ElytraPad?

    private final int tier;

    public FlypadBlock(Settings settings, int tier) {
        super(settings);
        this.tier = tier;
    }

    public int getTier() { return tier; }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() { return CODEC; }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) { return SHAPE; }
    @Override
    public BlockRenderType getRenderType(BlockState state) { return BlockRenderType.MODEL; }
    
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) { return new FlypadBlockEntity(pos, state); }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.FLYPAD_BE, FlypadBlockEntity::tick);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient() && placer instanceof PlayerEntity player) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof ElytraPadBlockEntity pad) {
                pad.setOwner(player.getUuid());
            }
        }
    }

    @Override
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        if (player.isCreative()) return super.calcBlockBreakingDelta(state, player, world, pos);
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof ElytraPadBlockEntity pad) {
            if (pad.isOwner(player)) return 1.0f / 40.0f;
            else return 1.0f / 1200.0f;
        }
        return super.calcBlockBreakingDelta(state, player, world, pos);
    }
}