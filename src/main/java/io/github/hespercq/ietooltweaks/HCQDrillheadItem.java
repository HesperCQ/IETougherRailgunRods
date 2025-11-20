package io.github.hespercq.ietooltweaks;

import com.google.common.collect.ImmutableList;
import blusunrize.immersiveengineering.common.register.IEItems.Tools;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import blusunrize.immersiveengineering.common.items.DrillItem;
import blusunrize.immersiveengineering.common.items.DrillheadItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;

public class HCQDrillheadItem extends DrillheadItem {

        public HCQDrillheadItem(HCQDrillHeadPerm perms) {
                super(perms);
                this.perms = perms; // Needed for fix
        }

        // 3*3*1
        public static final HCQDrillHeadPerm SHADOW_STEEL = new HCQDrillHeadPerm(
                        "shadow_steel",
                        TagKey.create(Registries.ITEM,
                                        ResourceLocation.fromNamespaceAndPath("hcq_hextech",
                                                        "drillhead_repair_shadow_steel")),
                        3,
                        1,
                        Tiers.NETHERITE,
                        32, // Speed
                        7, // Attack
                        12000, // Durability
                        ResourceLocation.fromNamespaceAndPath(IEToolTweaks.MODID, "item/drill_shadow_steel"));

        public static final HCQDrillHeadPerm BLACK_STEEL = new HCQDrillHeadPerm(
                        "black_steel",
                        TagKey.create(Registries.ITEM,
                                        ResourceLocation.fromNamespaceAndPath("hcq_hextech",
                                                        "drillhead_repair_black_steel")),
                        3,
                        1,
                        Tiers.NETHERITE,
                        48, // Speed
                        7, // Attack
                        16000, // Durability
                        ResourceLocation.fromNamespaceAndPath(IEToolTweaks.MODID, "item/drill_black_steel"));

        // X*X*1
        public static final HCQDrillHeadPerm ANCIENT = new HCQDrillHeadPerm(
                        "ancient_metal",
                        TagKey.create(Registries.ITEM,
                                        ResourceLocation.fromNamespaceAndPath("hcq_hextech",
                                                        "drillhead_repair_ancient")),
                        4,
                        1,
                        Tiers.NETHERITE,
                        32, // Speed
                        9, // Attack
                        10000, // Durability
                        ResourceLocation.fromNamespaceAndPath(IEToolTweaks.MODID, "item/drill_ancient"));

        public static final HCQDrillHeadPerm IGNIUM = new HCQDrillHeadPerm(
                        "ignium",
                        TagKey.create(Registries.ITEM,
                                        ResourceLocation.fromNamespaceAndPath("hcq_hextech",
                                                        "drillhead_repair_ignium")),
                        5,
                        1,
                        Tiers.NETHERITE,
                        32, // Speed
                        9, // Attack
                        12000, // Durability
                        ResourceLocation.fromNamespaceAndPath(IEToolTweaks.MODID, "item/drill_ignium"));

        public static final HCQDrillHeadPerm CURSIUM = new HCQDrillHeadPerm(
                        "cursium",
                        TagKey.create(Registries.ITEM,
                                        ResourceLocation.fromNamespaceAndPath("hcq_hextech",
                                                        "drillhead_repair_cursium")),
                        5,
                        1,
                        Tiers.NETHERITE,
                        32, // Speed
                        9, // Attack
                        16000, // Durability
                        ResourceLocation.fromNamespaceAndPath(IEToolTweaks.MODID, "item/drill_cursium"));
        // ==== BOTANIA =======================
        // X * X * X
        public static final HCQDrillHeadPerm MANA = new HCQDrillHeadPerm(
                        "mana",
                        TagKey.create(Registries.ITEM,
                                        ResourceLocation.fromNamespaceAndPath("hcq_hextech",
                                                        "drillhead_repair_mana")),
                        3,
                        3,
                        Tiers.NETHERITE,
                        4, // Speed
                        6, // Attack
                        5000, // Durability
                        ResourceLocation.fromNamespaceAndPath(IEToolTweaks.MODID, "item/drill_mana"));

        public static final HCQDrillHeadPerm TERRA = new HCQDrillHeadPerm(
                        "terra",
                        TagKey.create(Registries.ITEM,
                                        ResourceLocation.fromNamespaceAndPath("hcq_hextech",
                                                        "drillhead_repair_terra")),
                        5,
                        5,
                        Tiers.NETHERITE,
                        4, // Speed
                        6, // Attack
                        10000, // Durability
                        ResourceLocation.fromNamespaceAndPath(IEToolTweaks.MODID, "item/drill_terra"));

        public static final HCQDrillHeadPerm ELEMENTIUM = new HCQDrillHeadPerm(
                        "elementium",
                        TagKey.create(Registries.ITEM,
                                        ResourceLocation.fromNamespaceAndPath("hcq_hextech",
                                                        "drillhead_repair_elementium")),
                        2,
                        6,
                        Tiers.NETHERITE,
                        20, // Speed
                        6, // Attack
                        10000, // Durability
                        ResourceLocation.fromNamespaceAndPath(IEToolTweaks.MODID, "item/drill_elementium"));
        
        public static final HCQDrillHeadPerm RADIANCE = new HCQDrillHeadPerm(
                        "radiance",
                        TagKey.create(Registries.ITEM,
                                        ResourceLocation.fromNamespaceAndPath("hcq_hextech",
                                                        "drillhead_repair_radiance")),
                        9,
                        9,
                        Tiers.NETHERITE,
                        1, // Speed
                        50, // Attack
                        1, // Durability
                        ResourceLocation.fromNamespaceAndPath(IEToolTweaks.MODID, "item/drill_radiance"));

        // ==============================================================================================================
        // #region Fix Depth Offset
        public static class HCQDrillHeadPerm extends DrillHeadPerm {
                public final int drillSize;
                public final int drillDepth;

                public HCQDrillHeadPerm(String name, TagKey<Item> repairMaterial, int drillSize, int drillDepth,
                                Tier drillLevel, float drillSpeed, int drillAttack, int maxDamage,
                                ResourceLocation texture) {
                        super(name, repairMaterial, drillSize, drillDepth, drillLevel, drillSpeed, drillAttack,
                                        maxDamage, texture);
                        this.drillSize = drillSize;
                        this.drillDepth = drillDepth;
                }
        }

        public HCQDrillHeadPerm perms;

        @Override
        public ImmutableList<BlockPos> getExtraBlocksDug(ItemStack head, Level world, Player player, HitResult rtr) {
                if (!(rtr instanceof BlockHitResult brtr)) {
                        return ImmutableList.of();
                } else {
                        Direction side = brtr.getDirection();
                        int diameter = this.perms.drillSize;
                        int depth = this.perms.drillDepth;
                        BlockPos startPos = brtr.getBlockPos();
                        BlockState state = world.getBlockState(startPos);
                        float maxHardness = 1.0F;
                        if (!state.isAir()) {
                                maxHardness = state.getDestroyProgress(player, world, startPos) * 0.4F;
                        }

                        if (maxHardness < 0.0F) {
                                maxHardness = 0.0F;
                        }

                        if (diameter % 2 == 0) {
                                float hx = (float) brtr.getLocation().x - (float) brtr.getBlockPos().getX();
                                float hy = (float) brtr.getLocation().y - (float) brtr.getBlockPos().getY();
                                float hz = (float) brtr.getLocation().z - (float) brtr.getBlockPos().getZ();
                                // HCQ Fix
                                if (side.getAxis() == Axis.Y && (double) hx < 0.5
                                                || side.getAxis() == Axis.Z && (double) hx < 0.5) {
                                        startPos = startPos.offset(-1, 0, 0);
                                }

                                if (side.getAxis() != Axis.Y && (double) hy < 0.5) {
                                        startPos = startPos.offset(0, -1, 0);
                                }

                                if (side.getAxis() == Axis.Y && (double) hz < 0.5
                                                || side.getAxis() == Axis.X && (double) hz < 0.5) {
                                        startPos = startPos.offset(0, 0, -1);
                                }
                                // HCQ Fix
                                startPos = startPos.offset(
                                                // HCQ Fix
                                                (side.getAxis() == Axis.X
                                                                ? (side.getAxisDirection() == AxisDirection.POSITIVE
                                                                                ? -depth + 1
                                                                                : 0)
                                                                : (-diameter / 2) + 1),
                                                (side.getAxis() == Axis.Y
                                                                ? (side.getAxisDirection() == AxisDirection.POSITIVE
                                                                                ? -depth + 1
                                                                                : 0)
                                                                : (-diameter / 2) + 1),
                                                (side.getAxis() == Axis.Z
                                                                ? (side.getAxisDirection() == AxisDirection.POSITIVE
                                                                                ? -depth + 1
                                                                                : 0)
                                                                : (-diameter / 2) + 1));
                        } else {
                                startPos = startPos.offset(
                                                // HCQ Fix
                                                (side.getAxis() == Axis.X
                                                                ? (side.getAxisDirection() == AxisDirection.POSITIVE
                                                                                ? -depth + 1
                                                                                : 0)
                                                                : -diameter / 2),
                                                (side.getAxis() == Axis.Y
                                                                ? (side.getAxisDirection() == AxisDirection.POSITIVE
                                                                                ? -depth + 1
                                                                                : 0)
                                                                : -diameter / 2),
                                                (side.getAxis() == Axis.Z
                                                                ? (side.getAxisDirection() == AxisDirection.POSITIVE
                                                                                ? -depth + 1
                                                                                : 0)
                                                                : -diameter / 2));
                        }

                        ImmutableList.Builder<BlockPos> b = ImmutableList.builder();

                        for (int dd = 0; dd < depth; ++dd) {
                                for (int dw = 0; dw < diameter; ++dw) {
                                        for (int dh = 0; dh < diameter; ++dh) {
                                                BlockPos pos = startPos.offset(
                                                                side.getAxis() == Axis.X ? dd : dw,
                                                                side.getAxis() == Axis.Y ? dd : dh,
                                                                side.getAxis() == Axis.Y ? dh
                                                                                : (side.getAxis() == Axis.X ? dw : dd));
                                                if (!pos.equals(brtr.getBlockPos())) {
                                                        state = world.getBlockState(pos);
                                                        if (!state.isAir()) {
                                                                Block block = state.getBlock();
                                                                float h = state.getDestroyProgress(player, world, pos);
                                                                boolean canHarvest = block.canHarvestBlock(
                                                                                world.getBlockState(pos), world, pos,
                                                                                player);
                                                                boolean drillMat = ((DrillItem) Tools.DRILL.get())
                                                                                .isEffective(ItemStack.EMPTY, state);
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
        }

        // #endregion
}