package io.github.hespercq.ietooltweaks.drillheads;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;

public record DataDrillHeadPerms(String id, String name, TagKey<Item> repairMaterial, int drillSize, int drillDepth, Tier drillLevel, float drillSpeed, int drillAttack, int maxDamage,
		ResourceLocation texture, int itemColor, float itemModelOverrideId) {
}
