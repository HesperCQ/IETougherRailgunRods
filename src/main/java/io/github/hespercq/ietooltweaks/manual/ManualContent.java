package io.github.hespercq.ietooltweaks.manual;

import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Pair;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.lib.manual.ManualEntry;
import blusunrize.lib.manual.ManualEntry.SpecialElementData;
import io.github.hespercq.ietooltweaks.IEToolTweaks;
import io.github.hespercq.ietooltweaks.drillheads.DataDrillHeadVariantsDataLoader;
import io.github.hespercq.ietooltweaks.drillheads.DataDrillHeadVariant;
import blusunrize.lib.manual.ManualInstance;
import blusunrize.lib.manual.Tree.InnerNode;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ManualContent {
	public static void setupManualPages() {
		ResourceLocation rlToolTweaks = ResourceLocation.fromNamespaceAndPath(IEToolTweaks.MODID, "tool_tweaks");

		ManualInstance manual = ManualHelper.getManual();
		InnerNode<ResourceLocation, ManualEntry> toolTweaksNode = manual.getRoot().getOrCreateSubnode(rlToolTweaks, 200);

		manual.addEntry(toolTweaksNode, buildDrillHeadsEntry(manual), 10);
		manual.addEntry(toolTweaksNode, buildRailgunProjectilesEntry(manual), 20);
	}

	public static ManualEntry buildDrillHeadsEntry(ManualInstance manual) {
		IEToolTweaks.LOGGER.info("Start buildDrillHeadsEntry");
		ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(manual);
		IEToolTweaks.LOGGER.info("readFromFile");
		builder.readFromFile(ResourceLocation.fromNamespaceAndPath(IEToolTweaks.MODID, "drillheads"));
		IEToolTweaks.LOGGER.info("appendText");
		builder.appendText(ManualContent::getDrillHeadTexts);
		return builder.create();
	}

	public static ManualEntry buildRailgunProjectilesEntry(ManualInstance manual) {
		ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(manual);
		builder.readFromFile(ResourceLocation.fromNamespaceAndPath(IEToolTweaks.MODID, "railgun_ammo"));
		return builder.create();
	}

	public static Pair<String, List<SpecialElementData>> getDrillHeadTexts() {
		StringBuilder text = new StringBuilder();
		List<SpecialElementData> specials = new ArrayList<>();

		IEToolTweaks.LOGGER.warn("Adding drill heads to IE Manual: {}", DataDrillHeadVariantsDataLoader.VARIANTS.values().size());

		List<DataDrillHeadVariant> drillHeadVariants = DataDrillHeadVariantsDataLoader.VARIANTS.values().stream().toList();
		for (DataDrillHeadVariant drillHeadVariant : drillHeadVariants) {

			// Start new page
			text.append("<np>");
			text.append(Component.translatable("item.ie_hcq_tool_tweaks.drillhead".concat(drillHeadVariant.name())).getString());
			text.append("\n");
			text.append(Component.translatable("desc.immersiveengineering.flavour.drillhead.size", new Object[] { drillHeadVariant.miningSize(), drillHeadVariant.miningDepth() }).getString());
			text.append("\n");
			text.append(Component.translatable("desc.immersiveengineering.flavour.drillhead.level", new Object[] { Utils.getHarvestLevelName(drillHeadVariant.miningLevel()) }).getString());
			text.append("\n");
			text.append(Component.translatable("desc.immersiveengineering.flavour.drillhead.speed", new Object[] { Utils.formatDouble((double) drillHeadVariant.miningSpeed(), "0.###") }).getString());
			text.append("\n");
			text.append(
					Component.translatable("desc.immersiveengineering.flavour.drillhead.damage", new Object[] { Utils.formatDouble((double) drillHeadVariant.attackDamage(), "0.###") }).getString());
			text.append("\n");
			text.append(
					Component.translatable("desc.immersiveengineering.flavour.drillhead.damage", new Object[] { Utils.formatDouble((double) drillHeadVariant.attackDamage(), "0.###") }).getString());
			text.append("\n");
			text.append(Component.translatable("desc.immersiveengineering.info.durability", new Object[] { drillHeadVariant.durability() }).getString());
			/*
			 * if (drillHeadVariant.isVeinMining()) { Component.translatable("manual.ie_hcq_tool_tweaks.drillhead.veinMining", new Object[] { drillHeadVariant.veinMiningSize(),
			 * drillHeadVariant.veinMiningTag() }); }
			 */
		}

		return Pair.of(text.toString(), specials);
	}

	/*
	 * private static Pair<String, List<SpecialElementData>> getMineralVeinTexts() { StringBuilder text = new StringBuilder(); List<SpecialElementData> specials = new ArrayList<>(); List<MineralMix>
	 * mineralsToAdd = new ArrayList<>(MineralMix.RECIPES.getRecipes(Minecraft.getInstance().level)); Function<MineralMix, String> toName = mineral -> { String translationKey =
	 * mineral.getTranslationKey(); String localizedName = I18n.get(translationKey); if (localizedName.equals(translationKey)) localizedName = mineral.getPlainName(); return localizedName; };
	 * mineralsToAdd.sort((i1, i2) -> toName.apply(i1).compareToIgnoreCase(toName.apply(i2))); for (MineralMix mineral : mineralsToAdd) { String dimensionString; if (mineral.dimensions != null &&
	 * mineral.dimensions.size() > 0) { StringBuilder validDims = new StringBuilder(); for (ResourceKey<Level> dim : mineral.dimensions) validDims.append((validDims.length() > 0) ? ", " :
	 * "").append("<dim;").append(dim.location()).append(">"); dimensionString = I18n.get("ie.manual.entry.mineralsDimValid", toName.apply(mineral), validDims.getString()); } else dimensionString =
	 * I18n.get("ie.manual.entry.mineralsDimAny", toName.apply(mineral)); List<StackWithChance> formattedOutputs = Arrays.asList(mineral.outputs); List<StackWithChance> formattedSpoils =
	 * Arrays.asList(mineral.spoils); formattedOutputs.sort(Comparator.comparingDouble(i -> -i.chance())); formattedSpoils.sort(Comparator.comparingDouble(i -> -i.chance())); StringBuilder
	 * outputString = new StringBuilder(); NonNullList<ItemStack> sortedOres = NonNullList.create(); for (StackWithChance sorted : formattedOutputs) { outputString.append("\n").append(new
	 * DecimalFormat("00.00").format(sorted.chance() * 100).replaceAll("\\G0", "\u00A0")).append("% ") .append(sorted.stack().get().getHoverName().getString()); sortedOres.add(sorted.stack().get()); }
	 * StringBuilder spoilString = new StringBuilder(); for (StackWithChance sorted : formattedSpoils) { spoilString.append("\n").append(new DecimalFormat("00.00").format(sorted.chance() *
	 * 100).replaceAll("\\G0", "\u00A0")).append("% ") .append(sorted.stack().get().getHoverName().getString()); sortedOres.add(sorted.stack().get()); } specials.add(new
	 * SpecialElementData(mineral.getId().getString(), 0, new ManualElementItem(ManualHelper.getManual(), sortedOres))); String desc = I18n.get("ie.manual.entry.minerals_desc", dimensionString,
	 * outputString.getString(), spoilString.getString()); if (text.length() > 0) text.append("<np>"); text.append("<&").append(mineral.getId()).append(">").append(desc); } return
	 * Pair.of(text.getString(), specials); }
	 */

}
