package com.simpletweaks.mixin.client;

import com.simpletweaks.client.IOrbValue;
import net.minecraft.client.render.entity.state.ExperienceOrbEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ExperienceOrbEntityRenderState.class)
public class ExperienceOrbRenderStateMixin implements IOrbValue {

    @Unique
    private int simpletweaks$value;

    @Override
    public int simpletweaks$getValue() {
        return this.simpletweaks$value;
    }

    @Override
    public void simpletweaks$setValue(int value) {
        this.simpletweaks$value = value;
    }
}