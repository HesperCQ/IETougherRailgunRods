package io.github.hespercq.ietooltweaks.register;

import io.github.hespercq.ietooltweaks.IEToolTweaks;
import io.github.hespercq.ietooltweaks.common.railgun_ammo.HCQRailgunShotEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class IEToolTweaksEntityTypes {
	public static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, IEToolTweaks.MODID);
	// Tough Railgun Shot
	public static final RegistryObject<EntityType<HCQRailgunShotEntity>> TOUGH_RAILGUN_SHOT = REGISTER.register("tough_railgun_shot",
			() -> EntityType.Builder.<HCQRailgunShotEntity>of(HCQRailgunShotEntity::new, MobCategory.MISC).sized(.5F, .5F).build(IEToolTweaks.MODID + ":" + "tough_railgun_shot"));
}
