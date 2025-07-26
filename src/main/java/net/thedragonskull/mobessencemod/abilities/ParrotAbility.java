package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.InputEvent;
import net.thedragonskull.mobessencemod.network.C2SParrotFlapPacket;
import net.thedragonskull.mobessencemod.network.PacketHandler;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ParrotAbility implements IMobAbility {

    public static final Map<UUID, Boolean> hasDoubleJumped = new HashMap<>();
    private static final Map<UUID, Boolean> wasJumpKeyDown = new HashMap<>();

    @Override
    public void tick(ServerPlayer player, ItemStack stack) {
        if (player.onGround()) {
            hasDoubleJumped.put(player.getUUID(), false);
        }
    }

    public static void flap(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null) return;

        UUID uuid = player.getUUID();

        if (!TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:parrot"))) return;

        boolean jumpKeyDown = mc.options.keyJump.isDown();
        boolean wasDown = wasJumpKeyDown.getOrDefault(uuid, false);

        if (jumpKeyDown && !wasDown) {
            if (!player.onGround() && !player.isInWater() && !player.isInLava() && !player.isSwimming()) {
                PacketHandler.sendToServer(new C2SParrotFlapPacket());
            }
        }

        wasJumpKeyDown.put(uuid, jumpKeyDown);
    }

}
