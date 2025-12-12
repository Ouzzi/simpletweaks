package com.simpletweaks.mixin;

import com.simpletweaks.Simpletweaks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class SharpnessGrassCutMixin {

    @Inject(method = "attack", at = @At("HEAD"))
    private void onAttack(Entity target, CallbackInfo ci) {
        if (!Simpletweaks.getConfig().qOL.sharpnessCutsGrass) return;

        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack mainHand = player.getMainHandStack();
        boolean isWeapon = mainHand.isIn(ItemTags.SWORDS) || mainHand.isIn(ItemTags.AXES);

        if (isWeapon) {
            var registry = player.getEntityWorld().getRegistryManager().getOptional(RegistryKeys.ENCHANTMENT);
            if (registry.isPresent()) {
                var sharpness = registry.get().getOptional(Enchantments.SHARPNESS);

                if (sharpness.isPresent()) {
                    int level = EnchantmentHelper.getLevel(sharpness.get(), mainHand);
                    if (level >= 3) {
                        cutGrassAround(player, target);
                    }
                }
            }
        }
    }

    @Unique
    private void cutGrassAround(PlayerEntity player, Entity target) {
        Box box = target.getBoundingBox();
        BlockPos min = BlockPos.ofFloored(box.minX, box.minY, box.minZ);
        BlockPos max = BlockPos.ofFloored(box.maxX, box.maxY, box.maxZ);

        for (BlockPos pos : BlockPos.iterate(min, max)) {
            BlockState state = player.getEntityWorld().getBlockState(pos);

            // Dieselbe erweiterte Logik wie im OutlineMixin
            boolean isVegetation = state.isIn(BlockTags.FLOWERS)
                    || state.getBlock() == Blocks.SHORT_GRASS
                    || state.getBlock() == Blocks.TALL_GRASS
                    || state.getBlock() == Blocks.FERN
                    || state.getBlock() == Blocks.LARGE_FERN
                    || state.getBlock() == Blocks.DEAD_BUSH
                    || state.getBlock() == Blocks.PINK_PETALS
                    || state.getBlock() == Blocks.NETHER_SPROUTS
                    || state.getBlock() == Blocks.CRIMSON_ROOTS
                    || state.getBlock() == Blocks.WARPED_ROOTS
                    || state.isIn(BlockTags.REPLACEABLE);

            if (isVegetation) {
                // Block abbauen und Drops fallen lassen (true)
                player.getEntityWorld().breakBlock(pos, true, player);
            }
        }
    }
}