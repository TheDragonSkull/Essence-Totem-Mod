package net.thedragonskull.mobessencemod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SParrotFlapSoundPacket {

    public C2SParrotFlapSoundPacket() {
    }

    public C2SParrotFlapSoundPacket(FriendlyByteBuf buf) {
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                player.level().playSound(null, player.blockPosition(),
                        SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 5.0F, 1.0F);
            }
        });

        context.setPacketHandled(true);
    }

}
