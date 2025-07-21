package net.thedragonskull.mobessencemod.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public abstract class PlayerEnergySwirlLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    public PlayerEnergySwirlLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    protected abstract boolean shouldRender(T entity);

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float headPitch) {

        if (!shouldRender(entity)) return;

        float f = (float) entity.tickCount + partialTicks;
        EntityModel<T> model = this.model();
        model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
        this.getParentModel().copyPropertiesTo(model);

        VertexConsumer vertexconsumer = buffer.getBuffer(
                RenderType.energySwirl(getTextureLocation(), xOffset(f) % 1.0F, f * 0.01F % 1.0F)
        );
        model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY,
                0.5F, 0.5F, 0.5F, 1.0F);
    }

    protected abstract float xOffset(float tickCount);

    protected abstract ResourceLocation getTextureLocation();

    protected abstract EntityModel<T> model();
}
