package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.thedragonskull.mobessencemod.capability.MobEssenceCapabilities;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class PlayerAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:player"))) return;

        player.getCapability(MobEssenceCapabilities.MOB_ESSENCE_CAP).ifPresent(cap -> {

            cap.setKeepInventory(true);
            cap.storeInventory(player.getInventory());
        });
    }

    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        System.out.println("was death");

        event.getOriginal().reviveCaps();

        event.getOriginal().getCapability(MobEssenceCapabilities.MOB_ESSENCE_CAP).ifPresent(oldCap -> {
            System.out.println("old cap");

            event.getEntity().getCapability(MobEssenceCapabilities.MOB_ESSENCE_CAP).ifPresent(newCap -> {
                System.out.println("new cap");

                CompoundTag tag = new CompoundTag();
                oldCap.writeToNBT(tag);
                newCap.readFromNBT(tag);

                if (newCap.shouldKeepInventory()) {
                    newCap.restoreInventory(event.getEntity().getInventory());
                }
            });
        });
    }

    public static void onPlayerDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        player.getCapability(MobEssenceCapabilities.MOB_ESSENCE_CAP).ifPresent(cap -> {
            if (cap.shouldKeepInventory()) {
                event.getDrops().clear();
            }
        });
    }
}
