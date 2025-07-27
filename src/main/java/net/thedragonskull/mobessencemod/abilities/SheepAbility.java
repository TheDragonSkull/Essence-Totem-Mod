package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class SheepAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:sheep"))) return;

        DamageSource source = event.getSource();

        String id = source.getMsgId();
        boolean isMelee = id.equals("player") || id.equals("mob");
        boolean isProjectile = source.is(DamageTypeTags.IS_PROJECTILE);
        boolean isCactus = id.equals("cactus");
        boolean isFall = source.is(DamageTypeTags.IS_FALL);

        if (!(isMelee || isProjectile || isCactus || isFall)) return;

        if (player.getRandom().nextInt(3) == 0) {
            float original = event.getAmount();
            float reduced = original * 0.5F;

            event.setAmount(reduced);

            for (int i = 0; i < 5; i++) {
                player.level().playSound(null, player.blockPosition(), SoundEvents.WOOL_PLACE, SoundSource.PLAYERS, 10.0F, 1.2F);
            }

            ((ServerLevel) player.level()).sendParticles(ParticleTypes.POOF,
                    player.getX(), player.getY() + 1, player.getZ(),
                    6, 0.3, 0.5, 0.3, 0.02);

            player.displayClientMessage(
                    Component.literal("Your fluffy aura cushions the blow!"),
                    true
            );
        }

        //player.displayClientMessage(Component.literal(String.valueOf(event.getAmount())), false);
    }
}
