package io.github.hespercq.ietooltweaks.railgunrods;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;

import blusunrize.immersiveengineering.api.tool.RailgunHandler;
import io.github.hespercq.ietooltweaks.IEToolTweaks;
import io.github.hespercq.ietooltweaks.helpers.SafeJsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class DataRailgunProjectilesDataLoader extends SimpleJsonResourceReloadListener {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final String FOLDER = "railgun_projectiles";

	public DataRailgunProjectilesDataLoader() {
		super(GSON, FOLDER);
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager resourceManager, ProfilerFiller profiler) {
		RailgunHandler.projectilePropertyMap.clear();
		jsons.forEach(this::processEntry);
	}

	// =========================================================
	// Entry Pipeline
	// =========================================================

	private void processEntry(ResourceLocation rl, JsonElement jsonElement) {
		try {
			SafeJsonObject safeJson = new SafeJsonObject(jsonElement.getAsJsonObject());
			DataRailgunProjectile projectile = DataRailgunProjectile.Builder.fromSafeJsonObject(safeJson).build();
			if (projectile.ammo.isEmpty()) {
				IEToolTweaks.LOGGER.warn("Ammo Empty '{}'", rl);
				return;
			}
			/*
			 * boolean projectileDataIsValid = validateAmmoOverlap(rl, projectile); if (!projectileDataIsValid) { return; }
			 */
			registerProjectile(rl, projectile);
		}
		catch (Exception e) {
			IEToolTweaks.LOGGER.error("Failed to load railgun projectile data '{}': {}", rl, e.getMessage(), e);
		}
	}

	private void registerProjectile(ResourceLocation rl, DataRailgunProjectile projectile) {
		Ingredient newAmmoIngredient = projectile.ammo;
		RailgunHandler.registerProjectile(() -> newAmmoIngredient, projectile);
		IEToolTweaks.LOGGER.info("[IE-ToolTweaks] Registered railgun projectile: '{}'", rl);
	}

	// =========================================================
	// Helper Logic
	// =========================================================

	private boolean validateAmmoOverlap(ResourceLocation rl, DataRailgunProjectile projectile) {
		Ingredient newAmmoIngredient = projectile.ammo;
		boolean overlap = RailgunHandler.projectilePropertyMap.stream().map(Pair::getFirst).map(Supplier::get).anyMatch(existing -> checkIngredientsOverlap(newAmmoIngredient, existing));
		if (overlap) {
			IEToolTweaks.LOGGER.warn("[IE-ToolTweaks] Railgun projectile not added due to ammo overlap: '{}' <<< {}", rl, newAmmoIngredient.toJson());
			return false;
		}
		return true;
	}

	protected static boolean checkIngredientsOverlap(Ingredient newAmmoIngredient, Ingredient oldAmmoIngredient) {
		for (ItemStack newAmmoStack : newAmmoIngredient.getItems()) {
			IEToolTweaks.LOGGER.info("[IE-ToolTweaks] All items '{}'", newAmmoStack.toString());
			if (oldAmmoIngredient.test(newAmmoStack)) {
				IEToolTweaks.LOGGER.info("[IE-ToolTweaks] OVERLAP! '{}'", newAmmoStack.toString());
				return true;
			}
		}
		return false;
	}
}