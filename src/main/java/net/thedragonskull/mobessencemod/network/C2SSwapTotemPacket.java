package net.thedragonskull.mobessencemod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.function.Supplier;

public class C2SSwapTotemPacket {

    public C2SSwapTotemPacket() {
    }

    public C2SSwapTotemPacket(FriendlyByteBuf buf) {
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                TotemUtils.swapEssenceTotemServer(player);
            }
        });

        context.setPacketHandled(true);
    }

}
