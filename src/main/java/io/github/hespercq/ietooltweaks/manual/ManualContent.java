package io.github.hespercq.ietooltweaks.manual;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import com.mojang.datafixers.util.Pair;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import blusunrize.immersiveengineering.api.tool.RailgunHandler;
import blusunrize.immersiveengineering.client.manual.ManualElementBlueprint;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.lib.manual.ManualElementCrafting;
import blusunrize.lib.manual.ManualElementItem;
import blusunrize.lib.manual.ManualEntry;
import blusunrize.lib.manual.ManualEntry.SpecialElementData;
import io.github.hespercq.ietooltweaks.IEToolTweaks;
import io.github.hespercq.ietooltweaks.drillheads.DataDrillHeadVariantsDataLoader;
import io.github.hespercq.ietooltweaks.helpers.DisplayHelper;
import io.github.hespercq.ietooltweaks.railgunrods.AmmoDataLoader;
import io.github.hespercq.ietooltweaks.railgunrods.IRailgunAmmoData;
import io.github.hespercq.ietooltweaks.railgunrods.RailgunAmmoData;
import io.github.hespercq.ietooltweaks.register.IEToolTweaksItems;
import io.github.hespercq.ietooltweaks.drillheads.DataDrillHeadVariant;
import blusunrize.lib.manual.ManualInstance;
import blusunrize.lib.manual.SpecialManualElement;
import blusunrize.lib.manual.Tree.InnerNode;
import blusunrize.lib.manual.utils.ManualRecipeRef;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

public class ManualContent {
	public static void setupManualPages() {
		ResourceLocation rlToolTweaks = ResourceLocation.fromNamespaceAndPath(IEToolTweaks.MODID, "tool_tweaks");

		ManualInstance manual = ManualHelper.getManual();
		InnerNode<ResourceLocation, ManualEntry> toolTweaksNode = manual.getRoot().getOrCreateSubnode(rlToolTweaks, 200);

		manual.addEntry(toolTweaksNode, buildDrillHeadsEntry(manual), 10);
		manual.addEntry(toolTweaksNode, buildRailgunProjectilesEntry(manual), 20);
	}

	public static ManualEntry buildDrillHeadsEntry(ManualInstance manual) {
		ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(manual);
		builder.readFromFile(ResourceLocation.fromNamespaceAndPath(IEToolTweaks.MODID, "drillheads"));
		builder.appendText(ManualContent::getDrillHeadTexts);
		return builder.create();
	}

	public static ManualEntry buildRailgunProjectilesEntry(ManualInstance manual) {
		ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(manual);
		builder.readFromFile(ResourceLocation.fromNamespaceAndPath(IEToolTweaks.MODID, "railgun_ammo"));
		builder.appendText(ManualContent::getRailgunAmmoTexts);
		return builder.create();
	}

	// ##############################################################################################
	// Drillheads
	// ##############################################################################################
	public static Pair<String, List<SpecialElementData>> getDrillHeadTexts() {
		StringBuilder text = new StringBuilder();
		List<SpecialElementData> specials = new ArrayList<>();

		IEToolTweaks.LOGGER.warn("Adding drill heads to IE Manual: {}", DataDrillHeadVariantsDataLoader.VARIANTS.values().size());

		// Extra Pages
		List<DataDrillHeadVariant> drillHeadVariants = DataDrillHeadVariantsDataLoader.VARIANTS.values().stream().toList();
		for (DataDrillHeadVariant drillHeadVariant : drillHeadVariants) {
			// Start new page
			text.append("<np>");
			// Header
			// Check recipe exists, otherwise display item
			ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath(IEToolTweaks.MODID, DataDrillHeadVariantsDataLoader.FOLDER + "/" + drillHeadVariant.id());
			ManualRecipeRef manualRecipeRef = new ManualRecipeRef(recipeId);
			SpecialManualElement specialManualElement;
			if (checkCraftingRecipeExists(manualRecipeRef)) {
				// Recipe
				specialManualElement = new ManualElementCrafting(ManualHelper.getManual(), new ManualRecipeRef[][] { { manualRecipeRef } });
			}
			else if (checkBlueprintRecipeExists(manualRecipeRef)) {
				// Recipe
				specialManualElement = new ManualElementBlueprint(ManualHelper.getManual(), new ManualRecipeRef[] { manualRecipeRef });
			}
			else {
				// Item Display
				ItemStack drillHeadVariantItemStack = new ItemStack(IEToolTweaksItems.DRILLHEAD.get());
				drillHeadVariantItemStack.getOrCreateTag().putString("drillhead_variant_id", drillHeadVariant.id());
				specialManualElement = new ManualElementItem(ManualHelper.getManual(), drillHeadVariantItemStack);
			}
			specials.add(new SpecialElementData(drillHeadVariant.id(), 0, specialManualElement));
			text.append("<&").append(drillHeadVariant.id()).append(">");

			// Name
			text.append("\n");
			text.append("§l").append(Component.translatable("item.ie_hcq_tool_tweaks.drillhead.".concat(drillHeadVariant.name())).getString()).append("§r");
			// Stats
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
			text.append(Component.translatable("desc.immersiveengineering.info.durability", new Object[] { drillHeadVariant.durability() }).getString());

			// Optional - Vein Mining
			if (drillHeadVariant.isVeinMining()) {
				drillHeadVariant.veinMiningTag().ifPresent((veinMiningTag) -> {
					text.append("\n");
					text.append(
							Component.translatable("desc.ie_hcq_tool_tweaks.flavour.drillhead.vein", new Object[] { drillHeadVariant.veinMiningSize(), DisplayHelper.getTagDisplayName(veinMiningTag) })
									.getString());
				});
			}

			// If not empty - Repair Material
			if (!drillHeadVariant.repairMaterial().isEmpty()) {
				Component repairMaterialsListComponent = ComponentUtils.formatList(Arrays.stream(drillHeadVariant.repairMaterial().getItems()).map(ItemStack::getHoverName).toList(),
						Component.literal(", "));
				text.append("\n");
				text.append(Component.translatable("manual.ie_hcq_tool_tweaks.drillheads.repair_material", new Object[] { repairMaterialsListComponent.getString() }).getString());
			}
		}

		return Pair.of(text.toString(), specials);
	}

	// ##############################################################################################
	// Railgun Ammo
	// ##############################################################################################
	public static Pair<String, List<SpecialElementData>> getRailgunAmmoTexts() {
		StringBuilder text = new StringBuilder();
		List<SpecialElementData> specials = new ArrayList<>();

		IEToolTweaks.LOGGER.warn("Adding railgun rods to IE Manual: {}", RailgunHandler.projectilePropertyMap.size());

		List<Entry<String, IRailgunAmmoData>> railgunAmmoEntries = AmmoDataLoader.RAILGUN_AMMO.entrySet().stream().toList();

		railgunAmmoEntries.forEach((railgunAmmoEntry) -> {
			String id = railgunAmmoEntry.getKey();
			IRailgunAmmoData railgunAmmo = railgunAmmoEntry.getValue();
			if (!(railgunAmmo instanceof RailgunAmmoData railgunAmmoData)) {
				return;
			}
			// Start new page
			text.append("<np>");

			// Item Display
			ManualElementItem manualElementItem = new ManualElementItem(ManualHelper.getManual(), railgunAmmoData.getAmmoIngredient().getItems());
			specials.add(new SpecialElementData(id + "/items", 0, manualElementItem));
			text.append("<&").append(id + "/items").append(">");

			// Stats
			text.append("\n");
			text.append(
					Component.translatable("desc.immersiveengineering.flavour.drillhead.damage", new Object[] { Utils.formatDouble((double) railgunAmmoData.rodDamage, "0.###") }).getString());

		});

		return Pair.of(text.toString(), specials);
	}

	private static boolean checkCraftingRecipeExists(ManualRecipeRef ref) {
		AtomicBoolean found = new AtomicBoolean(false);
		ref.forEachMatchingRecipe(RecipeType.CRAFTING, r -> found.set(true));
		return found.get();
	}

	private static boolean checkBlueprintRecipeExists(ManualRecipeRef ref) {
		AtomicBoolean found = new AtomicBoolean(false);
		ref.forEachMatchingRecipe(IERecipeTypes.BLUEPRINT.get(), r -> found.set(true));
		return found.get();
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
