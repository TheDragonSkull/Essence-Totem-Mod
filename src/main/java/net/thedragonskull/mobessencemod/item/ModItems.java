package net.thedragonskull.mobessencemod.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.thedragonskull.mobessencemod.MobEssenceMod;
import net.thedragonskull.mobessencemod.item.custom.TotemOfEssenceItem;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MobEssenceMod.MOD_ID);

    public static final RegistryObject<Item> TOTEM_OF_ESSENCE = ITEMS.register("totem_of_essence",
            () -> new TotemOfEssenceItem(new Item.Properties().stacksTo(1).fireResistant()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
