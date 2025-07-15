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

import java.util.Map;

public class VillagerNoseLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private final VillagerNoseModel villagerNoseModel;
    private final WitchNoseModel witchNoseModel;

    private static final ResourceLocation VILLAGER_NOSE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MobEssenceMod.MOD_ID, "textures/misc/villager_nose.png");
    private static final ResourceLocation ZOMBIE_NOSE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MobEssenceMod.MOD_ID, "textures/misc/zombie_villager_nose.png");
    private static final ResourceLocation ILLAGER_NOSE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MobEssenceMod.MOD_ID, "textures/misc/illager_nose.png");
    private static final ResourceLocation WITCH_NOSE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MobEssenceMod.MOD_ID, "textures/misc/witch_nose.png");
    private static final ResourceLocation GOLEM_NOSE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MobEssenceMod.MOD_ID, "textures/misc/iron_golem_nose.png");

    private static final Map<ResourceLocation, ResourceLocation> NOSE_TEXTURES = Map.ofEntries(
            Map.entry(ResourceLocation.parse("minecraft:villager"), VILLAGER_NOSE_TEXTURE),
            Map.entry(ResourceLocation.parse("minecraft:wandering_trader"), VILLAGER_NOSE_TEXTURE),
            Map.entry(ResourceLocation.parse("minecraft:zombie_villager"), ZOMBIE_NOSE_TEXTURE),
            Map.entry(ResourceLocation.parse("minecraft:evoker"), ILLAGER_NOSE_TEXTURE),
            Map.entry(ResourceLocation.parse("minecraft:pillager"), ILLAGER_NOSE_TEXTURE),
            Map.entry(ResourceLocation.parse("minecraft:vindicator"), ILLAGER_NOSE_TEXTURE),
            Map.entry(ResourceLocation.parse("minecraft:iron_golem"), GOLEM_NOSE_TEXTURE)
    );

    public VillagerNoseLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> pRenderer, ModelPart villagerNosePart, ModelPart witchNosePart) {
        super(pRenderer);
        this.villagerNoseModel = new VillagerNoseModel(villagerNosePart);
        this.witchNoseModel = new WitchNoseModel(witchNosePart);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight,
                       AbstractClientPlayer player, float pLimbSwing, float pLimbSwingAmount,
                       float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {

        if (player.isInvisible()) return;

        if (TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:witch"))) {
            VertexConsumer vertexConsumer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(WITCH_NOSE_TEXTURE));

            poseStack.pushPose();
            this.getParentModel().head.translateAndRotate(poseStack);
            this.witchNoseModel.setupAnim(player, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            poseStack.translate(0.0F, -1.42F, -0.3F);
            this.witchNoseModel.renderToBuffer(poseStack, vertexConsumer, pPackedLight,
                    LivingEntityRenderer.getOverlayCoords(player, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();
            return;
        }

        ResourceLocation texture = null;

        for (Map.Entry<ResourceLocation, ResourceLocation> entry : NOSE_TEXTURES.entrySet()) {
            if (TotemUtils.hasTotemWithEssenceClient(player, entry.getKey())) {
                texture = entry.getValue();
                break;
            }
        }

        if (texture == null) return;

        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(texture));

        poseStack.pushPose();
        this.getParentModel().head.translateAndRotate(poseStack);
        this.villagerNoseModel.setupAnim(player, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        poseStack.translate(0.0F, -1.42F, -0.3F);
        this.villagerNoseModel.renderToBuffer(poseStack, vertexconsumer, pPackedLight, LivingEntityRenderer.getOverlayCoords(player, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();

    }
}
