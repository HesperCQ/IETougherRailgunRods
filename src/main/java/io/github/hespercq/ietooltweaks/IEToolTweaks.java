package io.github.hespercq.ietooltweaks;

import com.mojang.logging.LogUtils;

import io.github.hespercq.ietooltweaks.drillheads.DataDrillHeadItem;
import io.github.hespercq.ietooltweaks.railgunrods.ToughRailgunShotEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(IEToolTweaks.MODID)
@Mod.EventBusSubscriber(modid = IEToolTweaks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IEToolTweaks {

	public static final String MODID = "ie_hcq_tool_tweaks";
	public static final Logger LOGGER = LogUtils.getLogger();

	// Item Register
	// ================================================================================================
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

	// HCQDrillHead (Data Driven)
	public static final RegistryObject<Item> DRILLHEAD = ITEMS.register("drillhead", () -> new DataDrillHeadItem());

	// Entity Types Register
	// ================================================================================================
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);
	// Tough Railgun Shot - Builder
	private static final EntityType.Builder<ToughRailgunShotEntity> TOUGH_RAILGUN_SHOT_ENTIY_BUILDER = EntityType.Builder.<ToughRailgunShotEntity>of(ToughRailgunShotEntity::new, MobCategory.MISC)
			.sized(.5F, .5F);
	// Tough Railgun Shot - Registration
	public static final RegistryObject<EntityType<ToughRailgunShotEntity>> TOUGH_RAILGUN_SHOT = ENTITY_TYPES.register("tough_railgun_shot",
			() -> TOUGH_RAILGUN_SHOT_ENTIY_BUILDER.build(MODID + ":" + "tough_railgun_shot"));
				
	// Mod bus
	// ================================================================================================
	public IEToolTweaks(FMLJavaModLoadingContext context) {
		IEventBus modEventBus = context.getModEventBus();
		// Registers
		ITEMS.register(modEventBus);
		ENTITY_TYPES.register(modEventBus);
	}
}
