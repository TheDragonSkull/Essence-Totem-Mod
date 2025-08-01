package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.UUID;

public class PiglinBruteAbility implements IMobAbility {

    private static final UUID PIGLIN_BRUTE_ATTACK_SPEED_UUID = UUID.fromString("a8b90a2b-f84c-4c70-8b8b-7717e8fcf6fd");

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
        ItemStack held = player.getMainHandItem();
        if (!(held.getItem() instanceof AxeItem)) return;

        AttributeInstance attr = player.getAttribute(Attributes.ATTACK_SPEED);
        if (attr == null) return;

        attr.removeModifier(PIGLIN_BRUTE_ATTACK_SPEED_UUID);
        boolean halfHP = player.getHealth() < player.getMaxHealth() / 2.0;

        // ATK SPEED
        double multiplier = halfHP ? (held.is(Items.GOLDEN_AXE) ? 1.5 : 1.2) : 1.0;
        if (multiplier > 1.0) {
            double base = attr.getBaseValue();
            AttributeModifier mod = new AttributeModifier(
                    PIGLIN_BRUTE_ATTACK_SPEED_UUID,
                    "BruteSpeedBoost",
                    base * (multiplier - 1.0),
                    AttributeModifier.Operation.ADDITION
            );
            attr.addTransientModifier(mod);
        }

    }

    public static void onAttack(LivingAttackEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:piglin_brute"))) return;

        if (!(player.getMainHandItem().getItem() instanceof AxeItem)) return;
        if (player.getAttackStrengthScale(0.0F) < 1.0F) return;

        LivingEntity target = event.getEntity();

        if (player.level().getRandom().nextInt(6) == 0) {
            ItemStack targetWeapon = target.getMainHandItem();
            if (!targetWeapon.isEmpty()) {
                target.spawnAtLocation(targetWeapon);
                target.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            }
        }

    }

    public static void onBruteDoubleDamage(LivingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:piglin_brute"))) return;

        ItemStack weapon = player.getMainHandItem();
        if (!weapon.is(Items.GOLDEN_AXE)) return;

        boolean lowHealth = player.getHealth() < player.getMaxHealth() / 2.0;

        if (lowHealth) {
            float original = event.getAmount();
            event.setAmount(original * 2.0f);
        }

    }

    //DiggerItemMixin
}
