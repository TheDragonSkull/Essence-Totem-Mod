package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.thedragonskull.mobessencemod.handlers.MouseHandlerAccessor;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import static net.thedragonskull.mobessencemod.util.TotemUtils.restoreIfPressed;

public class ArmorStandAbility implements IMobAbility {

    private static boolean wasInArmorStandMode = false;

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void stillPlayer(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        MouseHandlerAccessor accessor = (MouseHandlerAccessor) mc.mouseHandler;

        boolean isArmorStandMode = TotemUtils.hasTotemWithEssenceClient(mc.player, ResourceLocation.parse("minecraft:armor_stand"));

        if (isArmorStandMode) {
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

            accessor.mobessencemod_setAccumulatedDX(0);
            accessor.mobessencemod_setAccumulatedDY(0);

        } else if (wasInArmorStandMode) {
            long window = mc.getWindow().getWindow();

            restoreIfPressed(mc.options.keyUp, window);
            restoreIfPressed(mc.options.keyDown, window);
            restoreIfPressed(mc.options.keyLeft, window);
            restoreIfPressed(mc.options.keyRight, window);
            restoreIfPressed(mc.options.keyJump, window);
            restoreIfPressed(mc.options.keySprint, window);
            restoreIfPressed(mc.options.keyShift, window);
            restoreIfPressed(mc.options.keyUse, window);
            restoreIfPressed(mc.options.keyAttack, window);
            restoreIfPressed(mc.options.keyDrop, window);
            restoreIfPressed(mc.options.keyPickItem, window);
        }

        wasInArmorStandMode = isArmorStandMode;
    }

    public static void onMobTarget(LivingChangeTargetEvent event) {
        if (event.getNewTarget() instanceof ServerPlayer player) {
            if (TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:armor_stand"))) {
                event.setNewTarget(null);
            }
        }
    }
}
