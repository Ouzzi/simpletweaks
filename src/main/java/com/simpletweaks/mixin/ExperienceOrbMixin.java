package com.simpletweaks.mixin;

import com.simpletweaks.Simpletweaks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ExperienceOrbEntity.class)
public abstract class ExperienceOrbMixin extends Entity {

    @Shadow private int orbAge;
    @Shadow private int pickingCount;
    @Shadow public abstract int getValue(); // Public method, can be shadowed

    public ExperienceOrbMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    // --- INSTANT PICKUP ---
    // Da der Orb kein "pickupDelay" mehr hat, setzen wir den Delay des Spielers auf 0,
    // sobald er mit dem Orb kollidiert.
    @Inject(method = "onPlayerCollision", at = @At("HEAD"))
    private void instantPickup(PlayerEntity player, CallbackInfo ci) {
        if (!Simpletweaks.getConfig().optimization.enableXpClumps) return;

        // Entfernt die 2-Tick Wartezeit des Spielers
        player.experiencePickUpDelay = 0;
    }

    // --- CLUMPING LOGIK ---
    @Inject(method = "tick", at = @At("HEAD"))
    private void clumpOrbs(CallbackInfo ci) {
        // Nur Server-seitig und wenn aktiviert
        // Performance: Nur alle 5 Ticks prüfen
        if (this.getEntityWorld().isClient() || !Simpletweaks.getConfig().optimization.enableXpClumps || this.age % 5 != 0) return;

        // Radius von 2 Blöcken suchen
        Box box = this.getBoundingBox().expand(2.0);
        List<ExperienceOrbEntity> others = this.getEntityWorld().getEntitiesByClass(ExperienceOrbEntity.class, box,
                e -> e != (Object)this && e.isAlive());

        for (ExperienceOrbEntity other : others) {
            // "this" verschluckt "other"

            // 1. Werte addieren
            int myValue = this.getValue();
            int otherValue = other.getValue();

            // Invoker nutzen, da setValue private ist
            ((ExperienceOrbInvoker) this).invokeSetValue(myValue + otherValue);

            // 3. Anderen Orb löschen
            other.discard();

            // 4. Alter zurücksetzen (damit der große Orb nicht despawnt)
            this.orbAge = 0;
        }
    }
}