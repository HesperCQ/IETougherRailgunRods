package io.github.hespercq.ietooltweaks.railgunrods;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;

import blusunrize.immersiveengineering.api.tool.RailgunHandler.RailgunRenderColors;
import blusunrize.immersiveengineering.api.tool.RailgunHandler.StandardRailgunProjectile;
import blusunrize.immersiveengineering.common.entities.RailgunShotEntity;
import io.github.hespercq.ietooltweaks.helpers.SafeJsonObject;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public final class RailgunAmmoData extends StandardRailgunProjectile implements IRailgunAmmoData {

	private final Ingredient ammo;

	private final boolean isValidForTurret;
	private final int chargeDuration;

	private final float speed;
	private final float deviation;
	private final double rodDamage;
	private final double rodGravity;
	private final boolean ignoresArrowSpecificCoding;
	private final Optional<String> rodOnHitCommand;

	// Future Plans
	// - ConsumesAmmo
	// - Shoots Entity
	// Copy delta movement from base projectile
	// Color
	// Book

	private RailgunAmmoData(Builder builder) {
		super(builder.rodDamage, builder.rodGravity);
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
	public Ingredient getAmmoIngredient() {
		return this.ammo;
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
		rodOnHitCommand.ifPresent((command) -> {
			world.getServer().getCommands().performPrefixedCommand(world.getServer().createCommandSourceStack(), command);
		});
	}

	@Override
	public double getBreakChance(@Nullable UUID shooter, ItemStack ammo) {
		return 0.25;
	}

	@Override
	public int getChargeDuration() {
		return this.chargeDuration;
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

		private Optional<String> rodOnHitCommand = Optional.empty();
		private Optional<RailgunRenderColors> railgunRenderColors = Optional.empty();

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
			if (command.isBlank()) {
				return this;
			}
			this.rodOnHitCommand = Optional.of(command);
			return this;
		}

		public Builder railgunRenderColors(RailgunRenderColors railgunRenderColors) {
			this.railgunRenderColors = Optional.of(railgunRenderColors);
			return this;
		}

		public RailgunAmmoData build() {
			RailgunAmmoData railgunAmmoData = new RailgunAmmoData(this);
			this.railgunRenderColors.ifPresent(railgunAmmoData::setColorMap);
			return railgunAmmoData;
		}

		public static Builder fromSafeJsonObject(SafeJsonObject json) {
			Builder builder = new Builder();
			json.getIngredient("ammo").ifPresent(builder::ammo);
			json.getInt("chargeDuration").ifPresent(builder::chargeDuration);
			json.getFloat("miningSpeed").ifPresent(builder::speed);
			json.getFloat("deviation").ifPresent(builder::deviation);
			json.getBoolean("isValidForTurret").ifPresent(builder::isValidForTurret);
			json.getDouble("rodDamage").ifPresent(builder::rodDamage);
			json.getDouble("rodGravity").ifPresent(builder::rodGravity);
			json.getBoolean("ignoresArrowSpecificCoding").ifPresent(builder::ignoresArrowSpecificCoding);

			json.getString("rodOnHitCommand").ifPresent(builder::rodOnHitCommand);
			json.getRailgunRenderColors("railgunRenderColors").ifPresent(builder::railgunRenderColors);
			return builder;
		}
	}
}