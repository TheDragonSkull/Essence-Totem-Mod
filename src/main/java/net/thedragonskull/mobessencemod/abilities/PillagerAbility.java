package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class PillagerAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onRideRavager(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        Entity target = event.getTarget();

        if (target instanceof Ravager ravager && TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:pillager"))) {

            if (!ravager.isPassenger()) {
                player.startRiding(ravager);
                event.setCanceled(true);
            }
        }
    }

    public static void onPillagerShoot(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        Entity direct = source.getDirectEntity();
        Entity owner = source.getEntity();

        if (!(direct instanceof AbstractArrow arrow)) return;
        if (!(owner instanceof ServerPlayer player)) return;

        if (!(player.getVehicle() instanceof Ravager)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:pillager"))) return;

        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();

        boolean isCrossbowEquipped = main.getItem() instanceof CrossbowItem || off.getItem() instanceof CrossbowItem;
        if (!isCrossbowEquipped) return;

        event.setAmount(event.getAmount() * 1.5F);
    }
}
