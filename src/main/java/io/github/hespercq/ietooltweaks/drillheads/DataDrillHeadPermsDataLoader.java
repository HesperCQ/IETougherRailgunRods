package io.github.hespercq.ietooltweaks.drillheads;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import io.github.hespercq.ietooltweaks.IEToolTweaks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

public class DataDrillHeadPermsDataLoader extends SimpleJsonResourceReloadListener {
	// Data for constructor
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final String FOLDER = "drillheads";
	private static final ResourceLocation DEFAULT_REPAIR_MATERIAL_TAG_RL = ResourceLocation.fromNamespaceAndPath(IEToolTweaks.MODID, "default_repair_materials");

	// Constants
	public static final Map<Integer, Tier> TIER_MAP = Map.of(0, Tiers.WOOD, 1, Tiers.STONE, 2, Tiers.IRON, 3, Tiers.DIAMOND, 4, Tiers.NETHERITE);
	public static final DataDrillHeadPerms INVALID = new DataDrillHeadPerms("invalid", "invalid", TagKey.create(Registries.ITEM, DEFAULT_REPAIR_MATERIAL_TAG_RL), 1, 1, Tiers.WOOD, 1, false, 1, 1,
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
		return getMap().getOrDefault(hdId, INVALID);
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager resourceManager, ProfilerFiller profiler) {

		IEToolTweaks.LOGGER.info("Loading drillheads: {}", jsons.size());
		DRILL_HEAD_PERMS.clear();

		jsons.forEach((rl, json) -> {
			IEToolTweaks.LOGGER.info("Loading drillhead file: {}", rl);

			try {
				JsonObject obj = json.getAsJsonObject();

				String id = rl.getPath();
				String name = id.contains("/") ? id.substring(id.lastIndexOf('/') + 1) : id;

				int drillSize = getIntOr(obj, "size", 1);
				int drillDepth = getIntOr(obj, "depth", 1);

				int tierInt = getIntOr(obj, "tier", 1);
				Tier drillLevel = TIER_MAP.getOrDefault(tierInt, Tiers.WOOD);

				float drillSpeed = getFloatOr(obj, "speed", 1f);
				int drillAttack = getIntOr(obj, "attack", 1);
				int maxDamage = getIntOr(obj, "durability", 100);

				int itemColor = getColorOr(obj, "itemColor", 0xFFFFFF);
				float itemModelOverrideId = getFloatOr(obj, "itemModelOverrideId", 0.0f);

				ResourceLocation texture = safeRL(obj, "texture", ImmersiveEngineering.rl("item/drill_diesel"));

				TagKey<Item> repairMaterialTag = TagKey.create(Registries.ITEM, safeTagRL(obj, "repairMaterialTag", DEFAULT_REPAIR_MATERIAL_TAG_RL));

				boolean veinMining = getBooleanOr(obj, "veinMining", false);

				DataDrillHeadPerms perm = new DataDrillHeadPerms(id, name, repairMaterialTag, drillSize, drillDepth, drillLevel, drillSpeed, veinMining, drillAttack, maxDamage, texture, itemColor,
						itemModelOverrideId);

				DRILL_HEAD_PERMS.put(id, perm);
			}
			catch (Exception e) {
				IEToolTweaks.LOGGER.error("Error loading drill head '{}':", rl, e);
			}
		});

		IEToolTweaks.LOGGER.info("Finished loading drillheads: {}", DRILL_HEAD_PERMS.size());
	}

	private int getIntOr(JsonObject obj, String key, int fallback) {
		return obj.has(key) ? obj.get(key).getAsInt() : fallback;
	}

	private boolean getBooleanOr(JsonObject obj, String key, boolean fallback) {
		return obj.has(key) ? obj.get(key).getAsBoolean() : fallback;
	}

	private float getFloatOr(JsonObject obj, String key, float fallback) {
		return obj.has(key) ? obj.get(key).getAsFloat() : fallback;
	}

	private int getColorOr(JsonObject obj, String key, int fallback) {
		if (!obj.has(key))
			return fallback;
		String hex = obj.get(key).getAsString();
		if (hex.startsWith("#"))
			hex = hex.substring(1);
		try {
			return Integer.parseInt(hex, 16);
		}
		catch (NumberFormatException e) {
			IEToolTweaks.LOGGER.warn("Invalid color hex '{}' for key '{}'", hex, key);
			return fallback;
		}
	}

	private ResourceLocation safeRL(JsonObject obj, String key, ResourceLocation fallback) {
		if (!obj.has(key))
			return fallback;
		String s = obj.get(key).getAsString();
		return ResourceLocation.isValidResourceLocation(s) ? ResourceLocation.tryParse(s) : fallback;
	}

	private ResourceLocation safeTagRL(JsonObject obj, String key, ResourceLocation fallback) {
		if (!obj.has(key))
			return fallback;

		String s = obj.get(key).getAsString();
		ResourceLocation rl = ResourceLocation.tryParse(s);

		if (rl == null) {
			IEToolTweaks.LOGGER.warn("Invalid ResourceLocation '{}' for key '{}'", s, key);
			return fallback;
		}

		return rl;
	}
}
