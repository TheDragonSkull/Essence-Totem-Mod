package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.thedragonskull.mobessencemod.handlers.MouseHandlerAccessor;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.List;

public class ArmorStandAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
        List<Mob> mobs = player.level().getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(32), mob ->
                mob.getTarget() == player
        );

        for (Mob mob : mobs) {
            mob.setTarget(null);
        }
    }

    public static void stillPlayer(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        MouseHandlerAccessor accessor = (MouseHandlerAccessor) mc.mouseHandler;

        if (TotemUtils.hasTotemWithEssenceClient(mc.player, ResourceLocation.parse("minecraft:armor_stand"))) {
            mc.player.input.leftImpulse = 0;
            mc.player.input.forwardImpulse = 0;
            mc.options.keyUp.setDown(false);
            mc.options.keyDown.setDown(false);
            mc.options.keyLeft.setDown(false);
            mc.options.keyRight.setDown(false);
            mc.options.keyJump.setDown(false);
            mc.options.keySprint.setDown(false);
            mc.options.keyShift.setDown(false);
            mc.options.keyUse.setDown(false);
            mc.options.keyAttack.setDown(false);
            mc.options.keyDrop.setDown(false);
            mc.options.keyPickItem.setDown(false);

            while (mc.options.keyAttack.consumeClick()) {}
            while (mc.options.keyUse.consumeClick()) {}

        }

        accessor.mobessencemod_setAccumulatedDX(0);
        accessor.mobessencemod_setAccumulatedDY(0);
    }

    public static void onMobTarget(LivingChangeTargetEvent event) {
        if (event.getNewTarget() instanceof ServerPlayer player) {
            if (TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:armor_stand"))) {
                event.setCanceled(true);
            }
        }
    }
}
