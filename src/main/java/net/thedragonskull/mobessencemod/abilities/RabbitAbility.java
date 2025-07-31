package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.thedragonskull.mobessencemod.item.ModItems;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;

public class RabbitAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onBunnyFrenzy(LivingDeathEvent event) {
        DamageSource source = event.getSource();
        Entity attacker = source.getEntity();

        if (!(attacker instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:killer_bunny"))) return;

        if (event.getSource().is(DamageTypeTags.IS_PROJECTILE)) return;

        if (player.getRandom().nextInt(4) == 0) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 15, 0));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 15, 0));

            player.level().playSound(null, player.blockPosition(), SoundEvents.RABBIT_ATTACK, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    public static void onKillerBunnyTransform(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;

        ResourceLocation rabbitId = ResourceLocation.parse("minecraft:rabbit");
        ResourceLocation killerId = ResourceLocation.parse("minecraft:killer_bunny");

        Optional<SlotResult> slotOpt = TotemUtils.findTotemWithEssenceServer(player, rabbitId);
        if (slotOpt.isEmpty()) return;
        if (!(player.getRandom().nextFloat() < 0.01)) return;

        ItemStack newStack = new ItemStack(ModItems.TOTEM_OF_ESSENCE.get());
        TotemUtils.setEssence(newStack, killerId);

        SlotResult result = slotOpt.get();
        String slotId = result.slotContext().identifier();
        int index = result.slotContext().index();

        CuriosApi.getCuriosInventory(player).ifPresent(inv -> {
            inv.setEquippedCurio(slotId, index, newStack);
        });

        ServerLevel level = (ServerLevel) player.level();
        level.playSound(null, player.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.5f, 1.0f);
        level.sendParticles(ParticleTypes.FLAME, player.getX(), player.getY() + 1.0, player.getZ(), 20, 0.4, 0.5, 0.4, 0.01);

        player.displayClientMessage(Component.literal("Your totem trembles as it twists into something... darker")
                .withStyle(ChatFormatting.DARK_PURPLE), true);

    }

    public static void onTuberFind(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;
        if (player.isCreative()) return;

        if (!(TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:rabbit")) ||
                TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:killer_bunny")))) return;

        BlockState state = event.getState();

        if (!(state.is(Blocks.GRASS) || state.is(Blocks.TALL_GRASS))) return;

        if (player.getRandom().nextFloat() < 0.1f) {
            ItemStack drop = switch (player.getRandom().nextInt(3)) {
                case 0 -> new ItemStack(Items.CARROT);
                case 1 -> new ItemStack(Items.POTATO);
                default -> new ItemStack(Items.BEETROOT);
            };

            ItemEntity entity = new ItemEntity(
                    player.level(),
                    event.getPos().getX() + 0.5,
                    event.getPos().getY() + 0.5,
                    event.getPos().getZ() + 0.5,
                    drop
            );

            player.level().addFreshEntity(entity);
        }
    }

    // RabbitJumpMixin
}
