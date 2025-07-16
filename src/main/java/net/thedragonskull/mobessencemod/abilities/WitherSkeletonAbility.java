package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import static net.thedragonskull.mobessencemod.util.TotemUtils.hasTotemWithEssenceServer;

public class WitherSkeletonAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onApplyWither(LivingAttackEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        if (!(hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:wither_skeleton")))) return;
        if (event.getSource().is(DamageTypeTags.IS_PROJECTILE)) return;

        LivingEntity target = event.getEntity();

        if (player.getRandom().nextInt(5) == 0) {
            MobEffectInstance current = target.getEffect(MobEffects.WITHER);
            int newDuration = 100;

            if (current != null) {
                newDuration += current.getDuration();
            }

            event.getEntity().addEffect(new MobEffectInstance(MobEffects.WITHER, newDuration, 0));
        }
    }

    public static void onWitherAndFireHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:wither_skeleton"))) return;

        DamageSource source = event.getSource();

        if (source.is(DamageTypes.WITHER) || source.is(DamageTypeTags.IS_FIRE)) {
            float original = event.getAmount();
            event.setAmount(original * 0.5f);
        }
    }
}
