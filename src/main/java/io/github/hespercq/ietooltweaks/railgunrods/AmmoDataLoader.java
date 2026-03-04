package io.github.hespercq.ietooltweaks.railgunrods;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import blusunrize.immersiveengineering.api.tool.RailgunHandler;
import io.github.hespercq.ietooltweaks.IEToolTweaks;
import io.github.hespercq.ietooltweaks.helpers.SafeJsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Ingredient;

public class AmmoDataLoader extends SimpleJsonResourceReloadListener {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final String FOLDER = "railgun_projectiles";

	public AmmoDataLoader() {
		super(GSON, FOLDER);
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager resourceManager, ProfilerFiller profiler) {
		RailgunHandler.projectilePropertyMap.clear();
		// TODO: Only delete standard projectiles, not all
		jsons.forEach(this::processEntry);
	}

	// Stores loaded railgun ammo
	public static final Map<String, IRailgunAmmoData> RAILGUN_AMMO = new HashMap<>();

	// =========================================================
	// Entry Pipeline
	// =========================================================

	private void processEntry(ResourceLocation rl, JsonElement jsonElement) {
		try {
			SafeJsonObject safeJson = new SafeJsonObject(jsonElement.getAsJsonObject());
			IRailgunAmmoData railgunAmmoData = RailgunAmmoData.Builder.fromSafeJsonObject(safeJson).build();
			if (railgunAmmoData.getAmmoIngredient().isEmpty()) {
				IEToolTweaks.LOGGER.warn("Ammo Empty '{}'", rl);
				return;
			}
			/*
			 * boolean projectileDataIsValid = validateAmmoOverlap(rl, projectile); if (!projectileDataIsValid) { return; }
			 */
			RAILGUN_AMMO.put(rl.getPath(), railgunAmmoData);
			registerProjectile(rl, railgunAmmoData);
		}
		catch (Exception e) {
			IEToolTweaks.LOGGER.error("Failed to load railgun projectile data '{}': {}", rl, e.getMessage(), e);
		}
	}

	private void registerProjectile(ResourceLocation rl, IRailgunAmmoData railgunAmmoData) {
		Ingredient newAmmoIngredient = railgunAmmoData.getAmmoIngredient();
		RailgunHandler.registerProjectile(() -> newAmmoIngredient, railgunAmmoData);
		IEToolTweaks.LOGGER.info("[IE-ToolTweaks] Registered railgun projectile: '{}'", rl);
	}

	// =========================================================
	// Helper Logic
	// =========================================================
	/*
	 * private boolean validateAmmoOverlap(ResourceLocation rl, RailgunAmmoData projectile) { Ingredient newAmmoIngredient = projectile.ammo; boolean overlap =
	 * RailgunHandler.projectilePropertyMap.stream().map(Pair::getFirst).map(Supplier::get).anyMatch(existing -> checkIngredientsOverlap(newAmmoIngredient, existing)); if (overlap) {
	 * IEToolTweaks.LOGGER.warn("[IE-ToolTweaks] Railgun projectile not added due to ammo overlap: '{}' <<< {}", rl, newAmmoIngredient.toJson()); return false; } return true; }
	 */
	/*
	 * protected static boolean checkIngredientsOverlap(Ingredient newAmmoIngredient, Ingredient oldAmmoIngredient) { for (ItemStack newAmmoStack : newAmmoIngredient.getItems()) {
	 * IEToolTweaks.LOGGER.info("[IE-ToolTweaks] All items '{}'", newAmmoStack.toString()); if (oldAmmoIngredient.test(newAmmoStack)) { IEToolTweaks.LOGGER.info("[IE-ToolTweaks] OVERLAP! '{}'",
	 * newAmmoStack.toString()); return true; } } return false; }
	 */
}