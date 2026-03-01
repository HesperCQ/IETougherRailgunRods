package io.github.hespercq.ietooltweaks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import blusunrize.immersiveengineering.common.items.RailgunItem;
import blusunrize.immersiveengineering.common.register.IEItems.Ingredients;
import io.github.hespercq.ietooltweaks.railgunrods.IRailgunAmmoData;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.api.tool.RailgunHandler;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.ItemOverlayUtils;
import blusunrize.immersiveengineering.client.utils.GuiHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

@Mixin(ItemOverlayUtils.class)
public abstract class MixinItemOverlayUtils {

	// TODO move entity Charge time to different file then railgun mixin
	@Inject(method = "renderRailgunOverlay(Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lcom/mojang/blaze3d/vertex/PoseStack;IILnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), cancellable = true, remap = false)

	private static void injectRenderRailgunOverlay(MultiBufferSource.BufferSource buffer, PoseStack transform, int scaledWidth, int scaledHeight, Player player, InteractionHand hand,
			ItemStack equipped, CallbackInfo ci) {
		// Use your custom getEntityChargeTime
		int duration = 72000 - (player.isUsingItem() && player.getUsedItemHand() == hand ? player.getUseItemRemainingTicks() : 0);
		int chargeTime = getProjectileChargeTime(equipped, player);
		int chargeLevel = duration < 72000 ? Math.min(99, (int) (duration / (float) chargeTime * 100)) : 0;
		float scale = 1.5f;

		VertexConsumer builder = ItemOverlayUtils.getHudElementsBuilder(buffer);
		boolean boundLeft = (player.getMainArm() == HumanoidArm.RIGHT) == (hand == InteractionHand.OFF_HAND);
		float dx = boundLeft ? 24 : (scaledWidth - 24 - 64);
		float dy = scaledHeight - 16;
		transform.pushPose();
		transform.translate(dx, dy, 0);
		GuiHelper.drawTexturedColoredRect(builder, transform, 0, -32, 64, 32, 1, 1, 1, 1, 0, 64 / 256f, 96 / 256f, 128 / 256f);

		ItemStack ammo = RailgunItem.findAmmo(equipped, player);
		if (!ammo.isEmpty())
			GuiHelper.renderItemWithOverlayIntoGUI(buffer, transform, ammo, 6, -22, player.level());

		transform.translate(30, -27.5, 0);
		transform.scale(scale, scale, 1);
		String chargeTxt = chargeLevel < 10 ? "0 " + chargeLevel : chargeLevel / 10 + " " + chargeLevel % 10;
		ClientUtils.font().drawInBatch(chargeTxt, 0, 0, Lib.COLOUR_I_ImmersiveOrange, true, transform.last().pose(), buffer, DisplayMode.NORMAL, 0, 0xf000f0);
		transform.popPose();

		ci.cancel(); // prevent original method from running
	}

	// ##########################################################################################################
	// #region HELPER
	private static int getProjectileChargeTime(ItemStack railgunItemStack, LivingEntity entity) {
		int baseCharge = 40;
		ItemStack ammo = getAmmoStack(railgunItemStack, entity);

		if (RailgunHandler.getProjectile(ammo) instanceof IRailgunAmmoData data) {
			baseCharge = data.getChargeDuration();
		}
		float speedUpgrade = RailgunItem.getUpgradesStatic(railgunItemStack).getFloat("miningSpeed");
		return (int) (baseCharge / (1 + speedUpgrade));

	}

	private static ItemStack getAmmoStack(ItemStack railgunItemStack, LivingEntity entity) {
		if (entity instanceof Player player) {
			return RailgunItem.findAmmo(railgunItemStack, player);
		}
		else {
			// Mobs use
			return new ItemStack(Ingredients.STICK_STEEL); // Mobs use Steel TODO: Check turret logic
		}
	}
	// #endregion
}
