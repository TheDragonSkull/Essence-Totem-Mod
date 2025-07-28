package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class PandaAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onPandaEat(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();

        if (!(player instanceof ServerPlayer serverPlayer)) return;
        Level level = player.level();
        ItemStack item = event.getItemStack();

        if (!TotemUtils.hasTotemWithEssenceServer(serverPlayer, ResourceLocation.parse("minecraft:panda"))) return;

        if (!item.is(Items.BAMBOO)) return;

        if (serverPlayer.getHealth() < serverPlayer.getMaxHealth()) {
            serverPlayer.heal(1.0F);

            player.getCooldowns().addCooldown(Items.BAMBOO, 30);
            item.shrink(1);

            //player.displayClientMessage(Component.literal(String.valueOf(player.getHealth())), true);

            level.playSound(null, serverPlayer.blockPosition(), SoundEvents.PANDA_EAT, SoundSource.PLAYERS, 1.0F, 1.0F);
            ((ServerLevel) level).sendParticles(ParticleTypes.HEART,
                    serverPlayer.getX(), serverPlayer.getY() + 1.0, serverPlayer.getZ(),
                    15, 0.2, 0.3, 0.2, 0.01);
        }

        event.setCanceled(true);
    }

    public static void onPandaHit(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:panda"))) return;

        DamageSource source = event.getSource();
        Entity attacker = source.getEntity();

        if (!(attacker instanceof LivingEntity)) return;
        if (attacker.is(player)) return;

        if (player.getRandom().nextInt(3) != 0) return;

        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 0));
    }

    public static void onPandaEatVegetable(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ItemStack eaten = event.getItem();

        if (!eaten.isEdible()) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:panda"))) return;

        Item item = eaten.getItem();
        if (TotemUtils.isVegetable(item)) {
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 0));
        }
    }
}
