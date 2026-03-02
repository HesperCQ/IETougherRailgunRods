package io.github.hespercq.ietooltweaks.events;

import io.github.hespercq.ietooltweaks.IEToolTweaks;
import io.github.hespercq.ietooltweaks.drillheads.DataDrillHeadItem;
import io.github.hespercq.ietooltweaks.drillheads.DataDrillHeadVariantsDataLoader;
import io.github.hespercq.ietooltweaks.manual.ManualContent;
import io.github.hespercq.ietooltweaks.railgunrods.ToughRailgunShotRenderer;
import io.github.hespercq.ietooltweaks.register.IEToolTweaksEntityTypes;
import io.github.hespercq.ietooltweaks.register.IEToolTweaksItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = IEToolTweaks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModClientEventSubscriber {

	@SubscribeEvent // EntityRenderersEvent.RegisterRenderers
	public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(IEToolTweaksEntityTypes.TOUGH_RAILGUN_SHOT.get(), ToughRailgunShotRenderer::new);
	}

	@SubscribeEvent // BuildCreativeModeTabContentsEvent
	public static void onBuildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
		// Is this the IE main creative tab?
		if (event.getTabKey().location().equals(ResourceLocation.fromNamespaceAndPath("immersiveengineering", "main"))) {
			// Add dynamic DrillHeads
			DataDrillHeadVariantsDataLoader.getIds().forEach(id -> {
				ItemStack stack = new ItemStack(IEToolTweaksItems.DRILLHEAD.get());
				stack.getOrCreateTag().putString("drillhead_variant_id", id);
				event.accept(stack);
			});
		}
	}

	@SubscribeEvent // RegisterColorHandlersEvent.Item
	public static void onRegisterColorHandlersItem(RegisterColorHandlersEvent.Item event) {
		event.register((stack, layer) -> {
			if (stack.getItem() instanceof DataDrillHeadItem drillHead) {
				return drillHead.getItemColor(stack);
			}
			return 0xFFFFFF; // fallback white
		}, IEToolTweaksItems.DRILLHEAD.get());
	}

	@SubscribeEvent
	public static void on(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			ManualContent.setupManualPages();
		});
	}
}
