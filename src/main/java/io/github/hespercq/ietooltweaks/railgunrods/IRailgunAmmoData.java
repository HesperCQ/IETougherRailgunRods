package io.github.hespercq.ietooltweaks.railgunrods;

import blusunrize.immersiveengineering.api.tool.RailgunHandler.IRailgunProjectile;
import net.minecraft.world.item.crafting.Ingredient;

public interface IRailgunAmmoData extends IRailgunProjectile {

	/**
	 * @return Ingredient that defines what items are recognized as this ammo
	 */
	abstract Ingredient getAmmoIngredient();

	/**
	 * @return Charge time for firing in ticks
	 */
	default int getChargeDuration() {
		return 20 * 2; // 2 Seconds
	}

}
