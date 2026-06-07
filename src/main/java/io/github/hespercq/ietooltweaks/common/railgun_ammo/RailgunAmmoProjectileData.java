package io.github.hespercq.ietooltweaks.common.railgun_ammo;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.hespercq.ietooltweaks.common.util.IEToolTweaksCodecs;

public record RailgunAmmoProjectileData(

		double damage, double gravity, double breakChance, boolean useUpgradedProjectile,

		RailgunAmmoCommandHooks commandHooks, Optional<List<List<Integer>>> colorData

) {

	public static final RailgunAmmoProjectileData DEFAULT = new RailgunAmmoProjectileData(

			10f, 1.05f, 0.25f, true, RailgunAmmoCommandHooks.EMPTY, Optional.empty()

	);

	public static final Codec<List<List<Integer>>> COLOR_DATA_CODEC = IEToolTweaksCodecs.COLOR_CODEC.listOf().listOf();

	public static final Codec<RailgunAmmoProjectileData> CODEC = RecordCodecBuilder.create(instance -> instance.group(

			Codec.DOUBLE.optionalFieldOf("damage", DEFAULT.damage()).forGetter(RailgunAmmoProjectileData::damage),

			Codec.DOUBLE.optionalFieldOf("gravity", DEFAULT.gravity()).forGetter(RailgunAmmoProjectileData::gravity),

			Codec.DOUBLE.optionalFieldOf("breakChance", DEFAULT.breakChance()).forGetter(RailgunAmmoProjectileData::breakChance),

			Codec.BOOL.optionalFieldOf("useUpgradedProjectile", DEFAULT.useUpgradedProjectile()).forGetter(RailgunAmmoProjectileData::useUpgradedProjectile),

			RailgunAmmoCommandHooks.CODEC.optionalFieldOf("commandHooks", DEFAULT.commandHooks()).forGetter(RailgunAmmoProjectileData::commandHooks),

			COLOR_DATA_CODEC.optionalFieldOf("railgunRenderColors").forGetter(RailgunAmmoProjectileData::colorData))

			.apply(instance, RailgunAmmoProjectileData::new));
}
