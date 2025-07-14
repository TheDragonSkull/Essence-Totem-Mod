package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import static net.thedragonskull.mobessencemod.util.TotemUtils.restoreIfPressed;

public class ShulkerAbility implements IMobAbility {

    private static boolean wasInShulkerMode = false;

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onShulkerStill(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        boolean isShulkerMode = mc.player.isCrouching()
                && TotemUtils.hasTotemWithEssenceClient(mc.player, ResourceLocation.parse("minecraft:shulker"));

        if (isShulkerMode) {
            mc.player.input.leftImpulse = 0;
            mc.player.input.forwardImpulse = 0;

            mc.options.keyUp.setDown(false);
            mc.options.keyDown.setDown(false);
            mc.options.keyLeft.setDown(false);
            mc.options.keyRight.setDown(false);
            mc.options.keyJump.setDown(false);
        } else if (wasInShulkerMode) {
            long window = mc.getWindow().getWindow();

            restoreIfPressed(mc.options.keyUp, window);
            restoreIfPressed(mc.options.keyDown, window);
            restoreIfPressed(mc.options.keyLeft, window);
            restoreIfPressed(mc.options.keyRight, window);
            restoreIfPressed(mc.options.keyJump, window);
        }

        wasInShulkerMode = isShulkerMode;
    }

    public static void onShulkerTank(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        boolean hasTotem = TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:shulker"));
        if (!hasTotem || !player.isCrouching()) return;

        event.setAmount(event.getAmount() * 0.2F);
        Entity attacker = event.getSource().getEntity();

        if (!(attacker instanceof LivingEntity)) return;

        if (player.getRandom().nextInt(5) == 0) {
            Vec3 eyes = player.getEyePosition();
            ShulkerBullet bullet = new ShulkerBullet(player.level(), player, attacker, Direction.Axis.Y);
            bullet.setPos(eyes.x, eyes.y, eyes.z);
            player.level().addFreshEntity(bullet);
            player.playSound(SoundEvents.SHULKER_SHOOT, 2.0F, (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F + 1.0F);
        }

        player.level().playSound(null, player.blockPosition(), SoundEvents.SHULKER_HURT_CLOSED, SoundSource.PLAYERS, 1f, 1f);
    }


    public static void shulkerHurt(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:shulker"))) return;

        DamageSource source = event.getSource();

        if (source.getDirectEntity() instanceof AbstractArrow && player.getRandom().nextInt(2) == 0) {
            event.setCanceled(true);

            player.level().playSound(null, player.blockPosition(), SoundEvents.SHULKER_HURT_CLOSED,
                    SoundSource.PLAYERS, 1.0f, 1.0f);
        }
    }
}
