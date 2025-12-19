package io.github.hespercq.ietooltweaks.railgunrods;

import java.util.Map;
import java.util.function.Supplier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;

import blusunrize.immersiveengineering.api.tool.RailgunHandler;
import blusunrize.immersiveengineering.api.tool.RailgunHandler.IRailgunProjectile;
import io.github.hespercq.ietooltweaks.IEToolTweaks;
import io.github.hespercq.ietooltweaks.helpers.DatapackJsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Ingredient;

public class DataRailgunProjectilesDataLoader extends SimpleJsonResourceReloadListener {
	// Data for constructor
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final String FOLDER = "railgun_projectiles";

	// Constants
	public DataRailgunProjectilesDataLoader() {
		super(GSON, FOLDER);
		// <-- Folder inside data/<modid>/
	}

	@Override protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager resourceManager,
			ProfilerFiller profiler) {
		// Clear all Projectiles
		RailgunHandler.projectilePropertyMap.clear();

		jsons.forEach((rl, json) -> {
			try {
				DatapackJsonObject obj = new DatapackJsonObject(json.getAsJsonObject());

				Ingredient ammo = obj.getIngredientOr("ammo", Ingredient.EMPTY);
				int chargeDuration = obj.getIntOr("chargeDuration", 40);
				float velocity = obj.getFloatOr("velocity", 1);
				float accuracy = obj.getFloatOr("accuracy", 0);
				boolean isValidForTurret = obj.getBooleanOr("isValidForTurret", false);
				double rodDamage = obj.getDoubleOr("rodDamage", 1);
				double rodGravity = obj.getDoubleOr("rodGravity", 1);
				boolean rodisAbstractArrow = obj.getBooleanOr("isValidForTurret", false);
				String rodOnHitCommand = obj.getStringOr("rodGravity", null);

				DataRailgunProjectile dataRailgunProjectile = new DataRailgunProjectile(ammo, chargeDuration, velocity,
						accuracy, isValidForTurret, rodDamage, rodGravity, rodisAbstractArrow, rodOnHitCommand);
				IEToolTweaks.LOGGER.info("Loaded Railgun Projectile Data: '{}'", rl);
				register(dataRailgunProjectile.ammo, dataRailgunProjectile);
			} catch (Exception e) {
				IEToolTweaks.LOGGER.error("Failed to load railgun projectile data '{}':{}", rl, e);
			}
		});

	}

	// HELPER
	// =========================================================================================================
	private static void register(Ingredient newAmmo, IRailgunProjectile railgunProjectile) {
		for (int i = 0; i < RailgunHandler.projectilePropertyMap.size(); i++) {
			Pair<Supplier<Ingredient>, IRailgunProjectile> pair = RailgunHandler.projectilePropertyMap.get(i);
			Ingredient existingAmmo = pair.getFirst().get();
			if (checkSimilarIngredient(existingAmmo, newAmmo)) {
				IEToolTweaks.LOGGER.info(
						"Registering Railgun Projectile: " + newAmmo.toJson().toString() + " - Overwriting Existing");
				RailgunHandler.projectilePropertyMap.set(i, Pair.of(() -> newAmmo, railgunProjectile));
			}
		}
		RailgunHandler.registerProjectile(() -> newAmmo, railgunProjectile);
		IEToolTweaks.LOGGER.info("Registering Railgun Projectile: " + newAmmo.toJson().toString() + " - Done");
	}    

	protected static boolean checkSimilarIngredient(Ingredient ing1, Ingredient ing2) {
		boolean stringEquality = ing1.toJson().toString().equals(ing2.toJson().toString());
		return stringEquality;
	}

}
