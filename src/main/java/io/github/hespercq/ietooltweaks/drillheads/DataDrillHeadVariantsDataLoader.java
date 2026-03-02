package io.github.hespercq.ietooltweaks.drillheads;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import io.github.hespercq.ietooltweaks.IEToolTweaks;
import io.github.hespercq.ietooltweaks.helpers.SafeJsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public class DataDrillHeadVariantsDataLoader extends SimpleJsonResourceReloadListener {
	// Data for constructor
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	public static final String FOLDER = "drillheads";

	// Constants
	public static final DataDrillHeadVariant DEBUG = new DataDrillHeadVariantBuilder("debug").build();

	// Stores loaded drill head permanents
	public static final Map<String, DataDrillHeadVariant> VARIANTS = new HashMap<>();

	public DataDrillHeadVariantsDataLoader() {
		super(GSON, FOLDER);
		// <-- Folder inside data/<modid>/
	}

	public static Set<String> getIds() {
		return VARIANTS.keySet();
	}

	public static DataDrillHeadVariant getData(String hdId) {
		return VARIANTS.getOrDefault(hdId, DEBUG);
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager resourceManager, ProfilerFiller profiler) {
		VARIANTS.clear();

		jsons.forEach((rl, json) -> {
			try {
				String id = rl.getPath();
				SafeJsonObject safeJsonObject = new SafeJsonObject(json.getAsJsonObject());
				DataDrillHeadVariant dataDrillHeadVariant = new DataDrillHeadVariantBuilder(id).fromSafeJsonObject(safeJsonObject).build();
				VARIANTS.put(id, dataDrillHeadVariant);
				IEToolTweaks.LOGGER.info("Loaded drill head '{}'", rl);
			}
			catch (Exception e) {
				IEToolTweaks.LOGGER.error("Failed to load drill head '{}':{}", rl, e);
			}
		});

	}
}
