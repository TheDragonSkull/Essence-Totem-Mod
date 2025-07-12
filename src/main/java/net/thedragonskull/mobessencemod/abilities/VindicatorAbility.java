package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import static net.thedragonskull.mobessencemod.util.TotemUtils.hasTotemWithEssenceServer;

public class VindicatorAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void vindicatorAxeSpeed(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        if (!(event.player instanceof ServerPlayer player)) return;

        if (!hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:vindicator"))) return;
        if (player.isPassenger()) return;

        ItemStack main = player.getMainHandItem();
        if (!main.is(ItemTags.AXES)) return;

        boolean hasNearbyLivingEntity = !player.level().getEntitiesOfClass(
                net.minecraft.world.entity.LivingEntity.class,
                player.getBoundingBox().inflate(6),
                entity -> entity.isAlive() && entity != player
        ).isEmpty();

        if (hasNearbyLivingEntity) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2, 1, false, false, false));
        }
    }

    public static void onRideRavager(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        Entity target = event.getTarget();

        if (target instanceof Ravager ravager && TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:vindicator"))) {

            if (!ravager.isPassenger()) {
                player.startRiding(ravager);
                event.setCanceled(true);
            }
        }
    }

    public static void onVindicatorAttack(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        Entity owner = source.getEntity();

        if (!(owner instanceof ServerPlayer player)) return;

        if (!(player.getVehicle() instanceof Ravager)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:vindicator"))) return;

        ItemStack main = player.getMainHandItem();
        if (!main.is(ItemTags.AXES)) return;

        event.setAmount(event.getAmount() * 1.5F);
    }
}
