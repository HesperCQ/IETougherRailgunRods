package io.github.hespercq.ietooltweaks.common;

import io.github.hespercq.ietooltweaks.IEToolTweaks;
import io.github.hespercq.ietooltweaks.common.drillheads.DrillHeadVariantManager;
import io.github.hespercq.ietooltweaks.common.network.IEToolTweaksNetwork;
import io.github.hespercq.ietooltweaks.common.network.SyncDrillHeadVariantsPacket;
import io.github.hespercq.ietooltweaks.common.network.SyncRailgunAmmosPacket;
import io.github.hespercq.ietooltweaks.common.railgun_ammo.RailgunAmmoManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = IEToolTweaks.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventSubscriber {

	// AddReloadListenerEvent
	@SubscribeEvent
	public static void onAddReloadListener(final AddReloadListenerEvent event) {
		event.addListener(new DrillHeadVariantManager());
		event.addListener(new RailgunAmmoManager());
	}

	@SubscribeEvent
	public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			IEToolTweaksNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SyncDrillHeadVariantsPacket(DrillHeadVariantManager.VARIANTS));
			IEToolTweaksNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SyncRailgunAmmosPacket(RailgunAmmoManager.AMMOS));
		}
	}
}
