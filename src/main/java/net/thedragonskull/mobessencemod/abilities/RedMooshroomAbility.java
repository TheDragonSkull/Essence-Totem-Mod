package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import static net.thedragonskull.mobessencemod.util.TotemUtils.isBadFood;

public class RedMooshroomAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onEat(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:red_mooshroom"))) return;

        ItemStack stack = event.getItem();
        Item item = stack.getItem();

        if (!(item.isEdible())) return;

        if (isBadFood(item)) return;

        if (player.getRandom().nextInt(3) == 0) {
            player.getFoodData().eat(2, 0.0F);

            ((ServerLevel) player.level()).sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    6, 0.3, 0.5, 0.3, 0.05);

            player.displayClientMessage(
                    Component.literal("Mushroom nutrients enhanced your meal!"),
                    true
            );
        }
    }

}
