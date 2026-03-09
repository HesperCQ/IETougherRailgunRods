package io.github.hespercq.ietooltweaks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import blusunrize.immersiveengineering.common.config.IEServerConfig;
import blusunrize.immersiveengineering.common.items.RailgunItem;
import blusunrize.immersiveengineering.common.register.IEItems.Ingredients;
import blusunrize.immersiveengineering.common.util.IESounds;
import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import blusunrize.immersiveengineering.api.shader.ShaderRegistry;
import blusunrize.immersiveengineering.api.shader.ShaderRegistry.ShaderAndCase;
import blusunrize.immersiveengineering.api.tool.RailgunHandler;
import blusunrize.immersiveengineering.api.utils.CapabilityUtils;
import blusunrize.immersiveengineering.api.utils.ItemUtils;
import io.github.hespercq.ietooltweaks.railgunrods.IRailgunAmmoData;

@Mixin(RailgunItem.class)
public abstract class MixinRailgunItem {

    @Inject(method = "playChargeSound(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private static void injectPlayChargeSound(LivingEntity living, ItemStack railgun, CallbackInfo ci) {
        float customChargeTime = getProjectileChargeTime(railgun, living);
        float pitch = 1.0f;
        float sampleTime = customChargeTime < 40 ? 20.0f : 35.0f;
        if (customChargeTime < 10) { // Do not play charge up sound as pitch would be to high
            ci.cancel();
            return;
        }
        if (customChargeTime < 20) { // Make charge sound shorter if custom charge time is shorter than normal short sound
            pitch = sampleTime / customChargeTime;
        }
        double varianceMultiplier = 1 + (0.03 * (Math.random() - 0.5)); // +/- 3%
        pitch = (float) (pitch * varianceMultiplier);

        living.level().playSound(null, living.getX(), living.getY(), living.getZ(), customChargeTime < 40 ? IESounds.chargeFast.get() : IESounds.chargeSlow.get(), SoundSource.PLAYERS, 1.5f, pitch);

        ci.cancel(); // prevent the original method from running
    }

    // onUseTick - Works
    @Inject(method = "onUseTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;I)V", at = @At("HEAD"), cancellable = true)
    private void injectOnUseTick(Level level, LivingEntity user, ItemStack stack, int count, CallbackInfo ci) {
        RailgunItem railgunItem = (RailgunItem) (Object) this;

        int inUse = railgunItem.getUseDuration(stack) - count;

        // Get Custom data
        int customChargeTime = getProjectileChargeTime(stack, user);
        float sampleTime = customChargeTime < 40 ? 20.0f : 35.0f;

        // Do Stuff after sampleTime is over
        if (inUse > sampleTime && inUse % 20 == user.getRandom().nextInt(20)) {
            user.level().playSound(null, user.getX(), user.getY(), user.getZ(), IESounds.spark.get(), SoundSource.PLAYERS, 0.8f + 0.2f * user.getRandom().nextFloat(),
                    0.5f + 0.5f * user.getRandom().nextFloat());

            ShaderAndCase shader = ShaderRegistry.getStoredShaderAndCase(stack);
            if (shader != null) {
                Vec3 pos = Utils.getLivingFrontPos(user, 0.4375, user.getBbHeight() * 0.75, ItemUtils.getLivingHand(user, user.getUsedItemHand()), false, 1);
                shader.registryEntry().getEffectFunction().execute(user.level(), shader.shader(), stack, shader.sCase().getShaderType().toString(), pos, null, 0.0625f);
            }
        }

        // TODO MAKE THIS CONFIGURABLE VIA CLIENT CONFIG
        // Play sound when charge time is hit maybe little earlier because reaction time
        if (inUse == (customChargeTime)) {
            user.level().playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.NOTE_BLOCK_BELL.get(), // Sound
                    SoundSource.PLAYERS, 1, // Volume
                    2.0f - (0.03f * user.getRandom().nextFloat())); // Pitch
        }
        ci.cancel(); // prevent the original method from running
    }

    // releaseUsing - should Work
    @Inject(method = "releaseUsing(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)V", at = @At("HEAD"), cancellable = true)
    private void injectReleaseUsing(ItemStack stack, Level world, LivingEntity user, int timeLeft, CallbackInfo ci) {
        RailgunItem railgunItem = (RailgunItem) (Object) this; // cast to RailgunItem

        if (!world.isClientSide() && user instanceof Player player) {
            int inUse = railgunItem.getUseDuration(stack) - timeLeft;
            ItemNBTHelper.remove(stack, "inUse");

            int customChargeTime = getProjectileChargeTime(stack, user);
            if (inUse < customChargeTime) {
                ci.cancel(); // stop vanilla method
                return;
            }

            int consumption = IEServerConfig.TOOLS.railgun_consumption.get();
            float energyMod = 1 + railgunItem.getUpgrades(stack).getFloat("consumption");
            consumption = (int) (consumption * energyMod);

            IEnergyStorage energy = CapabilityUtils.getPresentCapability(stack, ForgeCapabilities.ENERGY);
            if (energy.extractEnergy(consumption, true) == consumption) {
                ItemStack ammo = RailgunItem.findAmmo(stack, player);
                if (!ammo.isEmpty()) {
                    ItemStack ammoConsumed = ammo.split(1);
                    RailgunItem.fireProjectile(stack, world, user, ammoConsumed);
                    energy.extractEnergy(consumption, false);
                }
            }
        }

        ci.cancel(); // stop vanilla method
    }

    // ##########################################################################################################
    // #region HELPER
    private static int getProjectileChargeTime(ItemStack railgunItemStack, LivingEntity entity) {
        int baseCharge = 40;
        ItemStack ammo = getAmmoStack(railgunItemStack, entity);

        if (RailgunHandler.getProjectile(ammo) instanceof IRailgunAmmoData data) {
            baseCharge = data.getChargeDuration();
        }
        float speedUpgrade = RailgunItem.getUpgradesStatic(railgunItemStack).getFloat("speed");
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

/*
 * @Inject(method = "getChargeTime(Lnet/minecraft/world/item/ItemStack;)I", at = @At("HEAD"), cancellable = true, remap = false) private static void injectGetChargeTime(ItemStack railgun,
 * CallbackInfoReturnable<Integer> cir) { IEToolTweaks.LOGGER.info("MixinRailgunItem Called!"); ItemStack ammo = findAmmo(stack, player); IRailgunProjectile projectile =
 * RailgunHandler.getProjectile(railgun); if (projectile instanceof RailgunAmmoData data) { IEToolTweaks.LOGGER.info("Projectile!"); IEToolTweaks.LOGGER.info(data.chargeDuration); int baseCharge
 * = data.chargeDuration; float speedUpgrade = RailgunItem.getUpgradesStatic(railgun).getFloat("speed"); cir.setReturnValue((int) (baseCharge / (1 + speedUpgrade))); } }
 */
