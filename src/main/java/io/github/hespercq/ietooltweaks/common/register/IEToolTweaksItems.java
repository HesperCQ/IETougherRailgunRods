package io.github.hespercq.ietooltweaks.common.register;

import io.github.hespercq.ietooltweaks.IEToolTweaks;
import io.github.hespercq.ietooltweaks.common.drillheads.VariantDrillHeadItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.item.Item;

public class IEToolTweaksItems {
	// Item Register
	// ================================================================================================
	public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, IEToolTweaks.MODID);

	// HCQDrillHead (Data Driven)
	public static final RegistryObject<Item> DRILLHEAD = REGISTER.register("drillhead", () -> new VariantDrillHeadItem());

}
