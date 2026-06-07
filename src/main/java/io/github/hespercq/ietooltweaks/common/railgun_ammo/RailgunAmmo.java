package io.github.hespercq.ietooltweaks.common.railgun_ammo;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import blusunrize.immersiveengineering.api.tool.RailgunHandler.RailgunRenderColors;
import blusunrize.immersiveengineering.api.tool.RailgunHandler.StandardRailgunProjectile;
import blusunrize.immersiveengineering.common.entities.RailgunShotEntity;
import io.github.hespercq.ietooltweaks.common.util.IEToolTweaksCodecs;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public final class RailgunAmmo extends StandardRailgunProjectile implements IRailgunAmmo {

	public final Ingredient ammo;
	public final RailgunAmmoFiringData firingData;
	public final RailgunAmmoProjectileData projectileData;

	public RailgunAmmo(

			Ingredient ammo, RailgunAmmoFiringData firingData, RailgunAmmoProjectileData projectileData) {

		super(projectileData.damage(), projectileData.gravity());

		this.ammo = ammo;
		this.firingData = firingData;
		this.projectileData = projectileData;
	}

	public static final Codec<RailgunAmmo> CODEC = RecordCodecBuilder.create(instance -> instance.group(

			IEToolTweaksCodecs.INGREDIENT.fieldOf("ammo").forGetter(d -> d.ammo),

			RailgunAmmoFiringData.CODEC.optionalFieldOf("firing", RailgunAmmoFiringData.DEFAULT).forGetter(d -> d.firingData),

			RailgunAmmoProjectileData.CODEC.optionalFieldOf("projectile", RailgunAmmoProjectileData.DEFAULT).forGetter(d -> d.projectileData)

	).apply(instance, (ammo, firing, projectile) -> {
		RailgunAmmo railgunAmmo = new RailgunAmmo(ammo, firing, projectile);
		projectile.colorData()
				.ifPresent(data -> railgunAmmo.setColorMap(new RailgunRenderColors(data.stream().map(ring -> ring.stream().mapToInt(Integer::intValue).toArray()).toArray(int[][]::new))));
		return railgunAmmo;
	}));

	@Override
	public Ingredient getAmmoIngredient() {
		return this.ammo;
	}

	@Override
	public boolean isValidForTurret() {
		return firingData.isValidForTurret();
	}

	@Override
	public Entity getProjectile(@Nullable Player shooter, ItemStack ammo, Entity defaultProjectile) {
		if (shooter == null) {
			return defaultProjectile; // TODO: Check casting and getting the pos / speed via that
		}

		if (projectileData.useUpgradedProjectile()) {
			return new HCQRailgunShotEntity(shooter.level(), shooter, firingData.speed(), firingData.deviation(), ammo);
		}
		return new RailgunShotEntity(shooter.level(), shooter, firingData.speed(), firingData.deviation(), ammo);
	}

	@Override
	public double getGravity() {
		return projectileData.gravity();
	}

	@Override
	public double getDamage(Level world, Entity target, @Nullable UUID shooter, Entity projectile) {
		return projectileData.damage();
	}

	@Override
	public DamageSource getDamageSource(Level world, Entity target, @Nullable UUID shooter, Entity projectile) {
		return null;
	}

	@Override
	public void onHitTarget(Level world, HitResult target, @Nullable UUID shooter, Entity projectile) {
		if (world.isClientSide || world.getServer() == null)
			return;
		var commandHooks = projectileData.commandHooks();
		if (target instanceof EntityHitResult entityHit) {
			// TODO: Check Permission
			CommandSourceStack source = entityHit.getEntity().createCommandSourceStack().withPermission(4).withSuppressedOutput();
			for (String command : commandHooks.onHitEntity()) {
				world.getServer().getCommands().performPrefixedCommand(source, command);
			}
		}

		else if (target instanceof BlockHitResult blockHit) {
			// TODO: Check Permission
			CommandSourceStack source = world.getServer().createCommandSourceStack().withPosition(blockHit.getBlockPos().getCenter()).withPermission(4).withSuppressedOutput();

			List<String> directionalCommands = switch (blockHit.getDirection()) {
			case NORTH -> commandHooks.onHitBlockNorth();
			case EAST -> commandHooks.onHitBlockEast();
			case SOUTH -> commandHooks.onHitBlockSouth();
			case WEST -> commandHooks.onHitBlockWest();
			case UP -> commandHooks.onHitBlockUp();
			case DOWN -> commandHooks.onHitBlockDown();
			};

			var commandManager = world.getServer().getCommands();

			for (String command : commandHooks.onHitBlock()) {
				commandManager.performPrefixedCommand(source, command);
			}

			for (String command : directionalCommands) {
				commandManager.performPrefixedCommand(source, command);
			}
		}
	}

	@Override
	public double getBreakChance(@Nullable UUID shooter, ItemStack ammo) {
		return projectileData.breakChance();
	}

	@Override
	public int getChargeDuration() {
		return firingData.chargeDuration();
	}
}
