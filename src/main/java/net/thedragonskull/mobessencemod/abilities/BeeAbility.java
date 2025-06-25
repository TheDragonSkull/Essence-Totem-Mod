package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;

public class BeeAbility implements IMobAbility{

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (event.getSource().is(DamageTypeTags.IS_PROJECTILE)) return;
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;

        Optional<SlotResult> beeTotem = TotemUtils.findTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:bee"));

        if (beeTotem.isEmpty()) return;

        Vec3 lookVec = player.getLookAngle().normalize();
        Vec3 attackVec = attacker.position().subtract(player.position()).normalize();

        double dot = lookVec.dot(attackVec);

        if (dot < -0.5) {
            attacker.hurt(attacker.level().damageSources().sting(player), 1);
            attacker.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0));
        }
    }
}
