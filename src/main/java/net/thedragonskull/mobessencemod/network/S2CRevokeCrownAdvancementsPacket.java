package net.thedragonskull.mobessencemod.network;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CRevokeCrownAdvancementsPacket {
    private final String data;

    public S2CRevokeCrownAdvancementsPacket(String data) {
        this.data = data;
    }

    public S2CRevokeCrownAdvancementsPacket(FriendlyByteBuf buf) {
        this.data = buf.readUtf();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(data);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                player.getPersistentData().remove(data);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
