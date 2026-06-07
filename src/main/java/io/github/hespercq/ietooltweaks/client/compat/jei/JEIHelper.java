package io.github.hespercq.ietooltweaks.client.compat.jei;

import blusunrize.immersiveengineering.api.Lib;
import io.github.hespercq.ietooltweaks.common.drillheads.VariantDrillHeadItem;
import io.github.hespercq.ietooltweaks.register.IEToolTweaksItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class JEIHelper implements IModPlugin {
	private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Lib.MODID, "main");

	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration subtypeRegistry) {
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, IEToolTweaksItems.DRILLHEAD.get(), (stack, $) -> VariantDrillHeadItem.getVariantId(stack).toString());
	}

}
