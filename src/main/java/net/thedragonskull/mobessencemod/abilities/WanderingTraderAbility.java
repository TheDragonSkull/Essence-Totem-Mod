package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class WanderingTraderAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onLlamaTarget(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Llama)) return;
        if (!(event.getNewTarget() instanceof Player player)) return;

        if (TotemUtils.hasTotemWithEssenceServer((ServerPlayer) player, ResourceLocation.parse("minecraft:wandering_trader"))) {
            event.setCanceled(true);
        }
    }
}
