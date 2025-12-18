package io.github.hespercq.ietooltweaks.helpers;

import com.google.gson.JsonObject;
import io.github.hespercq.ietooltweaks.IEToolTweaks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public class DatapackJsonObject {
	private JsonObject obj;

	public DatapackJsonObject(JsonObject obj) {
		this.obj = obj;
	}

	public int getIntOr(String key, int fallback) {
		if (!obj.has(key) || obj.get(key).isJsonNull()) {
			return fallback;
		}

		try {
			// If it's a number, just return it
			if (obj.get(key).getAsJsonPrimitive().isNumber()) {
				return obj.get(key).getAsInt();
			}
			// Otherwise, treat it as a hex string
			String value = obj.get(key).getAsString();
			if (value.startsWith("#"))
				value = value.substring(1);

			return Integer.parseInt(value, 16);
		}
		catch (Exception e) {
			IEToolTweaks.LOGGER.warn("Invalid int/hex value '{}' for key '{}'", obj.get(key), key);
			return fallback;
		}
	}

	public boolean getBooleanOr(String key, boolean fallback) {
		if (!obj.has(key) || obj.get(key).isJsonNull()) {
			return fallback;
		}

		try {
			return obj.get(key).getAsBoolean();
		}
		catch (Exception e) {
			IEToolTweaks.LOGGER.warn("Invalid boolean value '{}' for key '{}'", obj.get(key), key);
			return fallback;
		}
	}

	public float getFloatOr(String key, float fallback) {
		if (!obj.has(key) || obj.get(key).isJsonNull()) {
			return fallback;
		}

		try {
			return obj.get(key).getAsFloat();
		}
		catch (Exception e) {
			IEToolTweaks.LOGGER.warn("Invalid float value '{}' for key '{}'", obj.get(key), key);
			return fallback;
		}
	}

	public ResourceLocation getResourceLocationOr(String key, ResourceLocation fallback) {
		if (!obj.has(key) || obj.get(key).isJsonNull()) {
			return fallback;
		}

		try {
			String value = obj.get(key).getAsString();
			ResourceLocation rl = ResourceLocation.tryParse(value);

			if (rl == null) {
				// Malformed ResourceLocation string
				IEToolTweaks.LOGGER.warn("Invalid ResourceLocation '{}' for key '{}', using fallback", value, key);
				return fallback;
			}

			return rl;
		}
		catch (ClassCastException | IllegalStateException e) {
			// Value exists but is not a string
			IEToolTweaks.LOGGER.warn("Expected a string for key '{}', but found invalid JSON, using fallback", key, e);
			return fallback;
		}
	}

	public Ingredient getIngredientOr(String key, Ingredient fallback) {
		if (!obj.has(key) || obj.get(key).isJsonNull()) {
			return fallback;
		}

		try {
			return Ingredient.fromJson(obj.get(key));
		}
		catch (Exception e) {
			IEToolTweaks.LOGGER.warn("Invalid ingredient for key '{}', using fallback: {}", key, obj.get(key), e);
			return fallback;
		}
	}

}
