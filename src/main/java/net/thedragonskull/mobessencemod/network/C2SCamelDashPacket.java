package net.thedragonskull.mobessencemod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.thedragonskull.mobessencemod.abilities.CamelAbility;

import java.util.function.Supplier;

public class C2SCamelDashPacket {

    public C2SCamelDashPacket() {
    }

    public C2SCamelDashPacket(FriendlyByteBuf buf) {
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                CamelAbility.tryDash(player);
            }
        });

        context.setPacketHandled(true);
    }

}
