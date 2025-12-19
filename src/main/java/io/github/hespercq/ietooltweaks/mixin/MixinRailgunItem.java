package io.github.hespercq.ietooltweaks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import blusunrize.immersiveengineering.common.items.RailgunItem;
import blusunrize.immersiveengineering.common.register.IEItems.Ingredients;
import blusunrize.immersiveengineering.common.util.IESounds;
import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import blusunrize.immersiveengineering.api.shader.ShaderRegistry;
import blusunrize.immersiveengineering.api.shader.ShaderRegistry.ShaderAndCase;
import blusunrize.immersiveengineering.api.tool.RailgunHandler;
import blusunrize.immersiveengineering.api.tool.RailgunHandler.IRailgunProjectile;
import blusunrize.immersiveengineering.api.utils.ItemUtils;
import io.github.hespercq.ietooltweaks.IEToolTweaks;
import io.github.hespercq.ietooltweaks.railgunrods.DataRailgunProjectile;

@Mixin(RailgunItem.class)
public abstract class MixinRailgunItem {

    /*
     * @Redirect(method = "playChargeSound(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "INVOKE", target =
     * "Lblusunrize/immersiveengineering/common/items/RailgunItem;getChargeTime(Lnet/minecraft/world/item/ItemStack;)I", remap = false), remap = false) private static int
     * redirectPlayChargeSoundGetChargeTime(RailgunItem instance, // "this" reference ItemStack stack, // argument passed to getChargeTime LivingEntity living, // from playChargeSound method ItemStack
     * railgun // from playChargeSound method ) { IEToolTweaks.LOGGER.info("Mixin playChargeSound getChargeTime called!"); return getEntityChargeTime(stack, living); }
     */

    //WORKS
    @Inject(method = "onUseTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;I)V", at = @At("HEAD"), cancellable = true)
    private void injectOnUseTick(Level level, LivingEntity user, ItemStack stack, int count, CallbackInfo ci) {
        RailgunItem railgunItem = (RailgunItem) (Object) this;

        int inUse = railgunItem.getUseDuration(stack) - count;
        int customChargeTime = getEntityChargeTime(stack, user);

        if (inUse > customChargeTime && inUse % 20 == user.getRandom().nextInt(20)) {
            user.level().playSound(null, user.getX(), user.getY(), user.getZ(), IESounds.spark.get(), SoundSource.PLAYERS, 0.8f + 0.2f * user.getRandom().nextFloat(),
                    0.5f + 0.5f * user.getRandom().nextFloat());

            ShaderAndCase shader = ShaderRegistry.getStoredShaderAndCase(stack);
            if (shader != null) {
                Vec3 pos = Utils.getLivingFrontPos(user, 0.4375, user.getBbHeight() * 0.75, ItemUtils.getLivingHand(user, user.getUsedItemHand()), false, 1);
                shader.registryEntry().getEffectFunction().execute(user.level(), shader.shader(), stack, shader.sCase().getShaderType().toString(), pos, null, 0.0625f);
            }
        }
        ci.cancel(); // prevent the original method from running
    }

    /*
     * @Redirect(method = "releaseUsing(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)V", at = @At(value = "INVOKE", target =
     * "Lblusunrize/immersiveengineering/common/items/RailgunItem;getChargeTime(Lnet/minecraft/world/item/ItemStack;)I", remap = false), remap = false) private int
     * redirectReleaseUsingGetChargeTime(ItemStack stack, // argument passed to getChargeTime ItemStack usedStack, // original calling method args Level world, LivingEntity user, int timeLeft) {
     * IEToolTweaks.LOGGER.info("Mixin releaseUsing"); return getEntityChargeTime(stack, user); }
     */
    // ##########################################################################################################
    // #region HELPER
    private static int getEntityChargeTime(ItemStack stack, LivingEntity entity) {
        IEToolTweaks.LOGGER.info("getProjectile!");
        ItemStack ammo;
        if (entity instanceof Player player) {
            IEToolTweaks.LOGGER.info("Player!");
            ammo = RailgunItem.findAmmo(stack, player);
        }
        else {
            ammo = new ItemStack(Ingredients.STICK_STEEL); // Mobs use Steel TODO: Check turret logic
        }
        return getProjectileChargeTime(stack, RailgunHandler.getProjectile(ammo));
    }

    private static int getProjectileChargeTime(ItemStack railgun, IRailgunProjectile projectile) {
        int baseCharge = 40;
        if (projectile instanceof DataRailgunProjectile data) {
            IEToolTweaks.LOGGER.info("Projectile!");
            IEToolTweaks.LOGGER.info(data.chargeDuration);
            baseCharge = data.chargeDuration;
        }
        float speedUpgrade = RailgunItem.getUpgradesStatic(railgun).getFloat("speed");
        return (int) (baseCharge / (1 + speedUpgrade));
    }
    // #endregion
}

/*
 * @Inject(method = "getChargeTime(Lnet/minecraft/world/item/ItemStack;)I", at = @At("HEAD"), cancellable = true, remap = false) private static void injectGetChargeTime(ItemStack railgun,
 * CallbackInfoReturnable<Integer> cir) { IEToolTweaks.LOGGER.info("MixinRailgunItem Called!"); ItemStack ammo = findAmmo(stack, player); IRailgunProjectile projectile =
 * RailgunHandler.getProjectile(railgun); if (projectile instanceof DataRailgunProjectile data) { IEToolTweaks.LOGGER.info("Projectile!"); IEToolTweaks.LOGGER.info(data.chargeDuration); int baseCharge
 * = data.chargeDuration; float speedUpgrade = RailgunItem.getUpgradesStatic(railgun).getFloat("speed"); cir.setReturnValue((int) (baseCharge / (1 + speedUpgrade))); } }
 */
