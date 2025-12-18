package io.github.hespercq.ietooltweaks.drillheads;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import io.github.hespercq.ietooltweaks.IEToolTweaks;
import io.github.hespercq.ietooltweaks.helpers.DatapackJsonObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public class DataDrillHeadPermsDataLoader extends SimpleJsonResourceReloadListener {
	// Data for constructor
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final String FOLDER = "drill_heads";

	// Constants
	public static final Map<Integer, Tier> TIER_MAP = Map.of(0, Tiers.WOOD, 1, Tiers.STONE, 2, Tiers.IRON, 3, Tiers.DIAMOND, 4, Tiers.NETHERITE);
	public static final DataDrillHeadPerms DEBUG = new DataDrillHeadPerms("debug", "debug", 1, 1, Tiers.WOOD, 1, 0, null, 1, 1, Ingredient.EMPTY,
			ResourceLocation.fromNamespaceAndPath(ImmersiveEngineering.MODID, "item/drill_diesel"), 0xFFFFFF, 0);

	// Stores loaded drill head permanents
	public static final Map<String, DataDrillHeadPerms> DRILL_HEAD_PERMS = new HashMap<>();

	public DataDrillHeadPermsDataLoader() {
		super(GSON, FOLDER);
		// <-- Folder inside data/<modid>/
	}

	public static Map<String, DataDrillHeadPerms> getMap() {
		return DRILL_HEAD_PERMS;
	}

	public static Set<String> getIds() {
		return getMap().keySet();
	}

	public static DataDrillHeadPerms getData(String hdId) {
		return getMap().getOrDefault(hdId, DEBUG);
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager resourceManager, ProfilerFiller profiler) {
		DRILL_HEAD_PERMS.clear();

		jsons.forEach((rl, json) -> {
			try {
				DatapackJsonObject obj = new DatapackJsonObject(json.getAsJsonObject());

				String id = rl.getPath();
				String name = id.contains("/") ? id.substring(id.lastIndexOf('/') + 1) : id;

				// Mining Area
				int drillSize = obj.getIntOr("size", 1);
				int drillDepth = obj.getIntOr("depth", 1);

				// Mining Level
				int tierInt = obj.getIntOr("tier", 1);
				Tier drillLevel = TIER_MAP.getOrDefault(tierInt, Tiers.WOOD);

				// Mining Speed
				float drillSpeed = obj.getFloatOr("speed", 1f);

				// Vein Mining
				int veinMiningSize = obj.getIntOr("veinMiningSize", 0);
				ResourceLocation veinMiningTagRL = obj.getResourceLocationOr("veinMiningTag", null);
				TagKey<Block> veinMiningTag = veinMiningTagRL != null ? TagKey.create(Registries.BLOCK, veinMiningTagRL) : null;

				// Attack, Durability & Repair
				int drillAttack = obj.getIntOr("attack", 1);
				int maxDamage = obj.getIntOr("durability", 100);
				Ingredient repairMaterial = obj.getIngredientOr("repairMaterial", Ingredient.EMPTY);

				// Render stuff
				int itemColor = obj.getIntOr("itemColor", 0xFFFFFF);
				float itemModelOverrideId = obj.getFloatOr("itemModelOverrideId", 0.0f);
				ResourceLocation texture = obj.getResourceLocationOr("texture", ImmersiveEngineering.rl("item/drill_diesel"));

				// Build & add to Map
				DataDrillHeadPerms perm = new DataDrillHeadPerms(id, name, drillSize, drillDepth, drillLevel, drillSpeed, veinMiningSize, veinMiningTag, drillAttack, maxDamage, repairMaterial,
						texture, itemColor, itemModelOverrideId);
				DRILL_HEAD_PERMS.put(id, perm);

				IEToolTweaks.LOGGER.info("Loaded drillhead '{}'", rl);
			}
			catch (Exception e) {
				IEToolTweaks.LOGGER.error("Failed to load drillhead '{}':{}", rl, e);
			}
		});

	}

	/*
	 * private ResourceLocation safeRL(JsonObject obj, String key, ResourceLocation fallback) { if (!obj.has(key)) return fallback; String s = obj.get(key).getAsString(); return
	 * ResourceLocation.isValidResourceLocation(s) ? ResourceLocation.tryParse(s) : fallback; }
	 */

}
