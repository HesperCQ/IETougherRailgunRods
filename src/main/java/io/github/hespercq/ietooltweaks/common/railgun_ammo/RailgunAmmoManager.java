package io.github.hespercq.ietooltweaks.common.railgun_ammo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;

import blusunrize.immersiveengineering.api.tool.RailgunHandler;
import blusunrize.immersiveengineering.api.tool.RailgunHandler.StandardRailgunProjectile;
import io.github.hespercq.ietooltweaks.IEToolTweaks;
import io.github.hespercq.ietooltweaks.common.network.IEToolTweaksNetwork;
import io.github.hespercq.ietooltweaks.common.network.SyncRailgunAmmosPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;

public class RailgunAmmoManager extends SimpleJsonResourceReloadListener {

	// Data for SimpleJsonResourceReloadListener constructor
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final String FOLDER = "railgun_ammo";

	public RailgunAmmoManager() {
		super(GSON, FOLDER);
	}

	public static final Map<ResourceLocation, RailgunAmmo> AMMOS = new HashMap<>();

	public static Set<ResourceLocation> getIdSet() {
		return AMMOS.keySet();
	}

	public static Collection<RailgunAmmo> getAll() {
		return AMMOS.values();
	}

	public static RailgunAmmo get(ResourceLocation ammoId) {
		return AMMOS.get(ammoId);
	}

	public static RailgunAmmoHolder getHolder(ResourceLocation ammoId) {
		return new RailgunAmmoHolder(ammoId, AMMOS.get(ammoId));
	}

	public static Collection<RailgunAmmoHolder> getHolderSet() {
		return AMMOS.entrySet().stream().map(entry -> new RailgunAmmoHolder(entry.getKey(), entry.getValue())).collect(Collectors.toSet());
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager resourceManager, ProfilerFiller profiler) {
		AMMOS.clear();
		jsons.forEach((rl, json) -> {
			ResourceLocation fullResourceLocation = ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), FOLDER + "/" + rl.getPath());
			RailgunAmmo railgunAmmo = RailgunAmmo.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false, (error) -> {
				IEToolTweaks.LOGGER.error("Failed to load railgun ammo '{}':{}", rl, error);
			});

			if (railgunAmmo.getAmmoIngredient().isEmpty()) {
				IEToolTweaks.LOGGER.warn("Skipped Railgun Ammo because Ingredient was empty: {}", rl);
				return;
			}
			// Add Ammos to Map and Railgun Handler
			AMMOS.put(fullResourceLocation, railgunAmmo);
			IEToolTweaks.LOGGER.info("Loaded railgun ammo '{}'", rl);

		});
		updateIERailgunHandler();
	}

	public static void syncDataToPlayers() {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

		if (server != null) {
			SyncRailgunAmmosPacket packet = new SyncRailgunAmmosPacket(RailgunAmmoManager.AMMOS);

			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				IEToolTweaksNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
			}
		}
	}

	public static void updateIERailgunHandler() {
		RailgunHandler.projectilePropertyMap.removeIf(entry -> {
			return entry.getSecond() instanceof IRailgunAmmo || entry.getSecond() instanceof StandardRailgunProjectile;
		});
		AMMOS.forEach((ResourceLocation rl, RailgunAmmo railgunAmmo) -> {
			registerProjectileToIERailgunHandler(rl, railgunAmmo);
		});
	}

	private static void registerProjectileToIERailgunHandler(ResourceLocation rl, IRailgunAmmo railgunAmmoData) {
		Ingredient newAmmoIngredient = railgunAmmoData.getAmmoIngredient();
		RailgunHandler.registerProjectile(() -> newAmmoIngredient, railgunAmmoData);
		IEToolTweaks.LOGGER.info("Registered railgun projectile to IE Railgun Handler: '{}'", rl);
	}
}