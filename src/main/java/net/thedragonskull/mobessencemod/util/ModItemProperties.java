package net.thedragonskull.mobessencemod.util;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.thedragonskull.mobessencemod.abilities.TotemEssenceRegistry;
import net.thedragonskull.mobessencemod.item.ModItems;

public class ModItemProperties {

    public static void addCustomItemProperties() {
        registerTotemEssenceProperties(ModItems.TOTEM_OF_ESSENCE.get());
    }

    private static void registerTotemEssenceProperties(Item item) {
        for (TotemEssenceRegistry.EssenceData data : TotemEssenceRegistry.getAll()) {
            ResourceLocation predicateId = ResourceLocation.parse(data.modelPredicateName());

            ItemProperties.register(item, predicateId, (stack, level, entity, seed) -> {
                ResourceLocation essence = TotemUtils.getEssence(stack);
                return (essence != null && essence.equals(data.id())) ? 1.0F : 0.0F;
            });
        }
    }
}
