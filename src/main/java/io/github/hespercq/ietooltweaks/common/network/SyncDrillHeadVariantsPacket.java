package io.github.hespercq.ietooltweaks.common.network;

import java.util.Map;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;

import io.github.hespercq.ietooltweaks.common.drillheads.DrillHeadVariant;
import io.github.hespercq.ietooltweaks.common.drillheads.DrillHeadVariantManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

public class SyncDrillHeadVariantsPacket {

    private static final Codec<Map<ResourceLocation, DrillHeadVariant>> DRILLHEAD_VARIANT_MAP_CODEC = Codec.unboundedMap(ResourceLocation.CODEC, DrillHeadVariant.CODEC);

    private final Map<ResourceLocation, DrillHeadVariant> drillHeadVariants;

    public SyncDrillHeadVariantsPacket(Map<ResourceLocation, DrillHeadVariant> drillHeadVriants) {
        this.drillHeadVariants = drillHeadVriants;
    }

    public SyncDrillHeadVariantsPacket(FriendlyByteBuf buf) {
        CompoundTag tag = buf.readNbt();

        this.drillHeadVariants = DRILLHEAD_VARIANT_MAP_CODEC.parse(NbtOps.INSTANCE, tag).getOrThrow(false, System.err::println);
    }

    public void encode(FriendlyByteBuf buf) {
        CompoundTag tag = (CompoundTag) DRILLHEAD_VARIANT_MAP_CODEC.encodeStart(NbtOps.INSTANCE, drillHeadVariants).getOrThrow(false, System.err::println);

        buf.writeNbt(tag);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            System.out.println("Received " + drillHeadVariants.size() + " drill head variants");
            DrillHeadVariantManager.VARIANTS.clear();
            DrillHeadVariantManager.VARIANTS.putAll(drillHeadVariants);
        });

        ctx.get().setPacketHandled(true);
    }
}
