package io.github.hespercq.ietooltweaks.client.ie_manual;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import io.github.hespercq.ietooltweaks.common.drillheads.DrillHeadVariantHolder;
import io.github.hespercq.ietooltweaks.common.drillheads.DrillHeadVariantManager;
import io.github.hespercq.ietooltweaks.common.railgun_ammo.RailgunAmmoManager;
import io.github.hespercq.ietooltweaks.common.register.IEToolTweaksItems;
import io.github.hespercq.ietooltweaks.common.railgun_ammo.RailgunAmmo;
import io.github.hespercq.ietooltweaks.common.railgun_ammo.RailgunAmmoHolder;
import io.github.hespercq.ietooltweaks.common.util.DisplayHelper;
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

		IEToolTweaks.LOGGER.info("Adding drill heads to IE Manual: {}", DrillHeadVariantManager.VARIANTS.values().size());

		// Extra Pages
		var variantHolderSet = DrillHeadVariantManager.getHolderSet();
		for (DrillHeadVariantHolder drillHeadVariantHolder : variantHolderSet) {
			var drillHeadVariant = drillHeadVariantHolder.variant();
			// Start new page
			text.append("<np>");
			// Header
			// Check recipe exists, otherwise display item
			ResourceLocation recipeId = drillHeadVariantHolder.id();
			ManualRecipeRef manualRecipeRef = new ManualRecipeRef(recipeId);
			SpecialManualElement specialManualElement;
			if (checkCraftingRecipeExists(manualRecipeRef)) {
				specialManualElement = new ManualElementCrafting(ManualHelper.getManual(), new ManualRecipeRef[][] { { manualRecipeRef } });
			}
			else if (checkBlueprintRecipeExists(manualRecipeRef)) {
				specialManualElement = new ManualElementBlueprint(ManualHelper.getManual(), new ManualRecipeRef[] { manualRecipeRef });
			}
			else {
				// Item Display
				ItemStack drillHeadVariantItemStack = new ItemStack(IEToolTweaksItems.DRILLHEAD.get());
				drillHeadVariantItemStack.getOrCreateTag().putString("drillhead_variant_id", drillHeadVariantHolder.id().toString());
				specialManualElement = new ManualElementItem(ManualHelper.getManual(), drillHeadVariantItemStack);
			}

			specials.add(new SpecialElementData(drillHeadVariantHolder.id().getPath(), 0, specialManualElement));
			text.append("<&").append(drillHeadVariantHolder.id().getPath()).append(">");

			// Name
			text.append("\n");
			text.append("§l").append(DisplayHelper.getSubItemDisplayName("item.ie_hcq_tool_tweaks.drillhead", drillHeadVariantHolder.id().toLanguageKey()).getString()).append("§r");
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
				drillHeadVariant.veinMiningTag().ifPresentOrElse((veinMiningTag) -> {
					text.append("\n");
					text.append(Component
							.translatable("desc.ie_hcq_tool_tweaks.flavour.drillhead.vein.tag", new Object[] { drillHeadVariant.veinMiningSize(), DisplayHelper.getTagDisplayName(veinMiningTag) })
							.getString());
				}, () -> {
					text.append("\n");
					text.append(Component.translatable("desc.ie_hcq_tool_tweaks.flavour.drillhead.vein", new Object[] { drillHeadVariant.veinMiningSize() }).getString());
				});
			}

			// Repair Material Ingredient not empty
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

		IEToolTweaks.LOGGER.info("Adding railgun rods to IE Manual: {}", RailgunHandler.projectilePropertyMap.size());

		var railgunAmmoHolderSet = RailgunAmmoManager.getHolderSet();
		for (RailgunAmmoHolder railgunAmmoHolder : railgunAmmoHolderSet) {
			String ammoId = railgunAmmoHolder.id().toString();
			RailgunAmmo railgunAmmo = railgunAmmoHolder.ammo();

			// Start new page
			text.append("<np>");

			// Item Display
			ManualElementItem manualElementItem = new ManualElementItem(ManualHelper.getManual(), railgunAmmo.getAmmoIngredient().getItems());
			specials.add(new SpecialElementData(ammoId + "/items", 0, manualElementItem));
			text.append("<&").append(ammoId + "/items").append(">");

			// Charge Duration
			text.append("\n");
			text.append(Component.translatable("manual.ie_hcq_tool_tweaks.railgun_ammo.charge_duration",
					new Object[] { Utils.formatDouble((double) railgunAmmo.firingData.chargeDuration() / (double) 20, "0.###") }).getString());
			// Launch - Speed
			text.append("\n");
			text.append(Component.translatable("manual.ie_hcq_tool_tweaks.railgun_ammo.speed", new Object[] { Utils.formatDouble((double) railgunAmmo.firingData.speed(), "0.###") }).getString());
			// Launch - Deviation
			if (railgunAmmo.firingData.deviation() > 0) {
				text.append("\n");
				text.append(Component.translatable("manual.ie_hcq_tool_tweaks.railgun_ammo.deviation", new Object[] { Utils.formatDouble((double) railgunAmmo.firingData.deviation(), "0.###") })
						.getString());
			}
			// Projectile - Damage
			text.append("\n");
			text.append(
					Component.translatable("manual.ie_hcq_tool_tweaks.railgun_ammo.damage", new Object[] { Utils.formatDouble((double) railgunAmmo.projectileData.damage(), "0.###") }).getString());
			// Projectile - Gravity
			text.append("\n");
			text.append(
					Component.translatable("manual.ie_hcq_tool_tweaks.railgun_ammo.gravity", new Object[] { Utils.formatDouble((double) railgunAmmo.projectileData.gravity(), "0.###") }).getString());
			// Projectile - Ignores most projectile deflections.
			if (railgunAmmo.projectileData.useUpgradedProjectile()) {
				text.append("\n");
				text.append(Component.translatable("manual.ie_hcq_tool_tweaks.railgun_ammo.no_deflection").getString());
			}

			// Projectile - Additional Text
			String extraTextKey = "manual." + railgunAmmoHolder.id().toLanguageKey() + ".extra_text";
			String extraText = Component.translatable(extraTextKey).getString();

			if (!extraText.equals(extraTextKey)) { // Translation Key exists
				text.append("\n");
				text.append(extraText);
			}

		}

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

}
