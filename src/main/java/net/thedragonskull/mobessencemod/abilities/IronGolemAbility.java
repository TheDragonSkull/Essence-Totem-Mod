package net.thedragonskull.mobessencemod.abilities;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.network.PacketDistributor;
import net.thedragonskull.mobessencemod.MobEssenceMod;
import net.thedragonskull.mobessencemod.network.PacketHandler;
import net.thedragonskull.mobessencemod.network.S2CIronGolemFractureSyncPacket;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.*;

public class IronGolemAbility implements IMobAbility {

    private static final Set<UUID> fracturedPlayers = new HashSet<>();
    private static final Map<UUID, Long> fractureExpiry = new HashMap<>();

    public static void onGolemDefense(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        boolean hasTotem = TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:iron_golem"));
        if (!hasTotem && !fracturedPlayers.contains(player.getUUID())) return;

        UUID id = player.getUUID();

        // double damage
        if (fracturedPlayers.contains(id)) {
            event.setAmount(event.getAmount() * 2f);
            fracturedPlayers.remove(id);
            fractureExpiry.remove(id);
            PacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                    new S2CIronGolemFractureSyncPacket(id, false));
            player.level().playSound(null, player.blockPosition(), SoundEvents.IRON_GOLEM_HURT, SoundSource.PLAYERS, 1f, 1f);
            return;
        }

        // fracture
        if (event.getSource().getEntity() instanceof LivingEntity && player.level().getRandom().nextInt(4) == 0) {
            event.setCanceled(true);
            fracturedPlayers.add(id);
            fractureExpiry.put(id, player.level().getGameTime() + 100);
            PacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                    new S2CIronGolemFractureSyncPacket(id, true));

            player.level().playSound(null, player.blockPosition(), SoundEvents.IRON_GOLEM_DAMAGE, SoundSource.PLAYERS, 1f, 1f);
        }
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        UUID id = player.getUUID();

        if (fractureExpiry.containsKey(id)) {
            if (player.level().getGameTime() >= fractureExpiry.get(id)) {
                fracturedPlayers.remove(id);
                fractureExpiry.remove(id);
                PacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                        new S2CIronGolemFractureSyncPacket(id, false));
            }
        }
    }

    public static void onGolemAttack(LivingAttackEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:iron_golem"))) return;

        if (event.getSource().is(DamageTypeTags.IS_PROJECTILE)) return;
        LivingEntity target = event.getEntity();

        if (player.level().getRandom().nextInt(4) == 0) {
            double resistance = target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
            double factor = Math.max(0.0, 1.0 - resistance);

            player.getServer().execute(() -> {
                Vec3 motion = target.getDeltaMovement().add(0, 0.6 * factor, 0);
                target.setDeltaMovement(motion);
                target.hurtMarked = true;

                target.level().playSound(null, target.blockPosition(), SoundEvents.IRON_GOLEM_ATTACK, SoundSource.PLAYERS, 1f, 1f);
            });
        }
    }

    public static void onUseIron(PlayerInteractEvent.RightClickItem event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:iron_golem"))) return;

        Level level = player.level();
        ItemStack item = event.getItemStack();
        if (!item.is(Items.IRON_INGOT)) return;

        if (player.getHealth() < player.getMaxHealth()) {
            player.heal(1.0F);
            player.getCooldowns().addCooldown(Items.IRON_INGOT, 30);
            item.shrink(1);

            player.level().playSound(null, player.blockPosition(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.PLAYERS, 1f, 1f);
            ((ServerLevel) level).sendParticles(ParticleTypes.HEART,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    15, 0.2, 0.3, 0.2, 0.01);

            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    public static void onOverlayRender(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || !IronGolemAbilityClient.isFractured(player)) return;
        if (!mc.options.getCameraType().isFirstPerson()) return;

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        ResourceLocation cracks = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/destroy_stage_9.png");

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, cracks);
        RenderSystem.setShaderColor(1f, 1f, 1f, 0.05f);

        event.getGuiGraphics().blit(cracks, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);

        RenderSystem.disableBlend();
    }

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    @OnlyIn(Dist.CLIENT)
    public static class IronGolemAbilityClient {
        private static final Set<UUID> fracturedPlayers = new HashSet<>();

        public static void addFractured(UUID id) {
            fracturedPlayers.add(id);
        }

        public static void removeFractured(UUID id) {
            fracturedPlayers.remove(id);
        }

        public static boolean isFractured(Player player) {
            return fracturedPlayers.contains(player.getUUID());
        }
    }

}
