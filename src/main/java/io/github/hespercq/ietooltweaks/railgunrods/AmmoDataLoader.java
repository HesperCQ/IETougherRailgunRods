package io.github.hespercq.ietooltweaks.railgunrods;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import blusunrize.immersiveengineering.api.tool.RailgunHandler;
import blusunrize.immersiveengineering.api.tool.RailgunHandler.StandardRailgunProjectile;
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
		RailgunHandler.projectilePropertyMap.removeIf(entry -> {
			return entry.getSecond() instanceof IRailgunAmmoData || entry.getSecond() instanceof StandardRailgunProjectile;
		});
		jsons.forEach(this::processEntry);
	}

	public static final Map<String, IRailgunAmmoData> RAILGUN_AMMO = new HashMap<>();

	// =========================================================
	// Entry Pipeline
	// =========================================================

	private void processEntry(ResourceLocation rl, JsonElement jsonElement) {
		try {
			SafeJsonObject safeJson = new SafeJsonObject(jsonElement.getAsJsonObject());
			IRailgunAmmoData railgunAmmoData = RailgunAmmoData.Builder.fromSafeJsonObject(safeJson).build();
			if (railgunAmmoData.getAmmoIngredient().isEmpty()) {
				IEToolTweaks.LOGGER.warn("[IE-ToolTweaks] Ingredient for railgun ammo is Empty: '{}'", rl);
				return;
			}
			RAILGUN_AMMO.put(rl.getPath(), railgunAmmoData);
			registerProjectile(rl, railgunAmmoData);
		}
		catch (Exception e) {
			IEToolTweaks.LOGGER.error("[IE-ToolTweaks] Failed to load railgun ammo data '{}': {}", rl, e.getMessage(), e);
		}
	}

	private void registerProjectile(ResourceLocation rl, IRailgunAmmoData railgunAmmoData) {
		Ingredient newAmmoIngredient = railgunAmmoData.getAmmoIngredient();
		RailgunHandler.registerProjectile(() -> newAmmoIngredient, railgunAmmoData);
		IEToolTweaks.LOGGER.info("[IE-ToolTweaks] Registered railgun projectile: '{}'", rl);
	}
}