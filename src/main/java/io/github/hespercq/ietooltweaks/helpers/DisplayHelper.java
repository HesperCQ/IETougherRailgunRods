package io.github.hespercq.ietooltweaks.helpers;

import java.util.Arrays;
import java.util.stream.Collectors;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class DisplayHelper {

	// -----------------------------
	// Tag Display Name
	// -----------------------------
	public static Component getTagDisplayName(TagKey<?> tag) {
		ResourceLocation id = tag.location();
		String registry = tag.registry().location().getPath(); // "block", "item", etc.
		String langKey = "tag." + registry + "." + id.getNamespace() + "." + id.getPath().replace('/', '.');

		Component translation = Component.translatable(langKey);

		// Return translation if exists
		if (!translation.getString().equals(langKey)) {
			return translation;
		}
		// Fallback: last segment of the tag path
		return Component.literal(toDisplayFallback(getLastSegment(id.getPath())));
	}

	// -----------------------------
	// SubItem Display Name
	// -----------------------------
	public static Component getSubItemDisplayName(String itemKey, String subKey) {
		String fullKey = itemKey + "." + subKey;

		Component translation = Component.translatable(fullKey);

		if (!translation.getString().equals(fullKey)) {
			return translation;
		}
		// Fallback: subKey + itemKey translation
		String displayName = toDisplayFallback(getLastSegment(subKey));
		return Component.literal(displayName).append(" ").append(Component.translatable(itemKey));
	}

	// -----------------------------
	// Helper: extract last segment from a path
	// -----------------------------
	public static String getLastSegment(String path) {
		if (path == null || path.isEmpty())
			return "";
		int index = path.lastIndexOf('/');
		return index == -1 ? path : path.substring(index + 1);
	}

	// -----------------------------
	// Helper: convert segment to display fallback
	// -----------------------------
	public static String toDisplayFallback(String segment) {
		return Arrays.stream(segment.split("[_\\s]+")) // split on underscores or spaces
				.filter(s -> !s.isEmpty()).map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase()).collect(Collectors.joining(" "));
	}
}