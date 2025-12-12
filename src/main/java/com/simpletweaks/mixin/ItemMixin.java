package com.simpletweaks.mixin;

import com.simpletweaks.Simpletweaks;
import com.simpletweaks.block.entity.projectile.BrickProjectileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void useThrowableBrick(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        // CONFIG CHECK: Wenn deaktiviert, sofort abbrechen
        if (!Simpletweaks.getConfig().fun.enableThrowableBricks) {
            return;
        }

        Item self = (Item) (Object) this;
        ItemStack itemStack = user.getStackInHand(hand);

        if (self == Items.BRICK || self == Items.NETHER_BRICK || self == Items.RESIN_BRICK) {

            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));

            if (!world.isClient()) {
                BrickProjectileEntity projectile = new BrickProjectileEntity(world, user);
                projectile.setItem(itemStack);
                projectile.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);
                world.spawnEntity(projectile);
            }

            user.incrementStat(Stats.USED.getOrCreateStat(self));
            if (!user.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }

            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}