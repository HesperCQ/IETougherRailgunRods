package io.github.hespercq.ietooltweaks.common.network;

import java.util.Map;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;

import io.github.hespercq.ietooltweaks.common.railgun_ammo.RailgunAmmo;
import io.github.hespercq.ietooltweaks.common.railgun_ammo.RailgunAmmoManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

public class SyncRailgunAmmosPacket {
    private static final Codec<Map<ResourceLocation, RailgunAmmo>> RAILGUN_AMMO_MAP_CODEC = Codec.unboundedMap(ResourceLocation.CODEC, RailgunAmmo.CODEC);

    private final Map<ResourceLocation, RailgunAmmo> railgunAmmos;

    public SyncRailgunAmmosPacket(Map<ResourceLocation, RailgunAmmo> drillHeadVriants) {
        this.railgunAmmos = drillHeadVriants;
    }

    public SyncRailgunAmmosPacket(FriendlyByteBuf buf) {
        CompoundTag tag = buf.readNbt();

        this.railgunAmmos = RAILGUN_AMMO_MAP_CODEC.parse(NbtOps.INSTANCE, tag).getOrThrow(false, System.err::println);
    }

    public void encode(FriendlyByteBuf buf) {
        CompoundTag tag = (CompoundTag) RAILGUN_AMMO_MAP_CODEC.encodeStart(NbtOps.INSTANCE, railgunAmmos).getOrThrow(false, System.err::println);

        buf.writeNbt(tag);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            System.out.println("Received " + railgunAmmos.size() + " railgun ammos");
            RailgunAmmoManager.AMMOS.clear();
            RailgunAmmoManager.AMMOS.putAll(railgunAmmos);
            RailgunAmmoManager.updateIERailgunHandler();
        });

        ctx.get().setPacketHandled(true);
    }
}
