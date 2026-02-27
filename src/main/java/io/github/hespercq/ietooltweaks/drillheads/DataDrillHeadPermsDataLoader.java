package io.github.hespercq.ietooltweaks.drillheads;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import io.github.hespercq.ietooltweaks.IEToolTweaks;
import io.github.hespercq.ietooltweaks.helpers.SafeJsonObject;
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
				SafeJsonObject obj = new SafeJsonObject(json.getAsJsonObject());

				String id = rl.getPath();
				String name = id.contains("/") ? id.substring(id.lastIndexOf('/') + 1) : id;

				// Mining Area
				int drillSize = obj.getInt("size").orElse(1);
				int drillDepth = obj.getInt("depth").orElse(1);

				// Mining Level
				int tierInt = obj.getInt("tier").orElse(1);
				Tier drillLevel = TIER_MAP.getOrDefault(tierInt, Tiers.WOOD);

				// Mining Speed
				float drillSpeed = obj.getFloat("speed").orElse(1f);

				// Vein Mining
				int veinMiningSize = obj.getInt("veinMiningSize").orElse(0);

				ResourceLocation veinMiningTagRL = obj.getResourceLocation("veinMiningTag").orElse(null);

				TagKey<Block> veinMiningTag = veinMiningTagRL != null ? TagKey.create(Registries.BLOCK, veinMiningTagRL) : null;

				// Attack, Durability & Repair
				int drillAttack = obj.getInt("attack").orElse(1);
				int maxDamage = obj.getInt("durability").orElse(100);

				Ingredient repairMaterial = obj.getIngredient("repairMaterial").orElse(Ingredient.EMPTY);

				// Render stuff
				int itemColor = obj.getInt("itemColor").orElse(0xFFFFFF);
				float itemModelOverrideId = obj.getFloat("itemModelOverrideId").orElse(0.0f);

				ResourceLocation texture = obj.getResourceLocation("texture").orElse(ImmersiveEngineering.rl("item/drill_diesel"));

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
}
