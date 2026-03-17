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
import net.minecraft.core.Direction;
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
	public final double rodBreakChance;
	public final boolean ignoresArrowSpecificCoding;
	public final Optional<String[]> onHitEntityCommands;
	public final Optional<String[]> onHitBlockCommands;
	public final Optional<String[]> onHitBlockCommandsNorth;
	public final Optional<String[]> onHitBlockCommandsEast;
	public final Optional<String[]> onHitBlockCommandsSouth;
	public final Optional<String[]> onHitBlockCommandsWest;
	public final Optional<String[]> onHitBlockCommandsUp;
	public final Optional<String[]> onHitBlockCommandsDown;

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
		this.rodBreakChance = builder.rodBreakChance;
		this.ignoresArrowSpecificCoding = builder.ignoresArrowSpecificCoding;

		this.onHitEntityCommands = builder.onHitEntityCommands;
		this.onHitBlockCommands = builder.onHitBlockCommands;
		this.onHitBlockCommandsNorth = builder.onHitBlockCommandsNorth;
		this.onHitBlockCommandsEast = builder.onHitBlockCommandsEast;
		this.onHitBlockCommandsSouth = builder.onHitBlockCommandsSouth;
		this.onHitBlockCommandsWest = builder.onHitBlockCommandsWest;
		this.onHitBlockCommandsUp = builder.onHitBlockCommandsUp;
		this.onHitBlockCommandsDown = builder.onHitBlockCommandsDown;
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

		String[] commands;
		CommandSourceStack source;

		if (target instanceof EntityHitResult entityHit) {
			source = entityHit.getEntity().createCommandSourceStack();
			commands = this.onHitEntityCommands.orElse(new String[0]);
		}
		else if (target instanceof BlockHitResult blockHit) {
			source = world.getServer().createCommandSourceStack().withPosition(blockHit.getBlockPos().getCenter());

			// Default block commands
			String[] defaultCommands = this.onHitBlockCommands.orElse(new String[0]);

			// Directional commands
			String[] directionalCommands;
			switch (blockHit.getDirection()) {
			case NORTH -> directionalCommands = this.onHitBlockCommandsNorth.orElse(new String[0]);
			case EAST -> directionalCommands = this.onHitBlockCommandsEast.orElse(new String[0]);
			case SOUTH -> directionalCommands = this.onHitBlockCommandsSouth.orElse(new String[0]);
			case WEST -> directionalCommands = this.onHitBlockCommandsWest.orElse(new String[0]);
			case UP -> directionalCommands = this.onHitBlockCommandsUp.orElse(new String[0]);
			case DOWN -> directionalCommands = this.onHitBlockCommandsDown.orElse(new String[0]);
			default -> directionalCommands = new String[0];
			}

			// Combine default and directional commands efficiently
			commands = new String[defaultCommands.length + directionalCommands.length];
			System.arraycopy(defaultCommands, 0, commands, 0, defaultCommands.length);
			System.arraycopy(directionalCommands, 0, commands, defaultCommands.length, directionalCommands.length);
		}
		else {
			return;
		}

		// Execute all commands
		source = source.withPermission(4).withSuppressedOutput();
		for (String command : commands) {
			IEToolTweaks.LOGGER.info("ZZZ: " + command);
			world.getServer().getCommands().performPrefixedCommand(source, command);
		}
	}

	@Override
	public double getBreakChance(@Nullable UUID shooter, ItemStack ammo) {
		return this.rodBreakChance;
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

		private int chargeDuration = 40;
		private float speed = 20;
		private float deviation = 0;
		private boolean isValidForTurret = false;

		private double rodDamage = 16.0;
		private double rodGravity = 1.25;
		private double rodBreakChance = 0.25;
		private boolean ignoresArrowSpecificCoding = false;
		private Optional<RailgunRenderColors> railgunRenderColors = Optional.empty();

		private Optional<String[]> onHitEntityCommands = Optional.empty();
		private Optional<String[]> onHitBlockCommands = Optional.empty();
		private Optional<String[]> onHitBlockCommandsNorth = Optional.empty();
		private Optional<String[]> onHitBlockCommandsEast = Optional.empty();
		private Optional<String[]> onHitBlockCommandsSouth = Optional.empty();
		private Optional<String[]> onHitBlockCommandsWest = Optional.empty();
		private Optional<String[]> onHitBlockCommandsUp = Optional.empty();
		private Optional<String[]> onHitBlockCommandsDown = Optional.empty();

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

		public Builder rodBreakChance(double value) {
			this.rodBreakChance = value;
			return this;
		}

		public Builder ignoresArrowSpecificCoding(boolean value) {
			this.ignoresArrowSpecificCoding = value;
			return this;
		}

		public Builder onHitEntityCommands(List<String> commands) {
			String[] commandsArray = commands.toArray(new String[commands.size()]);
			this.onHitEntityCommands = Optional.of(commandsArray);
			return this;
		}

		public Builder onHitBlockCommands(List<String> commands) {
			String[] commandsArray = commands.toArray(new String[0]);
			this.onHitBlockCommands = Optional.of(commandsArray);
			return this;
		}

		public Builder onHitBlockCommands(List<String> commands, Direction direction) {
			String[] commandsArray = commands.toArray(new String[0]);
			switch (direction) {
			case NORTH -> this.onHitBlockCommandsNorth = Optional.of(commandsArray);
			case EAST -> this.onHitBlockCommandsEast = Optional.of(commandsArray);
			case SOUTH -> this.onHitBlockCommandsSouth = Optional.of(commandsArray);
			case WEST -> this.onHitBlockCommandsWest = Optional.of(commandsArray);
			case UP -> this.onHitBlockCommandsUp = Optional.of(commandsArray);
			case DOWN -> this.onHitBlockCommandsDown = Optional.of(commandsArray);
			}
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

			json.getInt("firing/chargeDuration").ifPresent(builder::chargeDuration);
			json.getFloat("firing/speed").ifPresent(builder::speed);
			json.getFloat("firing/deviation").ifPresent(builder::deviation);
			json.getBoolean("firing/isValidForTurret").ifPresent(builder::isValidForTurret);

			json.getDouble("projectile/damage").ifPresent(builder::rodDamage);
			json.getDouble("projectile/gravity").ifPresent(builder::rodGravity);
			json.getDouble("projectile/breakChance").ifPresent(builder::rodBreakChance);
			json.getBoolean("projectile/useUpgradedProjectile").ifPresent(builder::ignoresArrowSpecificCoding);
			json.getRailgunRenderColors("projectile/railgunRenderColors").ifPresent(builder::railgunRenderColors);

			json.getStringArray("projectile/commandHooks/onHitEntity").ifPresent(builder::onHitEntityCommands);

			json.getStringArray("projectile/commandHooks/onHitBlock").ifPresent(builder::onHitBlockCommands);
			json.getStringArray("projectile/commandHooks/onHitBlockNorth").ifPresent(list -> builder.onHitBlockCommands(list, Direction.NORTH));
			json.getStringArray("projectile/commandHooks/onHitBlockEast").ifPresent(list -> builder.onHitBlockCommands(list, Direction.EAST));
			json.getStringArray("projectile/commandHooks/onHitBlockSouth").ifPresent(list -> builder.onHitBlockCommands(list, Direction.SOUTH));
			json.getStringArray("projectile/commandHooks/onHitBlockWest").ifPresent(list -> builder.onHitBlockCommands(list, Direction.WEST));
			json.getStringArray("projectile/commandHooks/onHitBlockUp").ifPresent(list -> builder.onHitBlockCommands(list, Direction.UP));
			json.getStringArray("projectile/commandHooks/onHitBlockDown").ifPresent(list -> builder.onHitBlockCommands(list, Direction.DOWN));
			return builder;
		}
	}
}