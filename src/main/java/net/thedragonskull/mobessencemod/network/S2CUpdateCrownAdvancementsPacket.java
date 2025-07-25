package net.thedragonskull.mobessencemod.network;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CUpdateCrownAdvancementsPacket {
    private final CompoundTag data;

    public S2CUpdateCrownAdvancementsPacket(CompoundTag data) {
        this.data = data;
    }

    public S2CUpdateCrownAdvancementsPacket(FriendlyByteBuf buf) {
        this.data = buf.readNbt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(data);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                player.getPersistentData().merge(data);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
