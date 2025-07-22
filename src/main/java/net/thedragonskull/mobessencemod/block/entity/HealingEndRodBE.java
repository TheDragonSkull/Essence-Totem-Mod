package net.thedragonskull.mobessencemod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndRodBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.thedragonskull.mobessencemod.particle.ModParticles;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.List;

public class HealingEndRodBE extends BlockEntity {

    public HealingEndRodBE(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.HEALING_END_ROD_BE.get(), pPos, pBlockState);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        ServerLevel serverLevel = (ServerLevel) level;

        double x = worldPosition.getX() + 0.5;
        double y = worldPosition.getY() + 0.5;
        double z = worldPosition.getZ() + 0.5;

        if (level.getGameTime() % 2 == 0) {
            serverLevel.sendParticles(
                    ModParticles.HEALING_GLITTER.get(),
                    x, y, z,
                    1,
                    0.1, 0.25, 0.1,
                    0.1
            );
        }

        AABB area = new AABB(worldPosition).inflate(5);

        List<LivingEntity> targets = serverLevel.getEntitiesOfClass(LivingEntity.class, area, entity ->
                (entity instanceof ServerPlayer ||
                        (entity instanceof TamableAnimal tamable && tamable.isTame()))
        );

        for (LivingEntity entity : targets) {
            MobEffectInstance current = entity.getEffect(MobEffects.REGENERATION);
            if (current == null || current.getDuration() <= 10) {
                entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0, true, false, true));
            }
        }

        boolean hasNearbyPlayer = !serverLevel.getEntitiesOfClass(ServerPlayer.class, area,
                p -> TotemUtils.hasTotemWithEssenceServer(p, ResourceLocation.parse("minecraft:ender_dragon"))
        ).isEmpty();

        if (!hasNearbyPlayer) {
            BlockState revert = Blocks.END_ROD.defaultBlockState()
                    .setValue(EndRodBlock.FACING, getBlockState().getValue(EndRodBlock.FACING));
            level.setBlockAndUpdate(worldPosition, revert);
        }
    }
}
