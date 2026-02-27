package io.github.hespercq.ietooltweaks.helpers;

import com.google.gson.JsonObject;
import io.github.hespercq.ietooltweaks.IEToolTweaks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Optional;

public class SafeJsonObject {
	private final JsonObject obj;

	public SafeJsonObject(JsonObject obj) {
		this.obj = obj;
	}

	public Optional<Integer> getInt(String key) {
		try {
			if (!obj.has(key) || obj.get(key).isJsonNull())
				return Optional.empty();
			if (obj.get(key).getAsJsonPrimitive().isNumber())
				return Optional.of(obj.get(key).getAsInt());
			String value = obj.get(key).getAsString();
			if (value.startsWith("#"))
				value = value.substring(1);
			return Optional.of(Integer.parseInt(value, 16));
		}
		catch (Exception e) {
			IEToolTweaks.LOGGER.warn("Failed to parse int/hex for key '{}': {}", key, obj.get(key));
			return Optional.empty();
		}
	}

	public Optional<Boolean> getBoolean(String key) {
		try {
			if (!obj.has(key) || obj.get(key).isJsonNull())
				return Optional.empty();
			return Optional.of(obj.get(key).getAsBoolean());
		}
		catch (Exception e) {
			IEToolTweaks.LOGGER.warn("Failed to parse boolean for key '{}': {}", key, obj.get(key));
			return Optional.empty();
		}
	}

	public Optional<Float> getFloat(String key) {
		try {
			if (!obj.has(key) || obj.get(key).isJsonNull())
				return Optional.empty();
			return Optional.of(obj.get(key).getAsFloat());
		}
		catch (Exception e) {
			IEToolTweaks.LOGGER.warn("Failed to parse float for key '{}': {}", key, obj.get(key));
			return Optional.empty();
		}
	}

	public Optional<Double> getDouble(String key) {
		try {
			if (!obj.has(key) || obj.get(key).isJsonNull())
				return Optional.empty();
			return Optional.of(obj.get(key).getAsDouble());
		}
		catch (Exception e) {
			IEToolTweaks.LOGGER.warn("Failed to parse double for key '{}': {}", key, obj.get(key));
			return Optional.empty();
		}
	}

	public Optional<String> getString(String key) {
		try {
			if (!obj.has(key) || obj.get(key).isJsonNull())
				return Optional.empty();
			return Optional.of(obj.get(key).getAsString());
		}
		catch (Exception e) {
			IEToolTweaks.LOGGER.warn("Failed to parse string for key '{}': {}", key, obj.get(key));
			return Optional.empty();
		}
	}

	public Optional<ResourceLocation> getResourceLocation(String key) {
		try {
			if (!obj.has(key) || obj.get(key).isJsonNull())
				return Optional.empty();
			String value = obj.get(key).getAsString();
			ResourceLocation rl = ResourceLocation.tryParse(value);
			if (rl == null) {
				IEToolTweaks.LOGGER.warn("Invalid ResourceLocation '{}' for key '{}'", value, key);
				return Optional.empty();
			}
			return Optional.of(rl);
		}
		catch (Exception e) {
			IEToolTweaks.LOGGER.warn("Failed to parse ResourceLocation for key '{}': {}", key, obj.get(key));
			return Optional.empty();
		}
	}

	public Optional<Ingredient> getIngredient(String key) {
		try {
			if (!obj.has(key) || obj.get(key).isJsonNull())
				return Optional.empty();
			return Optional.of(Ingredient.fromJson(obj.get(key)));
		}
		catch (Exception e) {
			IEToolTweaks.LOGGER.warn("Failed to parse Ingredient for key '{}': {}", key, obj.get(key));
			return Optional.empty();
		}
	}
}