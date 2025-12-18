package io.github.hespercq.ietooltweaks.drillheads;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public record DataDrillHeadPerms(
	String id,
	String name,
	int drillSize,
	int drillDepth,
	Tier drillLevel,
	float drillSpeed,
	int veinMiningSize,
	TagKey<Block> veinMiningTag,
	int drillAttack,
	int maxDamage,
	Ingredient repairMaterial,
	ResourceLocation texture, int itemColor, float itemModelOverrideId) {
		public boolean isVeinMining() {
			return veinMiningTag != null && veinMiningSize > 0;
		}
}
