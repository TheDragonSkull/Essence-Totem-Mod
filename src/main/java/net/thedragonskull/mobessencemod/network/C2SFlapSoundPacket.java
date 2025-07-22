package net.thedragonskull.mobessencemod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SFlapSoundPacket {
    private final ResourceLocation essenceId;

    public C2SFlapSoundPacket(ResourceLocation essenceId) {
        this.essenceId = essenceId;
    }

    public C2SFlapSoundPacket(FriendlyByteBuf buf) {
        this.essenceId = buf.readResourceLocation();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(essenceId);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            SoundEvent sound;
            switch (essenceId.toString()) {
                case "minecraft:parrot" -> sound = SoundEvents.ARMOR_EQUIP_LEATHER;
                case "minecraft:ender_dragon" -> sound = SoundEvents.ENDER_DRAGON_FLAP;
                default -> sound = SoundEvents.PARROT_FLY;
            }

            player.level().playSound(null, player.blockPosition(), sound, SoundSource.PLAYERS, 1.5f, 1.0f);
        });

        context.setPacketHandled(true);
    }

}
