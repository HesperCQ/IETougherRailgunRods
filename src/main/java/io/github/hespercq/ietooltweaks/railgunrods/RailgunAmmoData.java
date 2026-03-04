package io.github.hespercq.ietooltweaks.railgunrods;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;

import blusunrize.immersiveengineering.api.tool.RailgunHandler.RailgunRenderColors;
import blusunrize.immersiveengineering.api.tool.RailgunHandler.StandardRailgunProjectile;
import blusunrize.immersiveengineering.common.entities.RailgunShotEntity;
import io.github.hespercq.ietooltweaks.IEToolTweaks;
import io.github.hespercq.ietooltweaks.helpers.SafeJsonObject;
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

public final class RailgunAmmoData extends StandardRailgunProjectile implements IRailgunAmmoData {

	public final Ingredient ammo;

	public final boolean isValidForTurret;
	public final int chargeDuration;

	public final float speed;
	public final float deviation;
	public final double rodDamage;
	public final double rodGravity;
	public final boolean ignoresArrowSpecificCoding;
	public final Optional<String[]> onEntityHitCommands;
	public final Optional<String[]> onBlockHitCommands;

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
		this.onEntityHitCommands = builder.onEntityHitCommands;
		this.onBlockHitCommands = builder.onBlockHitCommands;
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
		IEToolTweaks.LOGGER.info("ZZZ onHitTarget");
		if (world.isClientSide || world.getServer() == null)
			return;

		IEToolTweaks.LOGGER.info("ZZZ not Client");

		Optional<String[]> commands;
		CommandSourceStack source;

		if (target instanceof EntityHitResult entityHit) {
			source = entityHit.getEntity().createCommandSourceStack();
			commands = onEntityHitCommands;
		}
		else if (target instanceof BlockHitResult blockHit) {
			source = world.getServer().createCommandSourceStack().withPosition(blockHit.getLocation());
			commands = onBlockHitCommands;
		}
		else {
			return;
		}

		source = source.withPermission(4).withSuppressedOutput();
		for (String command : commands.orElse(new String[0])) {
			IEToolTweaks.LOGGER.info("ZZZ: " + command);
			world.getServer().getCommands().performPrefixedCommand(source, command);
		}
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

		private Optional<String[]> onEntityHitCommands = Optional.empty();
		private Optional<String[]> onBlockHitCommands = Optional.empty();
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

		public Builder onEntityHitCommands(List<String> commands) {
			String[] commandsArray = commands.toArray(new String[commands.size()]);
			IEToolTweaks.LOGGER.info("ZZZ" + commands);
			this.onEntityHitCommands = Optional.of(commandsArray);
			return this;
		}

		public Builder onBlockHitCommands(List<String> commands) {
			String[] commandsArray = commands.toArray(new String[commands.size()]);
			IEToolTweaks.LOGGER.info("ZZY" + "commands");
			this.onBlockHitCommands = Optional.of(commandsArray);
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
			json.getFloat("speed").ifPresent(builder::speed);
			json.getFloat("deviation").ifPresent(builder::deviation);
			json.getBoolean("isValidForTurret").ifPresent(builder::isValidForTurret);
			json.getDouble("rodDamage").ifPresent(builder::rodDamage);
			json.getDouble("rodGravity").ifPresent(builder::rodGravity);
			json.getBoolean("ignoresArrowSpecificCoding").ifPresent(builder::ignoresArrowSpecificCoding);
			json.getStringArray("onEntityHitCommands").ifPresent(builder::onEntityHitCommands);
			json.getStringArray("onBlockHitCommands").ifPresent(builder::onBlockHitCommands);
			json.getRailgunRenderColors("railgunRenderColors").ifPresent(builder::railgunRenderColors);
			return builder;
		}
	}
}