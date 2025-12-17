package com.simpletweaks.client.gui.tooltip;

import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.map.MapState;

public record MapTooltipData(MapIdComponent mapId, MapState mapState) implements TooltipData {
}