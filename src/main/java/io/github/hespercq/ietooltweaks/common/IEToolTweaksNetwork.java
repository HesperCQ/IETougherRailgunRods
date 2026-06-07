package io.github.hespercq.ietooltweaks.common;

import io.github.hespercq.ietooltweaks.IEToolTweaks;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class IEToolTweaksNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath(IEToolTweaks.MODID, "messages"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

    private static int packetId = 0;

    public static void register() {
        INSTANCE.messageBuilder(SyncDrillHeadVariantsPacket.class, packetId++)
                .encoder(SyncDrillHeadVariantsPacket::encode)
                .decoder(SyncDrillHeadVariantsPacket::new)
                .consumerMainThread(SyncDrillHeadVariantsPacket::handle)
                .add();
    }
}