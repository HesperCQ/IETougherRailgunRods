package io.github.hespercq.ietooltweaks.drillheads;

import blusunrize.immersiveengineering.api.tool.IDrillHead;
import blusunrize.immersiveengineering.common.items.DrillItem;
import blusunrize.immersiveengineering.common.items.IEBaseItem;
import blusunrize.immersiveengineering.common.register.IEItems.Tools;
import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import blusunrize.immersiveengineering.common.util.Utils;
import io.github.hespercq.ietooltweaks.helpers.DisplayHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;

public class DataDrillHeadItem extends IEBaseItem implements IDrillHead {
	public DataDrillHeadItem() {
		super(new Properties().stacksTo(1));
	}

	@Override
	public Component getName(ItemStack stack) {
		String itemKey = stack.getDescriptionId();
		String subKey = getPermData(stack).name();
		return DisplayHelper.getSubItemDisplayName(itemKey, subKey);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
		DataDrillHeadVariant permData = getPermData(stack);
		list.add(Component.translatable("desc.immersiveengineering.flavour.drillhead.size", new Object[] { permData.miningSize(), permData.miningDepth() }));
		list.add(Component.translatable("desc.immersiveengineering.flavour.drillhead.level", new Object[] { Utils.getHarvestLevelName(this.getMiningLevel(stack)) }));
		list.add(Component.translatable("desc.immersiveengineering.flavour.drillhead.speed", new Object[] { Utils.formatDouble((double) this.getMiningSpeed(stack), "0.###") }));
		list.add(Component.translatable("desc.immersiveengineering.flavour.drillhead.damage", new Object[] { Utils.formatDouble((double) this.getAttackDamage(stack), "0.###") }));
		int maxDmg = this.getMaximumHeadDamage(stack);
		int dmg = maxDmg - this.getHeadDamage(stack);
		float quote = (float) dmg / (float) maxDmg;
		ChatFormatting var10000 = (double) quote < 0.1 ? ChatFormatting.RED : ((double) quote < 0.3 ? ChatFormatting.GOLD : ((double) quote < 0.6 ? ChatFormatting.YELLOW : ChatFormatting.GREEN));
		String status = "" + var10000;
		String s = status + (this.getMaximumHeadDamage(stack) - this.getHeadDamage(stack)) + "/" + this.getMaximumHeadDamage(stack);
		list.add(Component.translatable("desc.immersiveengineering.info.durability", new Object[] { s }));

		// Additional Info:
		if (permData.isVeinMining()) {
			permData.veinMiningTag().ifPresent((veinMiningTag) -> {
				list.add(Component.translatable("desc.ie_hcq_tool_tweaks.flavour.drillhead.vein",
						new Object[] { permData.veinMiningSize(), DisplayHelper.getTagDisplayName(veinMiningTag).getString() }));
			});
		}
	}

	public int getBarWidth(@Nonnull ItemStack stack) {
		return Math.round(13.0F * (1.0F - (float) this.getHeadDamage(stack) / (float) this.getMaximumHeadDamage(stack)));
	}

	public boolean isBarVisible(@Nonnull ItemStack stack) {
		return this.getHeadDamage(stack) > 0;
	}

	public boolean isValidRepairItem(ItemStack stack, ItemStack material) {
		Ingredient ingredient = getPermData(stack).repairMaterial();
		return ingredient.test(material);
	}

	// Custom Data for Item rendering
	public int getItemColor(ItemStack stack) {
		return getPermData(stack).color();
	}

	// ==============================================================================================================
	// #region IDrillHead
	// ==============================================================================================================
	@Override
	public float getAttackDamage(ItemStack head) {
		return getPermData(head).attackDamage();
	}

	@Override
	public int getMaximumHeadDamage(ItemStack head) {
		return getPermData(head).durability();
	}

	@Override
	public Tier getMiningLevel(ItemStack head) {
		return getPermData(head).miningLevel();
	}

	@Override
	public float getMiningSpeed(ItemStack head) {
		return getPermData(head).miningSpeed();
	}

	@Override
	public ResourceLocation getDrillTexture(ItemStack drill, ItemStack head) {
		return getPermData(head).texture();
	}

	@Override
	public int getHeadDamage(ItemStack head) {
		if (head.hasTag()) {
			CompoundTag nbt = head.getOrCreateTag();
			return nbt.contains("headDamage", 3) ? nbt.getInt("headDamage") : nbt.getInt("Damage");
		}
		else {
			return 0;
		}
	}

	// Handlers --------------------------------------------------------------------
	@Override
	public void afterBlockbreak(ItemStack drill, ItemStack head, Player player) {
	}

	@Override
	public boolean beforeBlockbreak(ItemStack drill, ItemStack head, Player player) {
		return false; // Why?
	}

	@Override
	public void damageHead(ItemStack head, int dmg) {
		setHeadDamage(head, this.getHeadDamage(head) + dmg);
	}

	@Override
	// Adds Fix for depth and even numbers & vein mining
	public ImmutableList<BlockPos> getExtraBlocksDug(ItemStack head, Level world, Player player, HitResult rtr) {
		// Exit on no Block
		if (!(rtr instanceof BlockHitResult brtr)) {
			return ImmutableList.of();
		}

		// Get drill params
		DataDrillHeadVariant dh_type = getPermData(head);
		int diameter = dh_type.miningSize();
		int depth = dh_type.miningDepth();

		// Get Start Block Info
		Direction side = brtr.getDirection();
		BlockPos startPos = brtr.getBlockPos();
		BlockState state = world.getBlockState(startPos);

		float maxHardness = 1.0F;
		if (!state.isAir()) {
			maxHardness = state.getDestroyProgress(player, world, startPos) * 0.4F;
		}
		if (maxHardness < 0.0F) {
			maxHardness = 0.0F;
		}

		// Get more start block info
		// Ignore canHarvest & isEffective, maybe add in future when adding logic for effective / can harvest tags for drills
		// boolean canHarvestStart = state.getBlock().canHarvestBlock(world.getBlockState(startPos), world, startPos, player);
		// boolean drillMatStart = ((DrillItem) Tools.DRILL.get()).isEffective(ItemStack.EMPTY, state);
		boolean hardnessStart = state.getDestroyProgress(player, world, startPos) >= maxHardness;

		// Get vein blocks instead if drill and block fit
		boolean stateInTag = dh_type.veinMiningTag().map(state::is).orElse(false);

		if (dh_type.isVeinMining() && stateInTag && hardnessStart) {
			return getBlocksInVein(head, world, player, brtr, dh_type.veinMiningSize());
		}

		if (diameter % 2 == 0) {
			float hx = (float) brtr.getLocation().x - (float) brtr.getBlockPos().getX();
			float hy = (float) brtr.getLocation().y - (float) brtr.getBlockPos().getY();
			float hz = (float) brtr.getLocation().z - (float) brtr.getBlockPos().getZ();
			if (side.getAxis() == Axis.Y && (double) hx < 0.5 || side.getAxis() == Axis.Z && (double) hx < 0.5) {
				startPos = startPos.offset(-1, 0, 0);
			}

			if (side.getAxis() != Axis.Y && (double) hy < 0.5) {
				startPos = startPos.offset(0, -1, 0);
			}

			if (side.getAxis() == Axis.Y && (double) hz < 0.5 || side.getAxis() == Axis.X && (double) hz < 0.5) {
				startPos = startPos.offset(0, 0, -1);
			}
			startPos = startPos.offset((side.getAxis() == Axis.X ? (side.getAxisDirection() == AxisDirection.POSITIVE ? -depth + 1 : 0) : (-diameter / 2) + 1),
					(side.getAxis() == Axis.Y ? (side.getAxisDirection() == AxisDirection.POSITIVE ? -depth + 1 : 0) : (-diameter / 2) + 1),
					(side.getAxis() == Axis.Z ? (side.getAxisDirection() == AxisDirection.POSITIVE ? -depth + 1 : 0) : (-diameter / 2) + 1));
		}
		else {
			startPos = startPos.offset((side.getAxis() == Axis.X ? (side.getAxisDirection() == AxisDirection.POSITIVE ? -depth + 1 : 0) : -diameter / 2),
					(side.getAxis() == Axis.Y ? (side.getAxisDirection() == AxisDirection.POSITIVE ? -depth + 1 : 0) : -diameter / 2),
					(side.getAxis() == Axis.Z ? (side.getAxisDirection() == AxisDirection.POSITIVE ? -depth + 1 : 0) : -diameter / 2));
		}
		ImmutableList.Builder<BlockPos> b = ImmutableList.builder();
		for (int dd = 0; dd < depth; ++dd) {
			for (int dw = 0; dw < diameter; ++dw) {
				for (int dh = 0; dh < diameter; ++dh) {
					BlockPos pos = startPos.offset(side.getAxis() == Axis.X ? dd : dw, side.getAxis() == Axis.Y ? dd : dh, side.getAxis() == Axis.Y ? dh : (side.getAxis() == Axis.X ? dw : dd));
					if (!pos.equals(brtr.getBlockPos())) {
						state = world.getBlockState(pos);
						if (!state.isAir()) {
							Block block = state.getBlock();
							float h = state.getDestroyProgress(player, world, pos);
							boolean canHarvest = block.canHarvestBlock(world.getBlockState(pos), world, pos, player);
							boolean drillMat = ((DrillItem) Tools.DRILL.get()).isEffective(ItemStack.EMPTY, state);
							boolean hardness = h >= maxHardness;
							if (canHarvest && drillMat && hardness) {
								b.add(pos);
							}
						}
					}
				}
			}
		}
		return b.build();
	}

	public ImmutableList<BlockPos> getBlocksInVein(ItemStack head, Level world, Player player, BlockHitResult brtr, int maxBlocksNum) {
		BlockPos startPos = brtr.getBlockPos();
		BlockState startState = world.getBlockState(startPos);
		Block targetBlock = startState.getBlock();

		// Predicate: only vein-mine same block type
		Predicate<BlockPos> isSimilarBlock = pos -> {
			BlockState state = world.getBlockState(pos);
			Block block = state.getBlock();
			// Optionally check harvestability and tool effectiveness
			return block == targetBlock && !state.isAir();
		};

		// BFS for vein mining
		Set<BlockPos> visited = new HashSet<>();
		Queue<BlockPos> queue = new ArrayDeque<>();
		queue.add(startPos);
		visited.add(startPos);

		while (!queue.isEmpty() && visited.size() < maxBlocksNum) {
			BlockPos current = queue.poll();

			for (Direction dir : Direction.values()) {
				BlockPos neighbor = current.relative(dir);
				if (!visited.contains(neighbor) && isSimilarBlock.test(neighbor)) {
					visited.add(neighbor);
					queue.add(neighbor);
				}
			}
		}
		// Remove the original block if you don’t want it mined twice
		visited.remove(startPos);
		return ImmutableList.copyOf(visited);
	}

	// #endregion ===================================================================================================
	// ==============================================================================================================
	// #region HELPERS
	// ==============================================================================================================
	public static String getDrillHeadId(ItemStack stack) {
		return ItemNBTHelper.getString(stack, "drillhead_variant_data");
	}

	public static DataDrillHeadVariant getPermData(ItemStack stack) {
		return DataDrillHeadVariantsDataLoader.getData(getDrillHeadId(stack));
	}

	public static void setHeadDamage(ItemStack head, int totalDamage) {
		CompoundTag nbt = head.getOrCreateTag();
		nbt.remove("headDamage");
		nbt.putInt("Damage", totalDamage);
	}
	// #endregion ===================================================================================================

}