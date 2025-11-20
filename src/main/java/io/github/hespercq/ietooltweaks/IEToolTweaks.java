package io.github.hespercq.ietooltweaks;

import com.mojang.logging.LogUtils;

import blusunrize.immersiveengineering.common.items.DrillheadItem;
import io.github.hespercq.ietooltweaks.toughRailgun.ToughRailgunProjectiles;
import io.github.hespercq.ietooltweaks.toughRailgun.ToughRailgunShotEntity;
import io.github.hespercq.ietooltweaks.toughRailgun.ToughRailgunShotRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(IEToolTweaks.MODID)
public class IEToolTweaks {

	public static final String MODID = "ie_hcq_tool_tweaks";
	public static final Logger LOGGER = LogUtils.getLogger();

	// Item Register
	// ================================================================================================
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

	// Drillheads
	public static final RegistryObject<Item> DRILLHEAD_SHADOW_STEEL = ITEMS.register("drillhead_ancient_metal",
			() -> new HCQDrillheadItem(HCQDrillheadItem.SHADOW_STEEL));
	public static final RegistryObject<Item> DRILLHEAD_BLACK_STEEL = ITEMS.register("drillhead_ancient_metal",
			() -> new HCQDrillheadItem(HCQDrillheadItem.BLACK_STEEL));

	public static final RegistryObject<Item> DRILLHEAD_ANCIENT = ITEMS.register("drillhead_ancient_metal",
			() -> new HCQDrillheadItem(HCQDrillheadItem.ANCIENT));
	public static final RegistryObject<Item> DRILLHEAD_IGNIUM = ITEMS.register("drillhead_ancient_metal",
			() -> new HCQDrillheadItem(HCQDrillheadItem.IGNIUM));
	public static final RegistryObject<Item> DRILLHEAD_CURSIUM = ITEMS.register("drillhead_ancient_metal",
			() -> new HCQDrillheadItem(HCQDrillheadItem.CURSIUM));

	public static final RegistryObject<Item> DRILLHEAD_MANA_STEEL = ITEMS.register("drillhead_mana_steel",
			() -> new HCQDrillheadItem(HCQDrillheadItem.MANA));
	public static final RegistryObject<Item> DRILLHEAD_TERRA_STEEL = ITEMS.register("drillhead_terra_steel",
			() -> new HCQDrillheadItem(HCQDrillheadItem.TERRA));
	public static final RegistryObject<Item> DRILLHEAD_ELEMENTIUM = ITEMS.register("drillhead_elementium",
			() -> new HCQDrillheadItem(HCQDrillheadItem.ELEMENTIUM));

	public static final RegistryObject<Item> DRILLHEAD_RADIANCE = ITEMS.register("drillhead_elementium",
			() -> new HCQDrillheadItem(HCQDrillheadItem.RADIANCE));

	// Entity Types Register
	// ================================================================================================
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister
			.create(ForgeRegistries.ENTITY_TYPES, MODID);
	// Tough Railgun Shot - Builder
	private static final EntityType.Builder<ToughRailgunShotEntity> TOUGH_RAILGUN_SHOT_ENTIY_BUILDER = EntityType.Builder
			.<ToughRailgunShotEntity>of(ToughRailgunShotEntity::new, MobCategory.MISC)
			.sized(.5F, .5F);
	// Tough Railgun Shot - Registration
	public static final RegistryObject<EntityType<ToughRailgunShotEntity>> TOUGH_RAILGUN_SHOT = ENTITY_TYPES.register(
			"tough_railgun_shot", () -> TOUGH_RAILGUN_SHOT_ENTIY_BUILDER.build(MODID + ":" + "tough_railgun_shot"));

	// modEventBus
	// ================================================================================================

	public IEToolTweaks(FMLJavaModLoadingContext context) {
		IEventBus modEventBus = context.getModEventBus();
		// Setup Listener
		modEventBus.addListener(this::commonSetup);
		modEventBus.addListener(this::registerRenderers);
		// Registers
		ITEMS.register(modEventBus);
		ENTITY_TYPES.register(modEventBus);
	}

	private void commonSetup(final InterModProcessEvent event) {

		LOGGER.info("[" + MODID + "] " + "InterModProcess - Start Railgun Projectile Injection");
		ToughRailgunProjectiles.inject();
		LOGGER.info("[" + MODID + "] " + "InterModProcess - End Railgun Projectile Injection");

	}

	private void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(TOUGH_RAILGUN_SHOT.get(), ToughRailgunShotRenderer::new);
	}

}
