package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class CaveSpiderAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void caveSpiderPoison(LivingAttackEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;

        if (!player.getMainHandItem().isEmpty()) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:cave_spider"))) return;

        if (player.getRandom().nextInt(5) == 0) {
            event.getEntity().addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));
        }
    }

    public static void climb(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Player player = event.player;
        if (player == null || !player.level().isClientSide()) return;

        if (!TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:cave_spider"))) return;

        if (!player.horizontalCollision) return;

        Minecraft mc = Minecraft.getInstance();

        boolean moving =
                mc.options.keyUp.isDown() ||
                        mc.options.keyDown.isDown() ||
                        mc.options.keyLeft.isDown() ||
                        mc.options.keyRight.isDown();

        if (!moving) return;

        Vec3 inputMotion = player.getDeltaMovement();
        double climbSpeed = 0.2;

        player.setDeltaMovement(inputMotion.x * 0.9, climbSpeed, inputMotion.z * 0.9);
        player.fallDistance = 0.0F;
    }

    // CobWebBlockMixin
}
