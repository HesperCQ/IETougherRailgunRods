package com.github.hespercq.ietougherrailgunrods;

import com.mojang.logging.LogUtils;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
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
@Mod(IETougherRailgunRods.MODID)
public class IETougherRailgunRods {

	
	public static final String MODID = "ietougherrailgunrods";
	public static final Logger LOGGER = LogUtils.getLogger();

	
	// Entity Types Register ================================================================================================
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);
	// Tough Railgun Shot - Builder
	private static final EntityType.Builder<ToughRailgunShotEntity> TOUGH_RAILGUN_SHOT_ENTIY_BUILDER =
    		EntityType.Builder.<ToughRailgunShotEntity>of(ToughRailgunShotEntity::new, MobCategory.MISC)
			.sized(.5F, .5F);
	// Tough Railgun Shot - Registration
	public static final RegistryObject<EntityType<ToughRailgunShotEntity>> TOUGH_RAILGUN_SHOT = ENTITY_TYPES.register(
			"tough_railgun_shot", () -> TOUGH_RAILGUN_SHOT_ENTIY_BUILDER.build(MODID+":"+"tough_railgun_shot"));

	
	

	// modEventBus ================================================================================================

	public IETougherRailgunRods(FMLJavaModLoadingContext context) {
		IEventBus modEventBus = context.getModEventBus();
		// Setup Listener
		modEventBus.addListener(this::commonSetup);
		modEventBus.addListener(this::registerRenderers);
		// Registers
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
