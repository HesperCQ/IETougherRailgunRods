package io.github.hespercq.ietooltweaks.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;

public class IEToolTweaksCodecs {
	public static final Codec<Ingredient> INGREDIENT = Codec.PASSTHROUGH.xmap(dynamic -> Ingredient.fromJson(dynamic.convert(JsonOps.INSTANCE).getValue()),
			ingredient -> new Dynamic<>(JsonOps.INSTANCE, ingredient.toJson()));

	public static final Codec<Tier> TIER = Codec.INT.xmap(tier -> switch (tier) {
	case 0 -> Tiers.WOOD;
	case 1 -> Tiers.STONE;
	case 2 -> Tiers.IRON;
	case 3 -> Tiers.DIAMOND;
	case 4 -> Tiers.NETHERITE;
	default -> Tiers.WOOD;
	}, tier -> {
		if (tier == Tiers.WOOD)
			return 0;
		if (tier == Tiers.STONE)
			return 1;
		if (tier == Tiers.IRON)
			return 2;
		if (tier == Tiers.DIAMOND)
			return 3;
		if (tier == Tiers.NETHERITE)
			return 4;
		return 0;
	});

	public static final Codec<Integer> COLOR_CODEC = Codec.either(Codec.INT, Codec.STRING).xmap(either -> either.map(integer -> integer, str -> {
		String s = str.trim();

		if (s.startsWith("#")) {
			s = s.substring(1);
		}
		else if (s.startsWith("0x") || s.startsWith("0X")) {
			s = s.substring(2);
		}

		return Integer.parseUnsignedInt(s, 16);
	}), color -> com.mojang.datafixers.util.Either.left(color));
}
