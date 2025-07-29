package net.thedragonskull.mobessencemod.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.thedragonskull.mobessencemod.capability.ClientGemDataStorage;

import java.util.UUID;
import java.util.function.Supplier;

public class S2CCrownGemSyncPacket {
    private final UUID playerId;
    private final CompoundTag tag;

    public S2CCrownGemSyncPacket(UUID playerId, CompoundTag tag) {
        this.playerId = playerId;
        this.tag = tag;
    }

    public S2CCrownGemSyncPacket(FriendlyByteBuf buf) {
        this.playerId = buf.readUUID();
        this.tag = buf.readNbt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(playerId);
        buf.writeNbt(tag);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientGemDataStorage.receive(playerId, tag);
        });

        ctx.get().setPacketHandled(true);
    }
}
