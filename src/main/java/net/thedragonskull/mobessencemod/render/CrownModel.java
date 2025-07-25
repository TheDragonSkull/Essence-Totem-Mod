package net.thedragonskull.mobessencemod.render;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.thedragonskull.mobessencemod.MobEssenceMod;

public class CrownModel extends EntityModel<LivingEntity> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MobEssenceMod.MOD_ID, "crown_model"), "main");
	private final ModelPart gems;
	private final ModelPart archs;
	private final ModelPart top_cross;
	private final ModelPart band;
	private final ModelPart bars;
	private final ModelPart crosses;

	public final ModelPart passive_gem;
	public final ModelPart hostile_gem;
	public final ModelPart boss_gem;
	public final ModelPart non_mob_gem;
	public final ModelPart special_gem;
	public final ModelPart neutral_gem;

	public CrownModel(ModelPart root) {
		this.gems = root.getChild("gems");

		this.hostile_gem = this.gems.getChild("hostile_gem_r1");
		this.passive_gem = this.gems.getChild("passive_gem_r1");
		this.boss_gem = this.gems.getChild("boss_gem_r1");
		this.non_mob_gem = this.gems.getChild("non_mob_gem_r1");
		this.special_gem = this.gems.getChild("special_gem_r1");
		this.neutral_gem = this.gems.getChild("neutral_gem_r1");

		this.archs = root.getChild("archs");
		this.top_cross = this.archs.getChild("top_cross");
		this.band = root.getChild("band");
		this.bars = this.band.getChild("bars");
		this.crosses = this.band.getChild("crosses");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition gems = partdefinition.addOrReplaceChild("gems",
				CubeListBuilder.create(),
				PartPose.offset(0.0F, 16.0F, 0.0F)
		);

		PartDefinition non_mob_gem_r1 = gems.addOrReplaceChild("non_mob_gem_r1", CubeListBuilder.create().texOffs(29, 0).addBox(-0.1375F, -0.4F, -0.35F, 0.275F, 0.8F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.4F, -0.3125F, 0.0F, 1.5708F, 0.0F));

		PartDefinition hostile_gem_r1 = gems.addOrReplaceChild("hostile_gem_r1", CubeListBuilder.create().texOffs(21, 0).addBox(2.9625F, 4.6F, -0.3625F, 0.275F, 0.8F, 0.7F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition boss_gem_r1 = gems.addOrReplaceChild("boss_gem_r1", CubeListBuilder.create().texOffs(25, 0).addBox(-0.1375F, -0.4F, -0.35F, 0.275F, 0.8F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.4F, 0.3875F, 0.0F, -1.5708F, 0.0F));

		PartDefinition special_gem_r1 = gems.addOrReplaceChild("special_gem_r1", CubeListBuilder.create().texOffs(29, 3).addBox(-0.1375F, -0.4F, -0.35F, 0.275F, 0.8F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.0F, 3.0875F, 0.0F, -1.5708F, 0.0F));

		PartDefinition neutral_gem_r1 = gems.addOrReplaceChild("neutral_gem_r1", CubeListBuilder.create().texOffs(21, 3).addBox(-0.1375F, -0.4F, -0.35F, 0.275F, 0.8F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.1F, 5.0F, -0.0125F, 0.0F, 3.1416F, 0.0F));

		PartDefinition passive_gem_r1 = gems.addOrReplaceChild("passive_gem_r1", CubeListBuilder.create().texOffs(25, 3).addBox(-0.1375F, -0.4F, -0.35F, 0.275F, 0.8F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.0F, -3.1125F, 0.0F, 1.5708F, 0.0F));

		PartDefinition archs = partdefinition.addOrReplaceChild("archs", CubeListBuilder.create().texOffs(24, 12).addBox(0.5F, 4.5F, -0.5F, 1.0F, 2.5F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(29, 12).addBox(0.5F, 4.5F, 5.5F, 1.0F, 2.5F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(19, 11).addBox(-2.0F, 4.5F, 2.0F, 0.0F, 2.5F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(16, 10).addBox(-2.0F, 4.5F, 2.0F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(23, 10).addBox(1.0F, 4.5F, 2.0F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(19, 12).addBox(0.5F, 4.5F, -0.5F, 1.0F, 0.0F, 2.5F, new CubeDeformation(0.0F))
				.texOffs(14, 12).addBox(0.5F, 4.5F, 3.0F, 1.0F, 0.0F, 2.5F, new CubeDeformation(0.0F))
				.texOffs(27, 11).addBox(4.0F, 4.5F, 2.0F, 0.0F, 2.5F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(9, 18).addBox(0.3F, 3.1F, 1.8F, 1.4F, 1.375F, 1.4F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 12.5F, -2.5F));

		PartDefinition top_cross = archs.addOrReplaceChild("top_cross", CubeListBuilder.create().texOffs(9, 28).addBox(-0.5F, 4.8F, -0.25F, 1.0F, 1.0F, 0.5F, new CubeDeformation(0.0F))
				.texOffs(20, 6).addBox(-1.5F, 5.8F, -0.25F, 3.0F, 1.0F, 0.5F, new CubeDeformation(0.0F))
				.texOffs(12, 28).addBox(-0.5F, 6.8F, -0.25F, 1.0F, 0.5F, 0.5F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -4.2F, 2.5F));

		PartDefinition band = partdefinition.addOrReplaceChild("band", CubeListBuilder.create().texOffs(2, 11).addBox(-2.5F, 6.0F, -1.0F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(1, 11).addBox(-2.5F, 6.0F, 5.0F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(1, 16).addBox(2.5F, 6.0F, 0.0F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(3, 16).addBox(-3.5F, 6.0F, 0.0F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-2.5F, 2.0F, 0.0F, 5.0F, 4.5F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, -2.5F));

		PartDefinition bars = band.addOrReplaceChild("bars", CubeListBuilder.create().texOffs(22, 23).addBox(-0.5F, 7.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(22, 27).addBox(-5.5F, 7.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(17, 27).addBox(-5.5F, 7.0F, 4.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(17, 23).addBox(-0.5F, 7.0F, 4.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, -3.0F, 0.0F));

		PartDefinition crosses = band.addOrReplaceChild("crosses", CubeListBuilder.create().texOffs(17, 19).addBox(3.5F, 7.0F, -4.0F, 0.5F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(21, 16).addBox(3.5F, 8.0F, -5.0F, 0.5F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(17, 21).addBox(3.5F, 9.0F, -4.0F, 0.5F, 0.5F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(27, 27).addBox(0.5F, 7.0F, -6.5F, 1.0F, 1.0F, 0.5F, new CubeDeformation(0.0F))
				.texOffs(24, 21).addBox(-0.5F, 8.0F, -6.5F, 3.0F, 1.0F, 0.5F, new CubeDeformation(0.0F))
				.texOffs(27, 30).addBox(0.5F, 9.0F, -6.5F, 1.0F, 0.5F, 0.5F, new CubeDeformation(0.0F))
				.texOffs(7, 24).addBox(-2.0F, 7.0F, -4.0F, 0.5F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(8, 24).addBox(-2.0F, 8.0F, -5.0F, 0.5F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(5, 26).addBox(-2.0F, 9.0F, -4.0F, 0.5F, 0.5F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(1, 26).addBox(0.5F, 7.0F, -1.0F, 1.0F, 1.0F, 0.5F, new CubeDeformation(0.0F))
				.texOffs(1, 28).addBox(-0.5F, 8.0F, -1.0F, 3.0F, 1.0F, 0.5F, new CubeDeformation(0.0F))
				.texOffs(0, 30).addBox(0.5F, 9.0F, -1.0F, 1.0F, 0.5F, 0.5F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, -3.5F, 6.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(LivingEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		gems.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		archs.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		band.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}