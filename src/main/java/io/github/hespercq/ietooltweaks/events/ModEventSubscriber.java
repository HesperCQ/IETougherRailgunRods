package io.github.hespercq.ietooltweaks.events;

import io.github.hespercq.ietooltweaks.IEToolTweaks;
import io.github.hespercq.ietooltweaks.drillheads.DataDrillHeadItem;
import io.github.hespercq.ietooltweaks.drillheads.DataDrillHeadPermsDataLoader;
import io.github.hespercq.ietooltweaks.railgunrods.ToughRailgunProjectiles;
import io.github.hespercq.ietooltweaks.railgunrods.ToughRailgunShotRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

@Mod.EventBusSubscriber(modid = IEToolTweaks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventSubscriber {

	// InterModProcessEvent
	@SubscribeEvent
	public static void onInterModProcessEvent(final InterModProcessEvent event) {
		IEToolTweaks.LOGGER.info("[" + IEToolTweaks.MODID + "] " + "InterModProcess - Start Railgun Projectile Injection");
		ToughRailgunProjectiles.inject();
		IEToolTweaks.LOGGER.info("[" + IEToolTweaks.MODID + "] " + "InterModProcess - End Railgun Projectile Injection");

	}

	// EntityRenderersEvent.RegisterRenderers
	@SubscribeEvent
	public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(IEToolTweaks.TOUGH_RAILGUN_SHOT.get(), ToughRailgunShotRenderer::new);
	}

	// BuildCreativeModeTabContentsEvent
	@SubscribeEvent
	public static void onBuildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
		// Is this the IE main creative tab?
		if (event.getTabKey().location().equals(ResourceLocation.fromNamespaceAndPath("immersiveengineering", "main"))) {
			// Add dynamic Drillheads
			DataDrillHeadPermsDataLoader.getIds().forEach(id -> {
				ItemStack stack = new ItemStack(IEToolTweaks.DRILLHEAD.get());
				stack.getOrCreateTag().putString("drillhead_perm_data", id);
				event.accept(stack);
			});
		}
	}

	// RegisterColorHandlersEvent.Item
	@SubscribeEvent
	public static void onRegisterColorHandlersItem(RegisterColorHandlersEvent.Item event) {
		event.register((stack, layer) -> {
			if (stack.getItem() instanceof DataDrillHeadItem drillHead) {
				return drillHead.getItemColor(stack); // returns int 0xRRGGBB
			}
			return 0xFFFFFF; // fallback white
		}, IEToolTweaks.DRILLHEAD.get() // register for your drillhead item
		);
	}
}
