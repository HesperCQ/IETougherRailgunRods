package io.github.hespercq.ietooltweaks.railgunrods;

import java.util.UUID;
import javax.annotation.Nullable;

import blusunrize.immersiveengineering.api.tool.RailgunHandler.IRailgunProjectile;
import blusunrize.immersiveengineering.common.entities.RailgunShotEntity;
import io.github.hespercq.ietooltweaks.helpers.SafeJsonObject;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public final class DataRailgunProjectile implements IRailgunProjectile {

	public final Ingredient ammo;

	public final boolean isValidForTurret;
	public final int chargeDuration;

	public final float speed;
	public final float deviation;
	public final double rodDamage;
	public final double rodGravity;
	public final boolean ignoresArrowSpecificCoding;
	@Nullable
	public final String rodOnHitCommand;

	// Future Plans
	// - ConsumesAmmo

	private DataRailgunProjectile(Builder builder) {
		this.ammo = builder.ammo;

		this.isValidForTurret = builder.isValidForTurret;
		this.chargeDuration = builder.chargeDuration;

		this.speed = builder.speed;
		this.deviation = builder.deviation;
		this.rodDamage = builder.rodDamage;
		this.rodGravity = builder.rodGravity;
		this.ignoresArrowSpecificCoding = builder.ignoresArrowSpecificCoding;
		this.rodOnHitCommand = builder.rodOnHitCommand;
	}

	@Override
	public boolean isValidForTurret() {
		return isValidForTurret;
	}

	@Override
	public Entity getProjectile(@Nullable Player shooter, ItemStack ammo, Entity defaultProjectile) {
		if (shooter == null) {
			return defaultProjectile; // TODO: Check casting and getting the pos via that
			// ToughRailgunShotEntity(defaultProjectile.level(), defaultProjectile.setOwner, speed, deviation, ammo);
		}

		if (ignoresArrowSpecificCoding) {
			return new ToughRailgunShotEntity(shooter.level(), shooter, speed, deviation, ammo);
		}
		return new RailgunShotEntity(shooter.level(), shooter, speed, deviation, ammo);
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
		// Execute command if present
		if (rodOnHitCommand != null && !rodOnHitCommand.isBlank()) {
			world.getServer().getCommands().performPrefixedCommand(world.getServer().createCommandSourceStack(), rodOnHitCommand);
		}
	}

	@Override
	public double getBreakChance(@Nullable UUID shooter, ItemStack ammo) {
		return 0.25;
	}

	// =========================
	// ======== BUILDER ========
	// =========================

	public static class Builder {
		// ===== Defaults =====

		private Ingredient ammo = Ingredient.EMPTY;

		private boolean isValidForTurret = false;
		private int chargeDuration = 40;

		private float speed = 20;
		private float deviation = 0;

		private double rodDamage = 16.0;
		private double rodGravity = 1.25;
		private boolean ignoresArrowSpecificCoding = false;
		private String rodOnHitCommand = null;

		// ===== Setters =====

		public Builder ammo(Ingredient ammo) {
			this.ammo = ammo;
			return this;
		}

		public Builder isValidForTurret(boolean value) {
			this.isValidForTurret = value;
			return this;
		}

		public Builder chargeDuration(int chargeDuration) {
			this.chargeDuration = chargeDuration;
			return this;
		}

		public Builder speed(float speed) {
			this.speed = speed;
			return this;
		}

		public Builder deviation(float deviation) {
			this.deviation = deviation;
			return this;
		}

		public Builder rodDamage(double value) {
			this.rodDamage = value;
			return this;
		}

		public Builder rodGravity(double value) {
			this.rodGravity = value;
			return this;
		}

		public Builder ignoresArrowSpecificCoding(boolean value) {
			this.ignoresArrowSpecificCoding = value;
			return this;
		}

		public Builder rodOnHitCommand(String command) {
			this.rodOnHitCommand = command;
			return this;
		}

		public DataRailgunProjectile build() {
			return new DataRailgunProjectile(this);
		}

		public static Builder fromSafeJsonObject(SafeJsonObject json) {
			Builder builder = new Builder();
			json.getIngredient("ammo").ifPresent(builder::ammo);
			json.getInt("chargeDuration").ifPresent(builder::chargeDuration);
			json.getFloat("speed").ifPresent(builder::speed);
			json.getFloat("deviation").ifPresent(builder::deviation);
			json.getBoolean("isValidForTurret").ifPresent(builder::isValidForTurret);
			json.getDouble("rodDamage").ifPresent(builder::rodDamage);
			json.getDouble("rodGravity").ifPresent(builder::rodGravity);
			json.getBoolean("ignoresArrowSpecificCoding").ifPresent(builder::ignoresArrowSpecificCoding);
			json.getString("rodOnHitCommand").ifPresent(builder::rodOnHitCommand);
			return builder;
		}
	}
}