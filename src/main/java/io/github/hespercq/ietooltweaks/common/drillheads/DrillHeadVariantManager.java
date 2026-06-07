package io.github.hespercq.ietooltweaks.common.drillheads;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import io.github.hespercq.ietooltweaks.IEToolTweaks;
import io.github.hespercq.ietooltweaks.common.network.IEToolTweaksNetwork;
import io.github.hespercq.ietooltweaks.common.network.SyncDrillHeadVariantsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;

public class DrillHeadVariantManager extends SimpleJsonResourceReloadListener {
	// Data for SimpleJsonResourceReloadListener constructor
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	public static final String FOLDER = "drillheads";

	public static final Map<ResourceLocation, DrillHeadVariant> VARIANTS = new HashMap<>();

	public static final DrillHeadVariantHolder DEBUG_HOLDER = new DrillHeadVariantHolder(ResourceLocation.fromNamespaceAndPath(IEToolTweaks.MODID, FOLDER + "/" + "debug"),

			new DrillHeadVariant(

					1, 1, Tiers.WOOD, 1,

					Optional.empty(), 0,

					1, 1,

					Ingredient.EMPTY, 0xFFFFFF, ImmersiveEngineering.rl("item/drill_diesel"), Optional.empty()));

	public DrillHeadVariantManager() {
		super(GSON, FOLDER);
	}

	public static Set<ResourceLocation> getIdSet() {
		return VARIANTS.keySet();
	}

	public static Collection<DrillHeadVariant> getAll() {
		return VARIANTS.values();
	}

	public static DrillHeadVariant get(ResourceLocation variantId) {
		return VARIANTS.getOrDefault(variantId, DEBUG_HOLDER.variant());
	}

	public static DrillHeadVariantHolder getHolder(ResourceLocation variantId) {
		var variant = VARIANTS.get(variantId);
		if (variant == null) {
			return DEBUG_HOLDER;
		}
		return new DrillHeadVariantHolder(variantId, variant);
	}

	public static Collection<DrillHeadVariantHolder> getHolderSet() {
		return VARIANTS.entrySet().stream().map(entry -> new DrillHeadVariantHolder(entry.getKey(), entry.getValue())).collect(Collectors.toSet());
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager resourceManager, ProfilerFiller profiler) {
		VARIANTS.clear();
		jsons.forEach((rl, json) -> {
			ResourceLocation fullResourceLocation = ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), FOLDER + "/" + rl.getPath());
			DrillHeadVariant dataDrillHeadVariant = DrillHeadVariant.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false, (error) -> {
				IEToolTweaks.LOGGER.error("Failed to load drill head '{}':{}", rl, error);
			});
			VARIANTS.put(fullResourceLocation, dataDrillHeadVariant);
			IEToolTweaks.LOGGER.info("Loaded drill head '{}'", rl);
		});

	}

	public static void syncDataToPlayers() {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

		if (server != null) {
			SyncDrillHeadVariantsPacket packet = new SyncDrillHeadVariantsPacket(DrillHeadVariantManager.VARIANTS);

			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				IEToolTweaksNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
			}
		}
	}
}
