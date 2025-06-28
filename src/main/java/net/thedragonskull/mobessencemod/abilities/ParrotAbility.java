package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.thedragonskull.mobessencemod.network.C2SParrotFlapSoundPacket;
import net.thedragonskull.mobessencemod.network.PacketHandler;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ParrotAbility implements IMobAbility {

    private static final Map<UUID, Boolean> hasDoubleJumped = new HashMap<>();
    private static boolean wasJumpKeyDown = false;

    @Override
    public void tick(ServerPlayer player, ItemStack stack) {
        if (player.onGround()) {
            hasDoubleJumped.remove(player.getUUID());
        }
    }

    public static void flap(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null) return;

        if (!TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:parrot")))
            return;

        UUID uuid = player.getUUID();
        boolean alreadyJumped = hasDoubleJumped.getOrDefault(uuid, false);
        boolean jumpKeyDown = mc.options.keyJump.isDown();

        if (jumpKeyDown && !wasJumpKeyDown) {
            if (!player.onGround() && !player.isInWater() && !player.isInLava() && !player.isSwimming()) {
                if (!alreadyJumped) {
                    Vec3 motion = player.getDeltaMovement();
                    player.setDeltaMovement(motion.x, 0.52, motion.z);
                    player.hasImpulse = true;

                    PacketHandler.sendToServer(new C2SParrotFlapSoundPacket());

                    hasDoubleJumped.put(uuid, true);
                }
            }
        }

        wasJumpKeyDown = jumpKeyDown;
    }

}
