package io.github.hespercq.ietooltweaks.railgunrods;

import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.Lib.DamageTypes;
import blusunrize.immersiveengineering.api.tool.RailgunHandler;
import blusunrize.immersiveengineering.api.tool.RailgunHandler.IRailgunProjectile;
import blusunrize.immersiveengineering.api.tool.RailgunHandler.RailgunRenderColors;
import blusunrize.immersiveengineering.api.tool.RailgunHandler.StandardRailgunProjectile;
import blusunrize.immersiveengineering.common.register.IEItems;
import blusunrize.immersiveengineering.common.register.IEPotions;
import blusunrize.immersiveengineering.mixin.accessors.DamageSourcesAccess;
import io.github.hespercq.ietooltweaks.IEToolTweaks;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.Tags;

import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;

public class ToughRailgunProjectiles {

	public static void inject() {
		// Iron
		register(Ingredient.of(IETags.ironRod), new ToughRailgunProjectile(16, 1.25)
				.setColorMap(new RailgunRenderColors(0xd8d8d8, 0xd8d8d8, 0xd8d8d8, 0xa8a8a8, 0x686868, 0x686868)));

		// Aluminum
		register(Ingredient.of(IETags.aluminumRod), new ToughRailgunProjectile(10, 1.05)
				.setColorMap(new RailgunRenderColors(0xd8d8d8, 0xd8d8d8, 0xd8d8d8, 0xa8a8a8, 0x686868, 0x686868)));

		// Steel
		register(Ingredient.of(IETags.steelRod), new ToughRailgunProjectile(24, 1.25)
				.setColorMap(new RailgunRenderColors(0xd8d8d8, 0xd8d8d8, 0xd8d8d8, 0xa8a8a8, 0x686868, 0x686868)));

		// Graphite
		register(Ingredient.of(IEItems.Misc.GRAPHITE_ELECTRODE), new ToughRailgunProjectile(30, 0.9)
				.setColorMap(new RailgunRenderColors(0x242424, 0x242424, 0x242424, 0x171717, 0x171717, 0x0a0a0a)));

		// Blaze Rod
		register(Ingredient.of(Tags.Items.RODS_BLAZE), new ToughRailgunProjectile(10, 1.05) {
			@Override
			public void onHitTarget(Level world, HitResult target, @Nullable UUID shooter, Entity projectile) {
				if (target instanceof EntityHitResult)
					((EntityHitResult) target).getEntity().setSecondsOnFire(5);
			}

			@Override
			public double getBreakChance(@Nullable UUID shooter, ItemStack ammo) {
				return 1;
			}
		}.setColorMap(new RailgunRenderColors(0xfff32d, 0xffc100, 0xb36b19, 0xbf5a00, 0xbf5a00, 0x953300)));

		// End Rod
		register(Ingredient.of(Items.END_ROD), new ToughRailgunProjectile(10, 1.05) {
			@Override
			public double getDamage(Level world, Entity target, @Nullable UUID shooter, Entity projectile) {
				double d = super.getDamage(world, target, shooter, projectile);
				if (target instanceof EnderMan)
					d *= 2;
				return d;
			}

			@Override
			public DamageSource getDamageSource(Level world, Entity target, @Nullable UUID shooter, Entity projectile) {
				if (target instanceof EnderMan enderMan) {
					enderMan.addEffect(new MobEffectInstance(IEPotions.STUNNED.get(), 200));
					final DamageSourcesAccess sources = (DamageSourcesAccess) world.damageSources();
					Player p;
					if (shooter != null && (p = world.getPlayerByUUID(shooter)) != null)
						return sources.invokeSource(DamageTypes.RAILGUN.turretType(), p, null);
					return sources.invokeSource(DamageTypes.RAILGUN.playerType(), null, null);
				}
				return null;
			}
		}.setColorMap(new RailgunRenderColors(0xf6e2cd, 0xfff6e6, 0xffffff, 0xfff6f6, 0xf6e2cd, 0x736565)));
	}

	// HELPER
	// =========================================================================================================
	protected static void register(Ingredient newAmmo, IRailgunProjectile railgunProjectile) {
		log("Registering Railgun Projectile: " + newAmmo.toJson().toString() + " - Start");

		for (int i = 0; i < RailgunHandler.projectilePropertyMap.size(); i++) {
			Pair<Supplier<Ingredient>, IRailgunProjectile> pair = RailgunHandler.projectilePropertyMap.get(i);
			Ingredient existingAmmo = pair.getFirst().get();
			if (checkSimilarIngredient(existingAmmo, newAmmo)) {
				log("Registering Railgun Projectile: " + newAmmo.toJson().toString() + " - Overwriting Existing Railgun Projectile");
				RailgunHandler.projectilePropertyMap.set(i, Pair.of(() -> newAmmo, railgunProjectile));
				log("Registering Railgun Projectile: " + newAmmo.toJson().toString() + " - Done");
			}
		}
		RailgunHandler.registerProjectile(() -> newAmmo, railgunProjectile);
	}

	protected static boolean checkSimilarIngredient(Ingredient ing1, Ingredient ing2) {
		boolean stringEquality = ing1.toJson().toString().equals(ing2.toJson().toString());
		return stringEquality;
	}

	private static void log(String sMessage) {
		IEToolTweaks.LOGGER.info("[" + IEToolTweaks.MODID + "] " + sMessage);
	}

	// Projectile Class
	// =========================================================================================================
	public static class ToughRailgunProjectile extends StandardRailgunProjectile {
		public ToughRailgunProjectile(double damage, double gravity) {
			super(damage, gravity);
		}

		@Override
		public Entity getProjectile(@Nullable Player shooter, ItemStack ammo, Entity defaultProjectile) {
			return new ToughRailgunShotEntity(shooter.level(), shooter, 20.0F, 0.0F, ammo);
		}
	}
}
