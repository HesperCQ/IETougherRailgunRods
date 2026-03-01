package io.github.hespercq.ietooltweaks.drillheads;

import java.util.Optional;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public record DataDrillHeadVariant(String id, String name,

		int miningSize, int miningDepth, Tier miningLevel, float miningSpeed,

		Optional<TagKey<Block>> veinMiningTag, int veinMiningSize,

		int durability, int attackDamage, Ingredient repairMaterial,

		int color, ResourceLocation texture, Optional<Float> itemModelOverrideId) {

	public boolean isVeinMining() {
		return veinMiningTag.isPresent() && veinMiningSize > 0;
	}

}
