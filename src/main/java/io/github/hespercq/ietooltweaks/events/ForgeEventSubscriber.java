package io.github.hespercq.ietooltweaks.events;

import io.github.hespercq.ietooltweaks.IEToolTweaks;
import io.github.hespercq.ietooltweaks.drillheads.DataDrillHeadVariantsDataLoader;
import io.github.hespercq.ietooltweaks.railgunrods.AmmoDataLoader;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IEToolTweaks.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventSubscriber {

	// AddReloadListenerEvent
	@SubscribeEvent
	public static void onAddReloadListener(final AddReloadListenerEvent event) {
		event.addListener(new DataDrillHeadVariantsDataLoader());
		event.addListener(new AmmoDataLoader());
		IEToolTweaks.LOGGER.info("Reload Listeners Registered");
	}
}
