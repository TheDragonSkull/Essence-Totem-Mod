package net.thedragonskull.mobessencemod.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.thedragonskull.mobessencemod.MobEssenceMod;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class CrownRenderer implements ICurioRenderer {
    private final ResourceLocation CROWN_TEXTURE = ResourceLocation.fromNamespaceAndPath(MobEssenceMod.MOD_ID, "textures/entity/crown.png");
    private final CrownModel model;

    public CrownRenderer() {
        this.model = new CrownModel(Minecraft.getInstance().getEntityModels().bakeLayer(CrownModel.LAYER_LOCATION));
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext,
                                                                          PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent,
                                                                          MultiBufferSource renderTypeBuffer, int light, float limbSwing,
                                                                          float limbSwingAmount, float partialTicks, float ageInTicks,
                                                                          float netHeadYaw, float headPitch) {
        LivingEntity living = slotContext.entity();
        if (!(living instanceof Player player)) return;
        if (player.isInvisible()) return;

        model.passive_gem.visible = player.getPersistentData().getBoolean("adv_passive");
        model.hostile_gem.visible = player.getPersistentData().getBoolean("adv_hostile");
        model.neutral_gem.visible = player.getPersistentData().getBoolean("adv_neutral");
        model.special_gem.visible = player.getPersistentData().getBoolean("adv_special");
        model.boss_gem.visible = player.getPersistentData().getBoolean("adv_boss");
        model.non_mob_gem.visible = player.getPersistentData().getBoolean("adv_non_mob");

        matrixStack.pushPose();
        if (renderLayerParent.getModel() instanceof HumanoidModel<?> humanoidModel) {
            humanoidModel.head.translateAndRotate(matrixStack);
            matrixStack.translate(0.0D, -2.0D, 0.0D);
        }

        VertexConsumer vertex = renderTypeBuffer.getBuffer(RenderType.entityCutoutNoCull(CROWN_TEXTURE));
        model.renderToBuffer(matrixStack, vertex, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        matrixStack.popPose();
    }

}
