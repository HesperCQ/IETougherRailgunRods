package io.github.hespercq.ietooltweaks.common.railgun_ammo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record RailgunAmmoFiringData(boolean isValidForTurret, int chargeDuration, float speed, float deviation) {

    public static final RailgunAmmoFiringData DEFAULT = new RailgunAmmoFiringData(

            false, 40, 20f, 0f);

    public static final Codec<RailgunAmmoFiringData> CODEC = RecordCodecBuilder.create(instance -> instance.group(

            Codec.BOOL.optionalFieldOf("isValidForTurret", DEFAULT.isValidForTurret()).forGetter(RailgunAmmoFiringData::isValidForTurret),

            Codec.INT.optionalFieldOf("chargeDuration", DEFAULT.chargeDuration()).forGetter(RailgunAmmoFiringData::chargeDuration),

            Codec.FLOAT.optionalFieldOf("speed", DEFAULT.speed()).forGetter(RailgunAmmoFiringData::speed),

            Codec.FLOAT.optionalFieldOf("deviation", DEFAULT.deviation()).forGetter(RailgunAmmoFiringData::deviation))

            .apply(instance, RailgunAmmoFiringData::new));

}
