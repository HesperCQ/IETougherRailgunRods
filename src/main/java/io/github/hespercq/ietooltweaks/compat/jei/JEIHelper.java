package io.github.hespercq.ietooltweaks.compat.jei;

import blusunrize.immersiveengineering.api.Lib;
import io.github.hespercq.ietooltweaks.IEToolTweaks;
import io.github.hespercq.ietooltweaks.drillheads.DataDrillHeadItem;
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
		subtypeRegistry.registerSubtypeInterpreter(
			VanillaTypes.ITEM_STACK,
			IEToolTweaks.DRILLHEAD.get(),
			(stack, $) -> DataDrillHeadItem.getPermData(stack).name());
	}

}
