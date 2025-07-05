package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;

public class ZombieAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        Optional<SlotResult> zombieTotem = TotemUtils.findTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:zombie"));

        if (zombieTotem.isEmpty()) return;

        DamageSource source = event.getSource();
        Entity attacker = source.getEntity();

        if (!(attacker instanceof LivingEntity)) return;

        event.setCanceled(true);

        player.setHealth(1.0F);

        ItemStack totemStack = zombieTotem.get().stack();
        CompoundTag tag = totemStack.getTag();
        if (tag != null && tag.contains("Essence")) {
            tag.remove("Essence");
        }

        // VFX
        ServerLevel serverLevel = (ServerLevel) player.level();

        double x = player.getX();
        double y = player.getY() + player.getBbHeight() / 2.0;
        double z = player.getZ();

        serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                x, y, z,
                40, 0.5, 0.5, 0.5, 0.02);

        serverLevel.sendParticles(
                ParticleTypes.SOUL,
                x, y, z,
                20,
                0.3, 0.5, 0.3,
                0.01
        );

        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.5F, 0.7F);

        player.displayClientMessage(Component.literal("You claw your way back from death...").withStyle(ChatFormatting.DARK_GREEN), true);
    }
}
