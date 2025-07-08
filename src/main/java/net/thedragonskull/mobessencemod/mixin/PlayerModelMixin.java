package net.thedragonskull.mobessencemod.mixin;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin<T extends LivingEntity> extends HumanoidModel<T> {

    @Shadow @Final public ModelPart rightSleeve;

    @Shadow @Final public ModelPart leftSleeve;

    @Shadow @Final public ModelPart leftPants;

    @Shadow @Final public ModelPart rightPants;

    public PlayerModelMixin(ModelPart pRoot) {
        super(pRoot);
    }

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("HEAD"), cancellable = true)
    private void disableAnimations(T entity, float f1, float f2, float f3, float f4, float f5, CallbackInfo ci) {
        if (entity instanceof AbstractClientPlayer player) {
            if (TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:armor_stand"))) {

                this.rightArm.setPos(-6.0F, 2.0F, 0.0F);
                this.leftArm.setPos(6.0F, 2.0F, 0.0F);

                this.rightArm.xRot = 0;
                this.rightArm.yRot = 0;
                this.rightArm.zRot = (float) Math.toRadians(90);

                this.leftArm.xRot = 0;
                this.leftArm.yRot = 0;
                this.leftArm.zRot = (float) Math.toRadians(-90);

                this.rightLeg.xRot = 0;
                this.leftLeg.xRot = 0;

                this.head.xRot = 0;
                this.head.yRot = 0;

                this.leftSleeve.copyFrom(this.leftArm);
                this.rightSleeve.copyFrom(this.rightArm);

                this.leftPants.copyFrom(this.leftLeg);
                this.rightPants.copyFrom(this.rightLeg);

                this.hat.copyFrom(this.head);
                ci.cancel();
            }
        }
    }
}

