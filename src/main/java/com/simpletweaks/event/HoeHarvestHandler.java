package com.simpletweaks.event;

import com.simpletweaks.Simpletweaks;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.*;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootWorldContext; // WICHTIG: Neuer Import
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class HoeHarvestHandler {

    public static void register() {
        UseBlockCallback.EVENT.register(HoeHarvestHandler::onRightClickBlock);
    }

    private static ActionResult onRightClickBlock(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        // 1. Basic Checks
        if (!Simpletweaks.getConfig().qOL.enableHoeHarvest) return ActionResult.PASS;
        if (world.isClient()) return ActionResult.PASS;
        if (hand != Hand.MAIN_HAND) return ActionResult.PASS;

        ItemStack stack = player.getMainHandStack();
        // 2. Muss eine Hacke sein
        if (!(stack.getItem() instanceof HoeItem)) return ActionResult.PASS;

        BlockPos pos = hitResult.getBlockPos();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        // 3. Ist es eine erntefähige Pflanze?
        if (isMatureCrop(state, block)) {
            return harvest(player, (ServerWorld) world, pos, state, stack);
        }

        return ActionResult.PASS;
    }

    private static boolean isMatureCrop(BlockState state, Block block) {
        if (block instanceof CropBlock cropBlock) {
            return cropBlock.isMature(state);
        } else if (block instanceof NetherWartBlock) {
            return state.get(NetherWartBlock.AGE) == 3;
        } else if (block instanceof CocoaBlock) {
            return state.get(CocoaBlock.AGE) == 2;
        }
        return false;
    }

    private static ActionResult harvest(PlayerEntity player, ServerWorld world, BlockPos pos, BlockState state, ItemStack tool) {
        // FIX: LootWorldContext statt LootContextParameterSet nutzen
        LootWorldContext.Builder builder = new LootWorldContext.Builder(world)
                .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
                .add(LootContextParameters.TOOL, tool)
                .add(LootContextParameters.THIS_ENTITY, player)
                .add(LootContextParameters.BLOCK_STATE, state);

        List<ItemStack> drops = state.getDroppedStacks(builder);

        // Suche nach dem Samen-Item zum "Bezahlen" des Replants
        ItemStack seedItem = getSeedForBlock(state.getBlock());
        boolean paidSeed = false;

        // Wir entfernen 1 Samen aus den Drops für das Replanting
        for (ItemStack drop : drops) {
            if (drop.getItem() == seedItem.getItem()) {
                drop.decrement(1);
                paidSeed = true;
                break;
            }
        }

        // Falls kein Samen gedroppt wurde, brechen wir ab (außer Creative)
        if (!paidSeed && !player.isCreative()) {
            return ActionResult.PASS;
        }

        // Drops spawnen
        for (ItemStack drop : drops) {
            if (!drop.isEmpty()) {
                Block.dropStack(world, pos, drop);
            }
        }

        // Pflanze zurücksetzen
        BlockState newState = state;
        if (state.getBlock() instanceof CropBlock crop) {
            newState = crop.withAge(0);
        } else if (state.getBlock() instanceof NetherWartBlock) {
            newState = state.with(NetherWartBlock.AGE, 0);
        } else if (state.getBlock() instanceof CocoaBlock) {
            newState = state.with(CocoaBlock.AGE, 0);
        }

        world.setBlockState(pos, newState);

        // Sound abspielen
        world.playSound(null, pos, SoundEvents.BLOCK_CROP_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);

        // Haltbarkeit abziehen (1 Schaden)
        if (!player.isCreative()) {
            tool.damage(2, player, EquipmentSlot.MAINHAND);
        }

        // Arm-Animation
        player.swingHand(Hand.MAIN_HAND, true);

        return ActionResult.SUCCESS;
    }

    private static ItemStack getSeedForBlock(Block block) {
        if (block == Blocks.WHEAT) return new ItemStack(Items.WHEAT_SEEDS);
        if (block == Blocks.POTATOES) return new ItemStack(Items.POTATO);
        if (block == Blocks.CARROTS) return new ItemStack(Items.CARROT);
        if (block == Blocks.BEETROOTS) return new ItemStack(Items.BEETROOT_SEEDS);
        if (block == Blocks.NETHER_WART) return new ItemStack(Items.NETHER_WART);
        if (block == Blocks.COCOA) return new ItemStack(Items.COCOA_BEANS);
        return ItemStack.EMPTY;
    }
}