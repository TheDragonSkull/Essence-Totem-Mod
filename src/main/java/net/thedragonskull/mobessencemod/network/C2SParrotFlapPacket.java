package net.thedragonskull.mobessencemod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.thedragonskull.mobessencemod.abilities.ParrotAbility;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.UUID;
import java.util.function.Supplier;

public class C2SParrotFlapPacket {

    public C2SParrotFlapPacket() {
    }

    public C2SParrotFlapPacket(FriendlyByteBuf buf) {
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public static void handle(C2SParrotFlapPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:parrot"))) return;

            UUID uuid = player.getUUID();
            boolean alreadyJumped = ParrotAbility.hasDoubleJumped.getOrDefault(uuid, false);

            if (!player.onGround() && !player.isInWater() && !player.isInLava() && !player.isSwimming()) {
                if (!alreadyJumped) {
                    Vec3 motion = player.getDeltaMovement();
                    player.setDeltaMovement(motion.x, 0.52, motion.z);
                    player.hasImpulse = true;
                    player.hurtMarked = true;

                    ParrotAbility.hasDoubleJumped.put(uuid, true);
                    player.level().playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1.5F, 1.0F);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
