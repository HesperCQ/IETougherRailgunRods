package io.github.hespercq.ietooltweaks.common.drillheads;

import net.minecraft.resources.ResourceLocation;

public record DrillHeadVariantHolder(
        ResourceLocation id,
        DrillHeadVariant variant
) {}