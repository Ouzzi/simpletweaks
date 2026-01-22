package com.simpletweaks.block.custom;

import com.mojang.serialization.MapCodec;
import com.simpletweaks.block.entity.CopperPressurePlateBlockEntity;
import com.simpletweaks.block.entity.ModBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CopperPressurePlateBlock extends BlockWithEntity implements Oxidizable {
    // MapCodec muss für jede Variante (Level) erstellt werden, hier vereinfacht
    public static final MapCodec<CopperPressurePlateBlock> CODEC = createCodec(settings -> new CopperPressurePlateBlock(Oxidizable.OxidationLevel.UNAFFECTED, settings));
    
    public static final BooleanProperty POWERED = Properties.POWERED;
    protected static final VoxelShape SHAPE = Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D); // Etwas kleiner als voller Block

    private final Oxidizable.OxidationLevel oxidationLevel;

    public CopperPressurePlateBlock(Oxidizable.OxidationLevel oxidationLevel, Settings settings) {
        super(settings);
        this.oxidationLevel = oxidationLevel;
        this.setDefaultState(this.stateManager.getDefaultState().with(POWERED, false));
    }

    @Override
    public Oxidizable.OxidationLevel getDegradationLevel() {
        return this.oxidationLevel;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    // --- Redstone Logic ---
    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) ? 15 : 0;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) && direction == Direction.UP ? 15 : 0;
    }

    // --- Random Tick für Oxidation (Vanilla Logic) ---
    @Override
    public boolean hasRandomTicks(BlockState state) {
        return Oxidizable.getIncreasedOxidationBlock(state.getBlock()).isPresent();
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.tickDegradation(state, world, pos, random);
    }

    // --- Entity & Logic ---
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CopperPressurePlateBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.COPPER_PRESSURE_PLATE_BE, CopperPressurePlateBlockEntity::tick);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient() && placer instanceof PlayerEntity player) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof CopperPressurePlateBlockEntity plate) {
                plate.setOwner(player.getUuid());
            }
        }
    }

    @Override
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        if (player.isCreative()) return super.calcBlockBreakingDelta(state, player, world, pos);
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof CopperPressurePlateBlockEntity plate) {
            if (plate.isOwner(player)) return 1.0f / 30.0f; // Schnell für Owner
            else return 1.0f / 200.0f; // Langsam für andere
        }
        return super.calcBlockBreakingDelta(state, player, world, pos);
    }
}