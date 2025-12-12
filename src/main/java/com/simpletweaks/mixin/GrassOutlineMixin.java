package com.simpletweaks.mixin;

import com.simpletweaks.Simpletweaks;
import net.minecraft.block.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// WICHTIG: Wir greifen jetzt in den BlockState ein, nicht mehr in den Block direkt.
// Das fängt alle Blöcke ab, auch die, die eigene Formen definieren.
@Mixin(AbstractBlock.AbstractBlockState.class)
public class GrassOutlineMixin {

    @Inject(method = "getOutlineShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", at = @At("HEAD"), cancellable = true)
    private void removeOutlineForSharpness(BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        // Prüfen, ob der Kontext ein Spieler ist
        if (context instanceof EntityShapeContext entityContext && entityContext.getEntity() instanceof PlayerEntity player) {

            if (!Simpletweaks.getConfig().qOL.sharpnessCutsGrass) return;

            ItemStack mainHand = player.getMainHandStack();
            boolean isWeapon = mainHand.isIn(ItemTags.SWORDS) || mainHand.isIn(ItemTags.AXES);

            if (isWeapon) {
                // Registry Zugriff sicherstellen (kann client-seitig manchmal null sein, daher vorsichtig)
                if (player.getEntityWorld() == null) return;

                var registryManager = player.getEntityWorld().getRegistryManager();
                if (registryManager == null) return;

                var registry = registryManager.getOptional(RegistryKeys.ENCHANTMENT);
                if (registry.isPresent()) {
                    var sharpness = registry.get().getOptional(Enchantments.SHARPNESS);

                    if (sharpness.isPresent()) {
                        int level = EnchantmentHelper.getLevel(sharpness.get(), mainHand);

                        if (level >= 3) {
                            // Zugriff auf den aktuellen BlockState (da wir im Mixin von AbstractBlockState sind)
                            BlockState state = (BlockState) (Object) this;

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
                                // Gibt eine leere Form zurück -> Man schlägt durch den Block hindurch!
                                cir.setReturnValue(VoxelShapes.empty());
                            }
                        }
                    }
                }
            }
        }
    }
}