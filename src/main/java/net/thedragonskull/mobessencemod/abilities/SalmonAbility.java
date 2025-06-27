package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class SalmonAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void swimBoost(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Player player = event.player;
        if (player == null || !player.level().isClientSide()) return;

        if (!player.isUnderWater() || !player.isSprinting()) return;

        if (!TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:salmon")))
            return;

        Minecraft mc = Minecraft.getInstance();

        boolean moving =
                mc.options.keyUp.isDown() ||
                        mc.options.keyDown.isDown() ||
                        mc.options.keyLeft.isDown() ||
                        mc.options.keyRight.isDown();

        if (!moving) return;

        Vec3 current = player.getDeltaMovement();
        Vec3 forward = player.getLookAngle().normalize().scale(0.02);

        Vec3 boosted = current.add(forward.x, forward.y, forward.z);
        player.setDeltaMovement(boosted);
    }

}
