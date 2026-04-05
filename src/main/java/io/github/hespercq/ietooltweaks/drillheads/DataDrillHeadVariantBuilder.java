package io.github.hespercq.ietooltweaks.drillheads;

import java.util.Map;
import java.util.Optional;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import io.github.hespercq.ietooltweaks.helpers.SafeJsonObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public class DataDrillHeadVariantBuilder {

	public static final Map<Integer, Tier> TIER_MAP = Map.of(0, Tiers.WOOD, 1, Tiers.STONE, 2, Tiers.IRON, 3, Tiers.DIAMOND, 4, Tiers.NETHERITE);
	// Required
	private final String id;
	private final String name;

	// Mining defaults
	private int miningSize = 1;
	private int miningDepth = 1;
	private Tier miningLevel = Tiers.WOOD;
	private float miningSpeed = 1.0f;

	// Mining Veins Optionals
	private Optional<TagKey<Block>> veinMiningTag = Optional.empty();
	private int veinMiningSize = 0;

	// Stats defaults
	private int durability = 250;
	private int attackDamage = 1;
	private Ingredient repairMaterial = Ingredient.EMPTY;

	// Rendering defaults
	private int color = 0xFFFFFF;
	private ResourceLocation texture = ImmersiveEngineering.rl("item/drill_diesel");
	private Optional<Float> itemModelOverrideId = Optional.empty();

	public DataDrillHeadVariantBuilder(String id) {
		this.id = id;
		this.name = id.contains("/") ? id.substring(id.lastIndexOf('/') + 1) : id;
	}

	// -------- Mining --------

	public DataDrillHeadVariantBuilder miningSize(int miningSize) {
		this.miningSize = miningSize;
		return this;
	}

	public DataDrillHeadVariantBuilder miningDepth(int miningDepth) {
		this.miningDepth = miningDepth;
		return this;
	}

	public DataDrillHeadVariantBuilder miningLevel(Tier miningLevel) {
		this.miningLevel = miningLevel;
		return this;
	}

	public DataDrillHeadVariantBuilder miningSpeed(float miningSpeed) {
		this.miningSpeed = miningSpeed;
		return this;
	}

	public DataDrillHeadVariantBuilder veinMiningTag(TagKey<Block> tag) {
		this.veinMiningTag = Optional.of(tag);
		return this;
	}

	public DataDrillHeadVariantBuilder veinMiningSize(int size) {
		this.veinMiningSize = size;
		return this;
	}

	// -------- Stats --------

	public DataDrillHeadVariantBuilder durability(int durability) {
		this.durability = durability;
		return this;
	}

	public DataDrillHeadVariantBuilder attackDamage(int attackDamage) {
		this.attackDamage = attackDamage;
		return this;
	}

	public DataDrillHeadVariantBuilder repairMaterial(Ingredient repairMaterial) {
		this.repairMaterial = repairMaterial;
		return this;
	}

	// -------- Rendering --------

	public DataDrillHeadVariantBuilder color(int color) {
		this.color = color;
		return this;
	}

	public DataDrillHeadVariantBuilder texture(ResourceLocation texture) {
		this.texture = texture;
		return this;
	}

	public DataDrillHeadVariantBuilder itemModelOverride(float itemModelOverrideId) {
		this.itemModelOverrideId = Optional.of(itemModelOverrideId);
		return this;
	}

	public DataDrillHeadVariantBuilder fromSafeJsonObject(SafeJsonObject json) {
		// Mining
		DataDrillHeadVariantBuilder builder = this;
		json.getInt("miningSize").ifPresent(builder::miningSize);
		json.getInt("miningDepth").ifPresent(builder::miningDepth);
		json.getInt("miningTier").ifPresent((miningTierInt) -> {
			Tier miningTier = TIER_MAP.get(miningTierInt);
			if (miningTier != null) {
				builder.miningLevel(miningTier);
			}
		});
		json.getFloat("miningSpeed").ifPresent(builder::miningSpeed);
		// Mining - Vein Mine
		json.getResourceLocation("veinMiningTag").ifPresent((veinMiningTagResourceLocation) -> {
			builder.veinMiningTag(TagKey.create(Registries.BLOCK, veinMiningTagResourceLocation));
		});
		json.getInt("veinMiningSize").ifPresent(builder::veinMiningSize);

		// Attack, durability, repair
		json.getInt("attackDamage").ifPresent(builder::attackDamage);
		json.getInt("durability").ifPresent(builder::durability);
		json.getIngredient("repairMaterial").ifPresent(builder::repairMaterial);

		// Render stuff
		json.getInt("color").ifPresent(builder::color);
		json.getFloat("itemModelOverrideId").ifPresent(builder::itemModelOverride);
		json.getResourceLocation("texture").ifPresent(builder::texture);
		return builder;
	}

	// -------- Build --------

	public DataDrillHeadVariant build() {
		return new DataDrillHeadVariant(id, name,

				miningSize, miningDepth, miningLevel, miningSpeed,

				veinMiningTag, veinMiningSize,

				attackDamage, durability, repairMaterial,

				color, texture, itemModelOverrideId);
	}

}