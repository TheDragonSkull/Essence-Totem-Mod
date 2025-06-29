package net.thedragonskull.mobessencemod.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final KeyBindings INSTANCE = new KeyBindings();

    private KeyBindings() {
    }

    public static final String KEY_CATEGORY_MOB_ESSENCE = "key.category.mobessencemod.mob_essence";
    public static final String KEY_TOTEM_SWAP = "key.category.mobessencemod.totem_swap";

    public final KeyMapping SWAP_TOTEM = new KeyMapping(
            KEY_TOTEM_SWAP,
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            KEY_CATEGORY_MOB_ESSENCE);
}
