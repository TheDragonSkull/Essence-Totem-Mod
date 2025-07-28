package net.thedragonskull.mobessencemod.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSkeleton.class)
public abstract class SkeletonMixin extends PathfinderMob {

    protected SkeletonMixin(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void creeperAvoidPlayer(CallbackInfo ci) {
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(
                this,
                Player.class,
                6.0F,
                1.0D,
                1.2D,
                player -> TotemUtils.hasTotemWithEssenceServer((ServerPlayer)player, ResourceLocation.parse("minecraft:wolf"))
        ));
    }


}
