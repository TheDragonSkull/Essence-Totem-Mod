package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.*;

public class IronGolemAbility implements IMobAbility {

    private static final Set<UUID> fracturedPlayers = new HashSet<>();
    private static final Map<UUID, Long> fractureExpiry = new HashMap<>();

    public static void onGolemDefense(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:iron_golem"))) return;

        UUID id = player.getUUID();

        // double damage
        if (fracturedPlayers.contains(id)) {
            event.setAmount(event.getAmount() * 2f);
            fracturedPlayers.remove(id);
            fractureExpiry.remove(id);
            player.level().playSound(null, player.blockPosition(), SoundEvents.IRON_GOLEM_HURT, SoundSource.PLAYERS, 1f, 1f);
            return;
        }

        // fracture
        if (event.getSource().getEntity() instanceof LivingEntity && player.level().getRandom().nextInt(4) == 0) {
            event.setCanceled(true);
            fracturedPlayers.add(id);
            fractureExpiry.put(id, player.level().getGameTime() + 100);

            player.level().playSound(null, player.blockPosition(), SoundEvents.IRON_GOLEM_DAMAGE, SoundSource.PLAYERS, 1f, 1f);
        }
    }

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
        UUID id = player.getUUID();
        if (fractureExpiry.containsKey(id)) {
            if (player.level().getGameTime() >= fractureExpiry.get(id)) {
                fracturedPlayers.remove(id);
                fractureExpiry.remove(id);
            }
        }
    }


    public static void onGolemAttack(LivingAttackEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:iron_golem"))) return;

        if (event.getSource().is(DamageTypeTags.IS_PROJECTILE)) return;
        LivingEntity target = event.getEntity();

        if (player.level().getRandom().nextInt(4) == 0) {
            double resistance = target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
            double factor = Math.max(0.0, 1.0 - resistance);

            player.getServer().execute(() -> {
                Vec3 motion = target.getDeltaMovement().add(0, 0.6 * factor, 0);
                target.setDeltaMovement(motion);
                target.hurtMarked = true;

                target.level().playSound(null, target.blockPosition(), SoundEvents.IRON_GOLEM_ATTACK, SoundSource.PLAYERS, 1f, 1f);
            });
        }
    }

    public static void onUseIron(PlayerInteractEvent.RightClickItem event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:iron_golem"))) return;

        Level level = player.level();
        ItemStack item = event.getItemStack();
        if (!item.is(Items.IRON_INGOT)) return;

        if (player.getHealth() < player.getMaxHealth()) {
            player.heal(1.0F);
            player.getCooldowns().addCooldown(Items.IRON_INGOT, 30);
            item.shrink(1);

            player.level().playSound(null, player.blockPosition(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.PLAYERS, 1f, 1f);
            ((ServerLevel) level).sendParticles(ParticleTypes.HEART,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    15, 0.2, 0.3, 0.2, 0.01);

            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }
}
