package com.simpletweaks.item.custom;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAssetKeys; // Neuer Import
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Unit;

public class SpawnElytraItem extends Item {
    public SpawnElytraItem(Item.Settings settings) {
        super(settings
                // Macht das Item flugfähig (Gleiter)
                .component(DataComponentTypes.GLIDER, Unit.INSTANCE)
                // Macht das Item ausrüstbar im Brust-Slot
                .component(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(EquipmentSlot.CHEST)
                        .equipSound(SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA)
                        .model(EquipmentAssetKeys.ELYTRA) // KORREKTUR: RegistryKey nutzen
                        .build())
        );
    }

    @Override
    public Text getName(ItemStack stack) {
        return Text.literal("Spawn Elytra").formatted(Formatting.AQUA, Formatting.ITALIC);
    }
}