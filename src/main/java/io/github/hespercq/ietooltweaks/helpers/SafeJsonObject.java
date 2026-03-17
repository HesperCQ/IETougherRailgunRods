package io.github.hespercq.ietooltweaks.helpers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import blusunrize.immersiveengineering.api.tool.RailgunHandler;
import io.github.hespercq.ietooltweaks.IEToolTweaks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SafeJsonObject {
	private final JsonObject jsonObject;

	public SafeJsonObject(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	// ---------------------------
	// Nested-path helper
	// ---------------------------
	private Optional<JsonElement> getNested(String path) {
		String[] parts = path.split("/");
		JsonElement element = jsonObject;
		for (String part : parts) {
			if (!element.isJsonObject())
				return Optional.empty();
			JsonObject obj = element.getAsJsonObject();
			if (!obj.has(part) || obj.get(part).isJsonNull())
				return Optional.empty();
			element = obj.get(part);
		}
		return Optional.of(element);
	}

	// ---------------------------
	// Primitive / object getters
	// ---------------------------
	public Optional<Integer> getInt(String path) {
		try {
			return getNested(path).flatMap(el -> {
				if (el.isJsonPrimitive() && el.getAsJsonPrimitive().isNumber())
					return Optional.of(el.getAsInt());
				String value = el.getAsString();
				return Optional.of(parseHexColor(value));
			});
		}
		catch (Exception e) {
			IEToolTweaks.LOGGER.warn("Failed to parse int/hex for path '{}'", path, e);
			return Optional.empty();
		}
	}

	public Optional<Boolean> getBoolean(String path) {
		try {
			return getNested(path).map(JsonElement::getAsBoolean);
		}
		catch (Exception e) {
			IEToolTweaks.LOGGER.warn("Failed to parse boolean for path '{}'", path, e);
			return Optional.empty();
		}
	}

	public Optional<Float> getFloat(String path) {
		try {
			return getNested(path).map(JsonElement::getAsFloat);
		}
		catch (Exception e) {
			IEToolTweaks.LOGGER.warn("Failed to parse float for path '{}'", path, e);
			return Optional.empty();
		}
	}

	public Optional<Double> getDouble(String path) {
		try {
			return getNested(path).map(JsonElement::getAsDouble);
		}
		catch (Exception e) {
			IEToolTweaks.LOGGER.warn("Failed to parse double for path '{}'", path, e);
			return Optional.empty();
		}
	}

	public Optional<String> getString(String path) {
		try {
			return getNested(path).map(JsonElement::getAsString);
		}
		catch (Exception e) {
			IEToolTweaks.LOGGER.warn("Failed to parse string for path '{}'", path, e);
			return Optional.empty();
		}
	}

	public Optional<ResourceLocation> getResourceLocation(String path) {
		try {
			return getString(path).flatMap(s -> {
				ResourceLocation rl = ResourceLocation.tryParse(s);
				if (rl == null) {
					IEToolTweaks.LOGGER.warn("Invalid ResourceLocation '{}' for path '{}'", s, path);
					return Optional.empty();
				}
				return Optional.of(rl);
			});
		}
		catch (Exception e) {
			IEToolTweaks.LOGGER.warn("Failed to parse ResourceLocation for path '{}'", path, e);
			return Optional.empty();
		}
	}

	public Optional<Ingredient> getIngredient(String path) {
		try {
			return getNested(path).map(Ingredient::fromJson);
		}
		catch (Exception e) {
			IEToolTweaks.LOGGER.warn("Failed to parse Ingredient for path '{}'", path, e);
			return Optional.empty();
		}
	}

	public Optional<RailgunHandler.RailgunRenderColors> getRailgunRenderColors(String path) {
		try {
			Optional<JsonElement> rootOpt = getNested(path);
			if (rootOpt.isEmpty() || !rootOpt.get().isJsonArray())
				return Optional.empty();

			JsonArray root = rootOpt.get().getAsJsonArray();
			if (root.size() == 0)
				return Optional.empty();

			List<int[]> rings = new ArrayList<>();
			boolean isNestedArray = root.get(0).isJsonArray();

			if (!isNestedArray) {
				int[] gradient = new int[root.size()];
				for (int i = 0; i < root.size(); i++)
					gradient[i] = parseHexColor(root.get(i).getAsString());
				rings.add(gradient);
			}
			else {
				int gradientLength = -1;
				for (JsonElement ringEl : root) {
					if (!ringEl.isJsonArray())
						return Optional.empty();
					JsonArray gradArr = ringEl.getAsJsonArray();
					if (gradArr.size() == 0)
						return Optional.empty();
					if (gradientLength == -1)
						gradientLength = gradArr.size();
					else if (gradArr.size() != gradientLength)
						return Optional.empty();

					int[] gradient = new int[gradientLength];
					for (int i = 0; i < gradientLength; i++)
						gradient[i] = parseHexColor(gradArr.get(i).getAsString());
					rings.add(gradient);
				}
			}

			return Optional.of(new RailgunHandler.RailgunRenderColors(rings.toArray(new int[0][])));
		}
		catch (Exception e) {
			return Optional.empty();
		}
	}

	public Optional<List<String>> getStringArray(String path) {
		try {
			Optional<JsonElement> arrayOpt = getNested(path);
			if (arrayOpt.isEmpty() || !arrayOpt.get().isJsonArray())
				return Optional.empty();

			JsonArray array = arrayOpt.get().getAsJsonArray();
			List<String> result = new ArrayList<>();
			for (JsonElement el : array) {
				if (el.isJsonNull())
					return Optional.empty();
				result.add(el.getAsString());
			}
			return Optional.of(result);
		}
		catch (Exception e) {
			IEToolTweaks.LOGGER.warn("Failed to parse String array for path '{}': {}", path, e.getMessage());
			return Optional.empty();
		}
	}

	// ---------------------------
	// Utility
	// ---------------------------
	private int parseHexColor(String color) {
		color = color.toLowerCase();
		if (color.startsWith("#"))
			color = color.substring(1);
		else if (color.startsWith("0x"))
			color = color.substring(2);
		return Integer.parseInt(color, 16);
	}
}