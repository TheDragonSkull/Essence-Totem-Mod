package net.thedragonskull.mobessencemod.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BedBlock.class)
public abstract class BedBlockMixin {

    @Inject(
            method = "use",
            at = @At("HEAD")
    )
    private void mobessence$forceSleepIfTooTired(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                                 BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (level.isClientSide) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(serverPlayer, ResourceLocation.parse("minecraft:player"))) return;
        if (!level.dimensionType().natural()) return;
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (!level.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) return;

        int ticksSinceRest = serverPlayer.getStats().getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
        boolean isDay = level.getDayTime() % 24000 < 12542;

        if (ticksSinceRest >= 24000 && isDay) {
            long day = level.getDayTime() / 24000;
            serverLevel.setDayTime(day * 24000 + 13000); // Night

            serverPlayer.connection.send(new ClientboundSoundPacket(
                    BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.PLAYER_LEVELUP),
                    SoundSource.PLAYERS, player.getX(), player.getY(), player.getZ(), 1.0f, 1.0f,
                    player.level().getRandom().nextLong())
            );

            serverPlayer.displayClientMessage(
                    Component.literal(serverPlayer.getName().getString() + " used /time set night")
                            .withStyle(ChatFormatting.GRAY),
                    false
            );

        }
    }
}

