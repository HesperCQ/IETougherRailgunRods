package io.github.hespercq.ietooltweaks;

import io.github.hespercq.ietooltweaks.register.IEToolTweaksEntityTypes;
import io.github.hespercq.ietooltweaks.register.IEToolTweaksItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(IEToolTweaks.MODID)
public class IEToolTweaks {
	public static final String MODID = "ie_hcq_tool_tweaks";
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public IEToolTweaks(FMLJavaModLoadingContext modContext) {
		final IEventBus modEventBus = modContext.getModEventBus();
		// TODO: Add Configs
		// modContext.registerConfig(ModConfig.Type.SERVER, IPServerConfig.ALL);
		// modContext.registerConfig(ModConfig.Type.CLIENT, IPClientConfig.ALL);

		// Registers
		IEToolTweaksItems.REGISTER.register(modEventBus);
		IEToolTweaksEntityTypes.REGISTER.register(modEventBus);
	}
}
