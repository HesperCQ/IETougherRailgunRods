package io.github.hespercq.ietooltweaks.common.drillheads;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import io.github.hespercq.ietooltweaks.common.util.IEToolTweaksCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public record DrillHeadVariant(

		int miningSize, int miningDepth, Tier miningLevel, float miningSpeed,

		Optional<TagKey<Block>> veinMiningTag, int veinMiningSize,

		int attackDamage, int durability, Ingredient repairMaterial,

		int color, ResourceLocation texture, Optional<Float> itemModelOverrideId) {

	public boolean isVeinMining() {
		return veinMiningTag.isPresent() & veinMiningSize > 0;
	}

	private static final Codec<TagKey<Block>> BLOCK_TAG_CODEC = ResourceLocation.CODEC.xmap(id -> TagKey.create(Registries.BLOCK, id), TagKey::location);

	public static final Codec<DrillHeadVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(

			Codec.INT.optionalFieldOf("miningSize", 1).forGetter(DrillHeadVariant::miningSize),

			Codec.INT.optionalFieldOf("miningDepth", 1).forGetter(DrillHeadVariant::miningDepth),

			IEToolTweaksCodecs.TIER.optionalFieldOf("miningTier", Tiers.WOOD).forGetter(DrillHeadVariant::miningLevel),

			Codec.FLOAT.optionalFieldOf("miningSpeed", 1.0F).forGetter(DrillHeadVariant::miningSpeed),

			BLOCK_TAG_CODEC.optionalFieldOf("veinMiningTag").forGetter(DrillHeadVariant::veinMiningTag),

			Codec.INT.optionalFieldOf("veinMiningSize", 0).forGetter(DrillHeadVariant::veinMiningSize),

			Codec.INT.optionalFieldOf("attackDamage", 1).forGetter(DrillHeadVariant::attackDamage),

			Codec.INT.optionalFieldOf("durability", 250).forGetter(DrillHeadVariant::durability),

			IEToolTweaksCodecs.INGREDIENT.optionalFieldOf("repairMaterial", Ingredient.EMPTY).forGetter(DrillHeadVariant::repairMaterial),

			IEToolTweaksCodecs.COLOR_CODEC.optionalFieldOf("color", 0xFFFFFF).forGetter(DrillHeadVariant::color),

			ResourceLocation.CODEC.xmap(rl -> rl == null ? ImmersiveEngineering.rl("item/drill_diesel") : rl, rl -> rl).optionalFieldOf("texture", ImmersiveEngineering.rl("item/drill_diesel"))
					.forGetter(DrillHeadVariant::texture),

			Codec.FLOAT.optionalFieldOf("itemModelOverrideId").forGetter(DrillHeadVariant::itemModelOverrideId)

	).apply(instance, DrillHeadVariant::new));

}
