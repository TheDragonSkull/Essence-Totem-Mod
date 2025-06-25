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
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;

public class ZombieAbility implements  IMobAbility{

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        Optional<SlotResult> zombieTotem = TotemUtils.findTotemWithEssence(player, ResourceLocation.parse("minecraft:zombie"));

        if (zombieTotem.isEmpty()) return;

        event.setCanceled(true);

        player.setHealth(1.0F);

        ItemStack totemStack = zombieTotem.get().stack();
        CompoundTag tag = totemStack.getTag();
        if (tag != null && tag.contains("Essence")) {
            tag.remove("Essence");
        }

        // VFX
        ServerLevel serverLevel = (ServerLevel) player.level();

        serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                player.getX(),
                player.getY() + player.getBbHeight() / 2,
                player.getZ(),
                40, 0.5, 0.5, 0.5, 0.02);

        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.5F, 0.7F);

        player.displayClientMessage(Component.literal("You claw your way back from death...").withStyle(ChatFormatting.DARK_GREEN), true);
    }
}
