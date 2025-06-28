package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class ChickenAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack stack) {
        if (!player.onGround() && player.getDeltaMovement().y < 0.0D) {
            player.fallDistance = 0.0F;
        }
    }

    public static void slowFall(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Player player = event.player;
        if (player == null || !player.level().isClientSide()) return;

        if (!TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:chicken"))) return;

        Vec3 vec3 = player.getDeltaMovement();

        if (!player.onGround() && vec3.y < 0.0D) {
            player.setDeltaMovement(vec3.multiply(1.0D, 0.6D, 1.0D));
            player.fallDistance = 0.0F;
        }
    }

}
