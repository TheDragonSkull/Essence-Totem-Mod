package net.thedragonskull.mobessencemod.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.thedragonskull.mobessencemod.MobEssenceMod;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class VillagerNoseLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private final VillagerNoseModel villagerNoseModel;
    private static final ResourceLocation NOSE_TEXTURE = ResourceLocation.fromNamespaceAndPath(MobEssenceMod.MOD_ID, "textures/misc/villager_nose.png");
    private static final ResourceLocation ZOMBIE_NOSE_TEXTURE = ResourceLocation.fromNamespaceAndPath(MobEssenceMod.MOD_ID, "textures/misc/zombie_villager_nose.png");

    public VillagerNoseLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> pRenderer, ModelPart villagerNosePart) {
        super(pRenderer);
        this.villagerNoseModel = new VillagerNoseModel(villagerNosePart);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight,
                       AbstractClientPlayer player, float pLimbSwing, float pLimbSwingAmount,
                       float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {

        boolean hasVillager = TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:villager"));
        boolean hasZombie = TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:zombie_villager"));

        if (!hasVillager && !hasZombie) return;
        if (player.isInvisible()) return;

        ResourceLocation texture = hasVillager ? NOSE_TEXTURE : ZOMBIE_NOSE_TEXTURE;
        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(texture));

        poseStack.pushPose();
        this.getParentModel().head.translateAndRotate(poseStack);
        this.villagerNoseModel.setupAnim(player, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        poseStack.translate(0.0F, -1.42F, -0.3F);
        this.villagerNoseModel.renderToBuffer(poseStack, vertexconsumer, pPackedLight, LivingEntityRenderer.getOverlayCoords(player, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();

    }
}
