package com.simpletweaks.mixin;

import net.minecraft.entity.ExperienceOrbEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ExperienceOrbEntity.class)
public interface ExperienceOrbInvoker {
    @Invoker("setValue")
    void invokeSetValue(int value);
}