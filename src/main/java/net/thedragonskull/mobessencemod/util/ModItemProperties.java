package net.thedragonskull.mobessencemod.util;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.thedragonskull.mobessencemod.item.ModItems;

public class ModItemProperties {

    public static void addCustomItemProperties() {
        totemStates(ModItems.TOTEM_OF_ESSENCE.get());
    }

    private static void totemStates(Item item) {

        ItemProperties.register(item, ResourceLocation.parse("essence_pig"),
                (stack, level, entity, seed) -> {
            String essence = stack.getTag() != null ? stack.getTag().getString("Essence") : "null";

            if ("minecraft:pig".equals(essence)) {
                return 1.0F;
            }

            return 0.0F;
        });

        ItemProperties.register(item, ResourceLocation.parse("essence_bee"),
                (stack, level, entity, seed) -> {

                    if (stack.getTag() != null && stack.hasTag() && "minecraft:bee".equals(stack.getTag().getString("Essence"))) {
                        return 1.0F;
                    }

                    return 0.0F;
                });

    }
}
