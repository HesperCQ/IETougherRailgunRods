package io.github.hespercq.ietooltweaks.helpers;

import java.util.Arrays;
import java.util.stream.Collectors;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class DisplayHelper {

	// TODO Check which approach is better, string in translatable or second Component

	// #region Tag Name
	public static Component getTagDisplayName(TagKey<?> tag) {
		ResourceLocation id = tag.location();
		String registry = tag.registry().location().getPath(); // "block", "item", etc.
		return Component.translatable("tag." + registry + "." + id.getNamespace() + "." + id.getPath().replace('/', '.'), getTagDisplayFallback(id));
	}

	public static String getTagDisplayFallback(ResourceLocation rl) {
		return Arrays.stream(rl.getPath().split("/")).reduce((a, b) -> b) // take only the last path segment
				.map(s -> s.split("_")).stream().flatMap(Arrays::stream).map(s -> s.substring(0, 1).toUpperCase() + s.substring(1)).collect(Collectors.joining(" "));
	}
	// #endregion

	// #region SubItem Name
	public static Component getSubItemDisplayName(String itemKey, String subKey) {
		String fullKey = itemKey + "." + subKey;

		// If a lang key was found
		if (!Component.translatable(fullKey).getString().equals(fullKey)) {
			return Component.translatable(fullKey); // Return translation
		}
		else {
			return getSubItemDisplayFallback(itemKey, subKey);
		}

		// Otherwise generate prefix before item name

	}

	public static Component getSubItemDisplayFallback(String itemKey, String subKey) {
		String[] words = subKey.split("[_\\s]+"); // split on underscores or spaces
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < words.length; i++) {
			if (words[i].isEmpty())
				continue;
			sb.append(Character.toUpperCase(words[i].charAt(0)));
			if (words[i].length() > 1) {
				sb.append(words[i].substring(1).toLowerCase());
			}
			if (i < words.length - 1)
				sb.append(" ");
		}
		sb.toString();
		return Component.literal(sb.toString()).append(" ").append(Component.translatable(itemKey));
	}
	// #endregion
}
