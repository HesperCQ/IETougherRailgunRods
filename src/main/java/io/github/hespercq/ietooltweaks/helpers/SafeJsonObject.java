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

	public Optional<Integer> getInt(String key) {
		try {
			if (!jsonObject.has(key) || jsonObject.get(key).isJsonNull())
				return Optional.empty();
			if (jsonObject.get(key).getAsJsonPrimitive().isNumber())
				return Optional.of(jsonObject.get(key).getAsInt());
			String value = jsonObject.get(key).getAsString();
			return Optional.of(parseHexColor(value));
		}
		catch (Exception e) {
			IEToolTweaks.LOGGER.warn("Failed to parse int/hex for key '{}': {}", key, jsonObject.get(key));
			return Optional.empty();
		}
	}

	public Optional<Boolean> getBoolean(String key) {
		try {
			if (!jsonObject.has(key) || jsonObject.get(key).isJsonNull())
				return Optional.empty();
			return Optional.of(jsonObject.get(key).getAsBoolean());
		}
		catch (Exception e) {
			IEToolTweaks.LOGGER.warn("Failed to parse boolean for key '{}': {}", key, jsonObject.get(key));
			return Optional.empty();
		}
	}

	public Optional<Float> getFloat(String key) {
		try {
			if (!jsonObject.has(key) || jsonObject.get(key).isJsonNull())
				return Optional.empty();
			return Optional.of(jsonObject.get(key).getAsFloat());
		}
		catch (Exception e) {
			IEToolTweaks.LOGGER.warn("Failed to parse float for key '{}': {}", key, jsonObject.get(key));
			return Optional.empty();
		}
	}

	public Optional<Double> getDouble(String key) {
		try {
			if (!jsonObject.has(key) || jsonObject.get(key).isJsonNull())
				return Optional.empty();
			return Optional.of(jsonObject.get(key).getAsDouble());
		}
		catch (Exception e) {
			IEToolTweaks.LOGGER.warn("Failed to parse double for key '{}': {}", key, jsonObject.get(key));
			return Optional.empty();
		}
	}

	public Optional<String> getString(String key) {
		try {
			if (!jsonObject.has(key) || jsonObject.get(key).isJsonNull())
				return Optional.empty();
			return Optional.of(jsonObject.get(key).getAsString());
		}
		catch (Exception e) {
			IEToolTweaks.LOGGER.warn("Failed to parse string for key '{}': {}", key, jsonObject.get(key));
			return Optional.empty();
		}
	}

	public Optional<ResourceLocation> getResourceLocation(String key) {
		try {
			if (!jsonObject.has(key) || jsonObject.get(key).isJsonNull())
				return Optional.empty();
			String value = jsonObject.get(key).getAsString();
			ResourceLocation rl = ResourceLocation.tryParse(value);
			if (rl == null) {
				IEToolTweaks.LOGGER.warn("Invalid ResourceLocation '{}' for key '{}'", value, key);
				return Optional.empty();
			}
			return Optional.of(rl);
		}
		catch (Exception e) {
			IEToolTweaks.LOGGER.warn("Failed to parse ResourceLocation for key '{}': {}", key, jsonObject.get(key));
			return Optional.empty();
		}
	}

	public Optional<Ingredient> getIngredient(String key) {
		try {
			if (!jsonObject.has(key) || jsonObject.get(key).isJsonNull())
				return Optional.empty();
			return Optional.of(Ingredient.fromJson(jsonObject.get(key)));
		}
		catch (Exception e) {
			IEToolTweaks.LOGGER.warn("Failed to parse Ingredient for key '{}': {}", key, jsonObject.get(key));
			return Optional.empty();
		}
	}

	public Optional<RailgunHandler.RailgunRenderColors> getRailgunRenderColors(String key) {
		if (!jsonObject.has(key))
			return Optional.empty();

		try {
			JsonArray root = jsonObject.getAsJsonArray(key);
			if (root.size() == 0)
				return Optional.empty();

			List<int[]> rings = new ArrayList<>();

			// Detect structure type
			boolean isNestedArray = root.get(0).isJsonArray();

			if (!isNestedArray) {
				// Treat as single ring gradient
				int gradientLength = root.size();
				int[] gradient = new int[gradientLength];

				for (int i = 0; i < gradientLength; i++)
					gradient[i] = parseHexColor(root.get(i).getAsString());

				rings.add(gradient);
			}
			else {
				int gradientLength = -1;

				for (JsonElement ringElement : root) {
					if (!ringElement.isJsonArray())
						return Optional.empty();

					JsonArray gradientArray = ringElement.getAsJsonArray();

					if (gradientArray.size() == 0)
						return Optional.empty();

					if (gradientLength == -1)
						gradientLength = gradientArray.size();
					else if (gradientArray.size() != gradientLength)
						return Optional.empty();

					int[] gradient = new int[gradientLength];

					for (int i = 0; i < gradientLength; i++)
						gradient[i] = parseHexColor(gradientArray.get(i).getAsString());

					rings.add(gradient);
				}
			}

			return Optional.of(new RailgunHandler.RailgunRenderColors(rings.toArray(new int[0][])));
		}
		catch (Exception e) {
			return Optional.empty();
		}
	}

	private int parseHexColor(String color) {
		color = color.toLowerCase();

		if (color.startsWith("#"))
			color = color.substring(1);
		else if (color.startsWith("0x"))
			color = color.substring(2);

		return Integer.parseInt(color, 16);
	}
}