package io.github.hespercq.ietooltweaks.common;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

public class SyncDataPacket {

    private final int value;

    public SyncDataPacket(int value) {
        this.value = value;
    }

    public SyncDataPacket(FriendlyByteBuf buf) {
        this.value = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(value);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        // Runs on client because packet is sent S2C
        Minecraft mc = Minecraft.getInstance();

        mc.execute(() -> {
            System.out.println("Received value: " + value);

            // Do client-side stuff here
            if (mc.player != null) {
                mc.player.displayClientMessage(
                        Component.literal("Value: " + value),
                        false
                );
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
