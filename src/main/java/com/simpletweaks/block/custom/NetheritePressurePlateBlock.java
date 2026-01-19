package com.simpletweaks.block.custom;

import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class NetheritePressurePlateBlock extends PressurePlateBlock {
    public NetheritePressurePlateBlock(BlockSetType type, Settings settings) {
        super(type, settings);
    }

    @Override
    protected int getRedstoneOutput(World world, BlockPos pos) {
        Box box = BOX.offset(pos);
        List<? extends Entity> list = world.getNonSpectatingEntities(PlayerEntity.class, box);

        if (list.isEmpty()) {
            return 0;
        }

        // Check for Barrel below
        BlockPos below = pos.down();
        BlockEntity be = world.getBlockEntity(below);

        // Wenn ein Fass drunter ist, fungiert es als Whitelist
        if (be instanceof BarrelBlockEntity barrel) {
            for (Entity entity : list) {
                if (entity instanceof PlayerEntity player) {
                    if (checkInventory(player, barrel)) {
                        return 15;
                    }
                }
            }
            return 0; // Spieler da, aber falsches Item
        }

        // Kein Fass drunter? -> Reagiere auf jeden Spieler (wie Dia-Platte)
        return 15;
    }

    private boolean checkInventory(PlayerEntity player, BarrelBlockEntity barrel) {
        // Durchsuche das Fass nach erlaubten Items (Whitelist)
        // Wir prüfen, ob der Spieler *eines* der Items aus dem Fass besitzt.
        for (int i = 0; i < barrel.size(); i++) {
            ItemStack whitelistStack = barrel.getStack(i);
            if (!whitelistStack.isEmpty()) {
                if (player.getInventory().contains(whitelistStack.getItem().getDefaultStack())) {
                    return true;
                }
                // Optional: Genauerer Check (NBT/Components), aber contains(Item) ist meist performanter und reicht oft.
                // Für exakte Übereinstimmung müsste man das Inventar iterieren.
                if (player.getInventory().containsAny(stack -> ItemStack.areItemsEqual(stack, whitelistStack))) {
                     return true;
                }
            }
        }
        return false;
    }
}