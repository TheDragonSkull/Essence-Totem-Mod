package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.thedragonskull.mobessencemod.capability.MobEssenceCapabilities;
import net.thedragonskull.mobessencemod.mixin.BedBlockMixin;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    // KEEP INVENTORY
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:player"))) return;
        if (player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) return;

        if (player.getRandom().nextInt(10) != 0) return;

        player.getCapability(MobEssenceCapabilities.MOB_ESSENCE_CAP).ifPresent(cap -> {
            cap.setKeepInventory(true);
            cap.storeInventory(player.getInventory());
        });
    }

    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        event.getOriginal().reviveCaps();

        event.getOriginal().getCapability(MobEssenceCapabilities.MOB_ESSENCE_CAP).ifPresent(oldCap -> {

            event.getEntity().getCapability(MobEssenceCapabilities.MOB_ESSENCE_CAP).ifPresent(newCap -> {
                CompoundTag tag = new CompoundTag();
                oldCap.writeToNBT(tag);
                newCap.readFromNBT(tag);
            });
        });
    }

    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        player.getCapability(MobEssenceCapabilities.MOB_ESSENCE_CAP).ifPresent(cap -> {
            if (cap.shouldKeepInventory()) {
                cap.restoreInventory(player.getInventory());

                player.connection.send(new ClientboundSoundPacket(
                        BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.PLAYER_LEVELUP),
                        SoundSource.PLAYERS, player.getX(), player.getY(), player.getZ(), 1.0f, 1.0f,
                        player.level().getRandom().nextLong())
                );

                player.displayClientMessage(Component.literal(player.getName().getString() + " used /gamerule keepInventory true!")
                        .withStyle(ChatFormatting.GRAY), false);
            }
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

    // TP & LAG
    private static final Map<UUID, BlockPos> lastSafePosition = new HashMap<>();

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:player"))) return;

        if (player.onGround() && !player.isSpectator() && player.fallDistance < 3.0f) {
            BlockPos below = player.blockPosition().below();
            if (!player.level().getBlockState(below).isAir()) {
                lastSafePosition.put(player.getUUID(), below.immutable());
            }
        }
    }

    public static void onPlayerTp(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:player"))) return;

        DamageSource source = event.getSource();

        boolean isFallingVoid = source.is(DamageTypes.FELL_OUT_OF_WORLD);
        boolean isFalling = source.is(DamageTypes.FALL);

        float damage = event.getAmount();
        float currentHealth = player.getHealth();

        if ((isFalling || isFallingVoid) && damage >= currentHealth) {
            if (player.getRandom().nextInt(6) == 0) {
                BlockPos safePos = lastSafePosition.get(player.getUUID());

                if (safePos != null) {
                    event.setCanceled(true);

                    player.teleportTo(safePos.getX() + 0.5, safePos.getY() + 1, safePos.getZ() + 0.5);
                    player.resetFallDistance();

                    player.connection.send(new ClientboundSoundPacket(
                            BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.PLAYER_LEVELUP),
                            SoundSource.PLAYERS, player.getX(), player.getY(), player.getZ(), 1.0f, 1.0f,
                            player.level().getRandom().nextLong())
                    );

                    String message;
                    if (isFallingVoid) {
                        message = player.getName().getString() + " used /tp " + player.getName().getString() +
                                " " + safePos.getX() + " " + (safePos.getY() + 1) + " " + safePos.getZ() + "!";
                    } else {
                        message = player.getName().getString() + " has poor connection!";
                    }

                    player.displayClientMessage(Component.literal(message).withStyle(ChatFormatting.GRAY), false);
                }
            }

        }
    }

    // GIVE CAKE
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:player"))) return;

        if (player.getRandom().nextInt(20) == 0) {
            ItemStack cake = new ItemStack(Items.CAKE);

            boolean added = player.getInventory().add(cake);

            if (!added) {
                player.drop(cake, false);
            }

            player.connection.send(new ClientboundSoundPacket(
                    BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.PLAYER_LEVELUP),
                    SoundSource.PLAYERS, player.getX(), player.getY(), player.getZ(), 1.0f, 1.0f,
                    player.level().getRandom().nextLong())
            );

            String message = player.getName().getString() + " used /give " + player.getName().getString() + " cake";
            player.displayClientMessage(Component.literal(message).withStyle(ChatFormatting.GRAY), false);
        }
    }

    // WEATHER RAIN
    public static void onFish(ItemFishedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:player"))) return;

        ServerLevel level = (ServerLevel) player.level();

        if (player.getRandom().nextInt(4) == 0) {
            if (!level.isRaining() && level.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)) {
                level.setWeatherParameters(0, 6000, true, false);

                player.connection.send(new ClientboundSoundPacket(
                        BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.PLAYER_LEVELUP),
                        SoundSource.PLAYERS, player.getX(), player.getY(), player.getZ(), 1.0f, 1.0f,
                        player.level().getRandom().nextLong())
                );

                player.displayClientMessage(Component.literal(
                        player.getName().getString() + " used /weather rain"
                ).withStyle(ChatFormatting.GRAY), false);
            }
        }
    }

    // TIME SET NIGHT
    //BedBlockMixin
}
