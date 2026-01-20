package com.simpletweaks.block.custom;

import com.mojang.serialization.MapCodec;
import com.simpletweaks.block.entity.ModBlockEntities;
import com.simpletweaks.block.entity.SpawnTeleporterBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

public class SpawnTeleporterBlock extends BlockWithEntity implements Waterloggable {
    public static final MapCodec<SpawnTeleporterBlock> CODEC = createCodec(settings -> new SpawnTeleporterBlock(settings, 1));
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    protected static final VoxelShape SHAPE = Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D);

    private final int tier; // 1, 2, 3, 4

    public SpawnTeleporterBlock(AbstractBlock.Settings settings, int tier) {
        super(settings);
        this.tier = tier;
        this.setDefaultState(this.stateManager.getDefaultState().with(WATERLOGGED, false));
    }
    public int getTier() { return tier; }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return this.getDefaultState().with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (state.get(WATERLOGGED)) {
            tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    // --- Bestehender Code ---

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
        if (player.isCreative()) return super.calcBlockBreakingDelta(state, player, world, pos);
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof SpawnTeleporterBlockEntity teleporter) {
            if (teleporter.isOwner(player)) return 1.0f / 40.0f;
            else return 1.0f / 1200.0f;
        }
        return super.calcBlockBreakingDelta(state, player, world, pos);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.SPAWN_TELEPORTER_BE, SpawnTeleporterBlockEntity::tick);
    }
}