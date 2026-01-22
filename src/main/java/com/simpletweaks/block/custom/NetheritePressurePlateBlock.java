package com.simpletweaks.block.custom;

import com.simpletweaks.block.entity.NetheritePressurePlateBlockEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NetheritePressurePlateBlock extends PressurePlateBlock implements BlockEntityProvider {

    public NetheritePressurePlateBlock(BlockSetType type, Settings settings) {
        super(type, settings);
    }

    // --- Block Entity Logic ---
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new NetheritePressurePlateBlockEntity(pos, state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient() && placer instanceof PlayerEntity player) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof NetheritePressurePlateBlockEntity plateBE) {
                plateBE.setOwner(player.getUuid());
            }
        }
    }

    // --- Breaking Speed Logic ---
    @Override
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        if (player.isCreative()) return super.calcBlockBreakingDelta(state, player, world, pos);

        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof NetheritePressurePlateBlockEntity plateBE) {
            // Besitzer: Schnell (ca. 1.5 Sekunden) - 1.0f / 30.0f
            if (plateBE.isOwner(player)) return 1.0f / 30.0f;
                // Fremde: Langsamer (ca. 10 Sekunden) - 1.0f / 200.0f (statt 1200 für 60s)
            else return 1.0f / 200.0f;
        }
        return super.calcBlockBreakingDelta(state, player, world, pos);
    }

    // --- Bestehende Barrel-Logik (unverändert) ---
    @Override
    protected int getRedstoneOutput(World world, BlockPos pos) {
        Box box = BOX.offset(pos);
        List<? extends Entity> list = world.getNonSpectatingEntities(PlayerEntity.class, box);

        if (list.isEmpty()) {
            return 0;
        }

        BlockPos below = pos.down();
        BlockEntity be = world.getBlockEntity(below);

        if (be instanceof BarrelBlockEntity barrel) {
            for (Entity entity : list) {
                if (entity instanceof PlayerEntity player) {
                    if (checkInventory(player, barrel)) {
                        return 15;
                    }
                }
            }
            return 0;
        }
        return 15;
    }

    private boolean checkInventory(PlayerEntity player, BarrelBlockEntity barrel) {
        for (int i = 0; i < barrel.size(); i++) {
            ItemStack whitelistStack = barrel.getStack(i);
            if (!whitelistStack.isEmpty()) {
                if (player.getInventory().contains(whitelistStack.getItem().getDefaultStack())) {
                    return true;
                }
                if (player.getInventory().containsAny(stack -> ItemStack.areItemsEqual(stack, whitelistStack))) {
                    return true;
                }
            }
        }
        return false;
    }
}