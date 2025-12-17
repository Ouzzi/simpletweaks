package com.simpletweaks.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult; // WICHTIG: Neuer Import
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class LaserPointerItem extends Item {

    public LaserPointerItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        // Erlaubt das "Halten" des Items (wie ein Bogen/Fernglas)
        user.setCurrentHand(hand);

        // Statt TypedActionResult.consume(...) gibt man jetzt einfach ActionResult.CONSUME zurück
        return ActionResult.CONSUME;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, net.minecraft.entity.LivingEntity user) {
        return 72000; // Lange Nutzungsdauer
    }
}