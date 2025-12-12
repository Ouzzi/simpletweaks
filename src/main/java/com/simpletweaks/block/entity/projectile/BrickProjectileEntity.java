package com.simpletweaks.block.entity.projectile;

import com.simpletweaks.Simpletweaks;
import com.simpletweaks.entity.ModEntities;
import com.simpletweaks.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect; // Wichtig
import net.minecraft.particle.ParticleTypes;       // Wichtig
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BrickProjectileEntity extends ThrownItemEntity {

    public BrickProjectileEntity(EntityType<? extends BrickProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public BrickProjectileEntity(World world, LivingEntity owner) {
        super(ModEntities.BRICK_PROJECTILE, owner, world, new ItemStack(Items.BRICK));
    }

    public BrickProjectileEntity(World world, double x, double y, double z) {
        super(ModEntities.BRICK_PROJECTILE, x, y, z, world, new ItemStack(Items.BRICK));
    }

    @Override
    protected Item getDefaultItem() {
        return Items.BRICK;
    }

    @Override
    public void handleStatus(byte status) {
        // Status 3 = Kollision/Impact
        if (status == 3) {
            ItemStack stack = this.getStack();

            // 1. Standard Partikel für das geworfene Item erzeugen (z.B. der Ziegel oder der Brick-Snowball selbst)
            for(int i = 0; i < 8; ++i) {
                this.getEntityWorld().addParticleClient(
                        new ItemStackParticleEffect(ParticleTypes.ITEM, stack),
                        this.getX(), this.getY(), this.getZ(),
                        ((double)this.random.nextFloat() - 0.5D) * 0.08D,
                        ((double)this.random.nextFloat() - 0.5D) * 0.08D,
                        ((double)this.random.nextFloat() - 0.5D) * 0.08D
                );
            }

            // 2. Spezial-Logik für Brick Snowball: Beide Partikelarten mischen
            if (stack.isOf(ModItems.BRICK_SNOWBALL)) {
                // Zusätzliche Ziegel-Partikel (Rot/Braun)
                ItemStack brickStack = new ItemStack(Items.BRICK);
                for(int i = 0; i < 8; ++i) {
                     this.getEntityWorld().addParticleClient(
                            new ItemStackParticleEffect(ParticleTypes.ITEM, brickStack),
                            this.getX(), this.getY(), this.getZ(),
                            ((double)this.random.nextFloat() - 0.5D) * 0.08D,
                            ((double)this.random.nextFloat() - 0.5D) * 0.08D,
                            ((double)this.random.nextFloat() - 0.5D) * 0.08D
                    );
                }

                // Zusätzliche Schneeball-Partikel (Weiß) - für mehr "Schnee-Explosion"
                ItemStack snowStack = new ItemStack(Items.SNOWBALL);
                for(int i = 0; i < 8; ++i) {
                     this.getEntityWorld().addParticleClient(
                            new ItemStackParticleEffect(ParticleTypes.ITEM, snowStack),
                            this.getX(), this.getY(), this.getZ(),
                            ((double)this.random.nextFloat() - 0.5D) * 0.08D,
                            ((double)this.random.nextFloat() - 0.5D) * 0.08D,
                            ((double)this.random.nextFloat() - 0.5D) * 0.08D
                    );
                }
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();

        // FIX 2: damage() benötigt jetzt (ServerWorld, Source, Amount)
        World world = this.getEntityWorld();
        if (world instanceof ServerWorld serverWorld) {

            // SCHADEN AUS CONFIG
            float damageAmount = Simpletweaks.getConfig().fun.brickDamage; // Default

            // Wenn es der Brick Snowball ist, nimm den anderen Wert
            if (this.getStack().isOf(ModItems.BRICK_SNOWBALL)) {
                damageAmount = Simpletweaks.getConfig().fun.brickSnowballDamage;
            }

            entity.damage(serverWorld, this.getDamageSources().thrown(this, this.getOwner()), damageAmount);
        }
    }

    // Wenn ein Block getroffen wird (Glas brechen)
    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (!this.getEntityWorld().isClient()) {
            BlockPos pos = blockHitResult.getBlockPos();
            BlockState state = this.getEntityWorld().getBlockState(pos);

            // NEU: Prüfung der Config Option
            boolean canBreak = Simpletweaks.getConfig().fun.throwableBricksBreakBlocks;

            if (canBreak && shouldBreakBlock(state)) {
                this.getEntityWorld().breakBlock(pos, true, this.getOwner());
                this.playSound(SoundEvents.BLOCK_GLASS_BREAK, 1.0f, 1.0f);
            } else {
                ItemStack stack = this.getStack();
                if (stack.isOf(ModItems.BRICK_SNOWBALL)) {
                    this.playSound(SoundEvents.BLOCK_SNOW_BREAK, 1.0f, 0.8f);
                } else {
                    this.playSound(SoundEvents.BLOCK_STONE_HIT, 1.0f, 1.5f);
                }
            }
        }
    }

    // Allgemeine Kollision (Partikel & Despawn)
    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getEntityWorld().isClient()) {
            this.getEntityWorld().sendEntityStatus(this, (byte)3); // Partikel Effekt
            this.discard(); // Despawn
        }
    }

    private boolean shouldBreakBlock(BlockState state) {
        if (state.isOf(Blocks.TINTED_GLASS)) return false;
        return state.getSoundGroup() == BlockSoundGroup.GLASS;
    }
}