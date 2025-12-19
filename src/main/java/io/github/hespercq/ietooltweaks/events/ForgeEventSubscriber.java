package io.github.hespercq.ietooltweaks.events;

import io.github.hespercq.ietooltweaks.IEToolTweaks;
import io.github.hespercq.ietooltweaks.drillheads.DataDrillHeadPermsDataLoader;
import io.github.hespercq.ietooltweaks.railgunrods.DataRailgunProjectilesDataLoader;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IEToolTweaks.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventSubscriber {

	// AddReloadListenerEvent
	@SubscribeEvent
	public static void onAddReloadListener(final AddReloadListenerEvent event) {
		event.addListener(new DataDrillHeadPermsDataLoader());
		event.addListener(new DataRailgunProjectilesDataLoader());
		IEToolTweaks.LOGGER.info("Reload Listeners Registered");
	}
}
