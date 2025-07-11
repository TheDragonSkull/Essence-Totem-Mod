package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class StrayAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void straySlowArrow(ArrowLooseEvent event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        ItemStack bow = event.getBow();

        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(serverPlayer, ResourceLocation.parse("minecraft:stray"))) return;
        if (!event.hasAmmo()) return;

        ItemStack ammo = player.getProjectile(bow);
        if (!ammo.is(Items.ARROW)) return;

        int charge = event.getCharge();
        charge = (int) (charge * 1.75F);

        float velocity = BowItem.getPowerForTime(charge);
        if (velocity < 0.1F) return;

        if (player.getRandom().nextInt(3) != 0) return;

        Arrow arrow = new Arrow(level, player);
        arrow.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 0));
        arrow.pickup = AbstractArrow.Pickup.DISALLOWED;

        arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, velocity * 3.0F, 1.0F);
        level.addFreshEntity(arrow);

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS,
                1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 1 * 0.5F);

        event.setCanceled(true);
    }


    // CommonAbilityUtils.onFreezingHurt
    // CommonAbilityUtils.onArrowLoose
}
