package io.github.hespercq.ietooltweaks.common.railgun_ammo;

import net.minecraft.resources.ResourceLocation;

public record RailgunAmmoHolder(
        ResourceLocation id,
        RailgunAmmo ammo
) {}