package net.thedragonskull.mobessencemod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.thedragonskull.mobessencemod.abilities.IronGolemAbility;

import java.util.UUID;
import java.util.function.Supplier;

public class S2CIronGolemFractureSyncPacket {
    private final UUID playerId;
    private final boolean fractured;

    public S2CIronGolemFractureSyncPacket(UUID playerId, Boolean fractured) {
        this.playerId = playerId;
        this.fractured = fractured;
    }

    public S2CIronGolemFractureSyncPacket(FriendlyByteBuf buf) {
        this.playerId = buf.readUUID();
        this.fractured = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(playerId);
        buf.writeBoolean(fractured);
    }

    public static void handle(S2CIronGolemFractureSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (msg.fractured) {
                IronGolemAbility.IronGolemAbilityClient.addFractured(msg.playerId);
            } else {
                IronGolemAbility.IronGolemAbilityClient.removeFractured(msg.playerId);
            }
        });

        ctx.get().setPacketHandled(true);
    }

}
