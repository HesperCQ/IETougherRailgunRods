package io.github.hespercq.ietooltweaks.common.railgunrods;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record RailgunAmmoCommandHooks(List<String> onHitEntity, List<String> onHitBlock, List<String> onHitBlockNorth, List<String> onHitBlockEast, List<String> onHitBlockSouth,
		List<String> onHitBlockWest, List<String> onHitBlockUp, List<String> onHitBlockDown) {

	public static final RailgunAmmoCommandHooks EMPTY = new RailgunAmmoCommandHooks(List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of());

	public static final Codec<RailgunAmmoCommandHooks> CODEC = RecordCodecBuilder.create(instance -> instance.group(

			Codec.STRING.listOf().optionalFieldOf("onHitEntity", List.of()).forGetter(RailgunAmmoCommandHooks::onHitEntity),

			Codec.STRING.listOf().optionalFieldOf("onHitBlock", List.of()).forGetter(RailgunAmmoCommandHooks::onHitBlock),

			Codec.STRING.listOf().optionalFieldOf("onHitBlockNorth", List.of()).forGetter(RailgunAmmoCommandHooks::onHitBlockNorth),

			Codec.STRING.listOf().optionalFieldOf("onHitBlockEast", List.of()).forGetter(RailgunAmmoCommandHooks::onHitBlockEast),

			Codec.STRING.listOf().optionalFieldOf("onHitBlockSouth", List.of()).forGetter(RailgunAmmoCommandHooks::onHitBlockSouth),

			Codec.STRING.listOf().optionalFieldOf("onHitBlockWest", List.of()).forGetter(RailgunAmmoCommandHooks::onHitBlockWest),

			Codec.STRING.listOf().optionalFieldOf("onHitBlockUp", List.of()).forGetter(RailgunAmmoCommandHooks::onHitBlockUp),

			Codec.STRING.listOf().optionalFieldOf("onHitBlockDown", List.of()).forGetter(RailgunAmmoCommandHooks::onHitBlockDown)

	).apply(instance, RailgunAmmoCommandHooks::new));
}
