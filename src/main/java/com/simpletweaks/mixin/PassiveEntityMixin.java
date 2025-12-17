package com.simpletweaks.mixin.common;

import com.simpletweaks.Simpletweaks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PassiveEntity.class)
public abstract class PassiveEntityMixin extends LivingEntity {

    protected PassiveEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow public abstract int getBreedingAge();
    @Shadow public abstract void setBreedingAge(int age);

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void onTickMovement(CallbackInfo ci) {
        // Wir prüfen nur, wenn das Entity ein Kind ist (Alter < 0)
        // und einen Custom Name hat.
        if (!this.getEntityWorld().isClient() && this.getBreedingAge() < 0 && this.hasCustomName()) {

            Text customName = this.getCustomName();
            if (customName == null) return;

            String name = customName.getString();
            List<String> suffixes = Simpletweaks.getConfig().qOL.nametagBabySuffixes;

            // Prüfen, ob der Name mit einem der Suffixe endet
            for (String suffix : suffixes) {
                if (name.endsWith(suffix)) {
                    // Alter auf -24000 (Standard Baby-Startwert) zurücksetzen
                    // oder einfach verhindern, dass es hochzählt.
                    // Da tickMovement() das Alter hochzählt, setzen wir es hier fest.
                    // -24000 ist sicher, damit es klein bleibt.
                    if (this.getBreedingAge() > -24000) {
                        this.setBreedingAge(-24000);
                    }
                    return;
                }
            }
        }
    }
}