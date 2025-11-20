package io.github.hespercq.ietooltweaks.toughRailgun;

import blusunrize.immersiveengineering.api.Lib.DamageTypes;
import blusunrize.immersiveengineering.api.Lib.TurretDamageType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;

public class ToughDamageSources {
	public static DamageSource causeToughRailgunDamage(ToughRailgunShotEntity shot, Entity shooter)
	{
		TurretDamageType turretDamageType = DamageTypes.RAILGUN;
		ResourceKey<DamageType> type = shooter==null ? turretDamageType.turretType() : turretDamageType.playerType();
		final Registry<DamageType> registry = shot.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
		return new DamageSource(registry.getHolderOrThrow(type), shot, shooter);
	}
	
}