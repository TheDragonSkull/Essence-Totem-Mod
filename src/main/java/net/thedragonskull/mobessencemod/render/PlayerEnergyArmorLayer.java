package net.thedragonskull.mobessencemod.render;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.thedragonskull.mobessencemod.util.TotemUtils;

@OnlyIn(Dist.CLIENT)
public class PlayerEnergyArmorLayer extends PlayerEnergySwirlLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private static final ResourceLocation WITHER_ARMOR_LOCATION = ResourceLocation.parse("textures/entity/wither/wither_armor.png");
    private final PlayerModel<AbstractClientPlayer> model;

    public PlayerEnergyArmorLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.model = new PlayerModel<>(modelSet.bakeLayer(ModelLayers.PLAYER), true);
    }

    @Override
    protected boolean shouldRender(AbstractClientPlayer player) {
        return player.getHealth() <= player.getMaxHealth() / 2.0F &&
                TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:wither"));
    }

    @Override
    protected float xOffset(float tickCount) {
        return Mth.cos(tickCount * 0.02F) * 3.0F;
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return WITHER_ARMOR_LOCATION;
    }

    @Override
    protected EntityModel<AbstractClientPlayer> model() {
        return this.model;
    }
}
