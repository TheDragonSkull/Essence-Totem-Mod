package net.thedragonskull.mobessencemod.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.thedragonskull.mobessencemod.MobEssenceMod;
import net.thedragonskull.mobessencemod.abilities.IronGolemAbility;

public class FractureOverlayLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private static final ResourceLocation CRACK_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MobEssenceMod.MOD_ID, "textures/misc/cracked_player.png");

    public FractureOverlayLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        if (!IronGolemAbility.isFractured(player)) return;

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(CRACK_TEXTURE));

        PlayerModel<AbstractClientPlayer> model = getParentModel();

        model.hat.visible = false;
        model.leftSleeve.visible = false;
        model.rightSleeve.visible = false;
        model.jacket.visible = false;
        model.leftPants.visible = false;
        model.rightPants.visible = false;

        model.copyPropertiesTo(model);
        model.renderToBuffer(
                poseStack,
                consumer,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 0.8F
        );
    }
}
