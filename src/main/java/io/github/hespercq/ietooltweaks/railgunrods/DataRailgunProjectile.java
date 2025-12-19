package io.github.hespercq.ietooltweaks.railgunrods;

import java.util.UUID;

import javax.annotation.Nullable;

import blusunrize.immersiveengineering.api.tool.RailgunHandler.IRailgunProjectile;
import blusunrize.immersiveengineering.common.entities.RailgunShotEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class DataRailgunProjectile implements IRailgunProjectile {
	public final Ingredient ammo;
	public final int chargeDuration;
	public final float velocity;
	public final float accuracy;
	public final boolean isValidForTurret;

	public final double rodGravity;
	public final double rodDamage;
	public final boolean rodIsAbstractArrow;
	public final String rodOnHitCommand;

	public DataRailgunProjectile(Ingredient ammo, int chargeDuration, float velocity, float accuracy, boolean isValidForTurret, double rodDamage, double rodGravity, boolean rodIsAbstractArrow, String rodOnHitCommand) {
		this.ammo = ammo;
		this.chargeDuration = chargeDuration;
		this.velocity = velocity;
		this.accuracy = accuracy;
		this.isValidForTurret = isValidForTurret;

		this.rodIsAbstractArrow = rodIsAbstractArrow;
		this.rodDamage = rodDamage;
		this.rodGravity = rodGravity;
		this.rodOnHitCommand = rodOnHitCommand;
	}

	@Override
	public boolean isValidForTurret() {
		return false;
	}

	@Override
	public Entity getProjectile(@Nullable Player shooter, ItemStack ammo, Entity defaultProjectile) {
		if(rodIsAbstractArrow) {
			return new RailgunShotEntity(shooter.level(), shooter, velocity, accuracy, ammo);
		}
		return new ToughRailgunShotEntity(shooter.level(), shooter, velocity, accuracy, ammo);
	}

	@Override
	public double getGravity() {
		return rodGravity;
	}

	@Override
	public double getDamage(Level world, Entity target, @Nullable UUID shooter, Entity projectile) {
		return rodDamage;
	}

	@Override
	public DamageSource getDamageSource(Level world, Entity target, @Nullable UUID shooter, Entity projectile) {
		return null;
	}

	@Override
	public void onHitTarget(Level world, HitResult target, @Nullable UUID shooter, Entity projectile) {
	}

	@Override
	public double getBreakChance(@Nullable UUID shooter, ItemStack ammo) {
		return 0.25;
	}

}
