package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
    private static boolean isInShulkerMode = false;

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

            if (mc.level != null && !isInShulkerMode) {
                isInShulkerMode = true;
                mc.level.playLocalSound(mc.player.getX(), mc.player.getY(), mc.player.getZ(),
                        SoundEvents.SHULKER_CLOSE, SoundSource.PLAYERS, 2.0F, 1.0F, false);
            }

        } else if (wasInShulkerMode) {
            long window = mc.getWindow().getWindow();

            restoreIfPressed(mc.options.keyUp, window);
            restoreIfPressed(mc.options.keyDown, window);
            restoreIfPressed(mc.options.keyLeft, window);
            restoreIfPressed(mc.options.keyRight, window);
            restoreIfPressed(mc.options.keyJump, window);

            isInShulkerMode = false;
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

        if (player.getRandom().nextInt(5) == 0 && !event.getSource().is(DamageTypeTags.IS_PROJECTILE)) {
            ((LivingEntity) attacker).addEffect(new MobEffectInstance(MobEffects.LEVITATION, 100));
            player.level().playSound(null, player.blockPosition(), SoundEvents.SHULKER_SHOOT, SoundSource.PLAYERS,
                    2.0F, (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F + 1.0F);
        }

        player.level().playSound(null, player.blockPosition(), SoundEvents.SHULKER_HURT_CLOSED, SoundSource.PLAYERS, 1f, 1f);
    }


    public static void shulkerHurt(LivingAttackEvent event) {
        Entity sourceEntity = event.getSource().getEntity();
        Entity targetEntity = event.getEntity();

        //Deflect arrows
        if (targetEntity instanceof ServerPlayer player) {
            if (TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:shulker")) &&
                    player.isCrouching()) {

                if (event.getSource().getDirectEntity() instanceof AbstractArrow) {
                    event.setCanceled(true);

                    player.level().playSound(null, player.blockPosition(), SoundEvents.SHULKER_HURT_CLOSED,
                            SoundSource.PLAYERS, 1.0f, 1.0f);
                    return;
                }
            }
        }

        //Prevent attacking
        if (sourceEntity instanceof ServerPlayer player) {
            if (TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:shulker")) &&
                    player.isCrouching()) {

                event.setCanceled(true);

                player.level().playSound(null, player.blockPosition(), SoundEvents.SHULKER_CLOSE,
                        SoundSource.PLAYERS, 0.6f, 1.1f);
            }
        }
    }
}
