package net.thedragonskull.mobessencemod.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EndRodBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.thedragonskull.mobessencemod.block.entity.HealingEndRodBE;
import net.thedragonskull.mobessencemod.particle.ModParticles;
import org.jetbrains.annotations.Nullable;

public class HealingEndRod extends EndRodBlock implements EntityBlock {

    public HealingEndRod(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        Direction direction = pState.getValue(FACING);

        double d0 = (double)pPos.getX() + 0.55D - (double)(pRandom.nextFloat() * 0.1F);
        double d1 = (double)pPos.getY() + 0.55D - (double)(pRandom.nextFloat() * 0.1F);
        double d2 = (double)pPos.getZ() + 0.55D - (double)(pRandom.nextFloat() * 0.1F);

        double d3 = 0.4F - (pRandom.nextFloat() + pRandom.nextFloat()) * 0.4F;
        //pLevel.addParticle(ModParticles.HEALING_GLITTER.get(), d0 + (double)direction.getStepX() * d3, d1 + (double)direction.getStepY() * d3, d2 + (double)direction.getStepZ() * d3, pRandom.nextGaussian() * 0.1D, pRandom.nextGaussian() * 0.1D, pRandom.nextGaussian() * 0.1D);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new HealingEndRodBE(pPos, pState);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : (lvl, pos, st, be) -> {
            if (be instanceof HealingEndRodBE rod) rod.tick();
        };
    }
}
