package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;


public class EvokerAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:evoker"))) return;

        DamageSource source = event.getSource();
        Entity attackerEntity = source.getEntity();

        if (!(attackerEntity instanceof LivingEntity attacker)) return;

        RandomSource random = player.level().random;
        Entity directEntity = source.getDirectEntity();

        if (directEntity instanceof Projectile) {
            if (random.nextInt(3) == 0) {
                player.level().playSound(null, player.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0f, 1.0f);
                player.displayClientMessage(Component.literal("The ground cracks with arcane wrath!").withStyle(ChatFormatting.DARK_PURPLE), true);
                summonEvokerFangs(player, attacker);
            }
        } else if (attacker instanceof LivingEntity) {
            if (random.nextInt(6) == 0) {
                player.level().playSound(null, player.blockPosition(), SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.PLAYERS, 1.0f, 1.0f);
                player.displayClientMessage(Component.literal("Phantom blades heed your pain, screaming for vengeance.").withStyle(ChatFormatting.DARK_AQUA), true);
                summonVex(player, attacker);
            }
        }
    }

    private static void summonVex(ServerPlayer player, LivingEntity target) {
        BlockPos blockpos = player.blockPosition().offset(-2 + player.level().random.nextInt(5), 1, -2 + player.level().random.nextInt(5));
        Vex vex = EntityType.VEX.create(player.level());
        if (vex != null) {
            vex.moveTo(blockpos, 0,0);

            vex.goalSelector.addGoal(1, new MeleeAttackGoal(vex, 1.0D, true));
            vex.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(vex, LivingEntity.class, 10, true, false,
                    entity -> entity == target));
            
            vex.setLimitedLife(600);
            vex.setTarget(target);
            vex.setAggressive(true);
            vex.setPersistenceRequired();
            vex.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
            player.level().addFreshEntity(vex);
        }
    }

    private static void summonEvokerFangs(ServerPlayer player, Entity target) {
        EvokerFangs fangs = EntityType.EVOKER_FANGS.create(player.level());
        if (fangs != null) {
            fangs.moveTo(target.getX(), target.getY(), target.getZ(), player.getYRot(), 0);
            fangs.setOwner(player);
            player.level().addFreshEntity(fangs);
        }
    }

    public static void onVexTarget(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Vex)) return;
        if (!(event.getNewTarget() instanceof Player player)) return;

        if (TotemUtils.hasTotemWithEssenceServer((ServerPlayer) player, ResourceLocation.parse("minecraft:evoker"))) {
            event.setNewTarget(null);
        }
    }
}
