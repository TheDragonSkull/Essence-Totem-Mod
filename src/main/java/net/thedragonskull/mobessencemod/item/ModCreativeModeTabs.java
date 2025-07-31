package net.thedragonskull.mobessencemod.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.thedragonskull.mobessencemod.MobEssenceMod;
import net.thedragonskull.mobessencemod.abilities.TotemEssenceRegistry;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.Comparator;
import java.util.List;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MobEssenceMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab>  MOB_ESSENCE_TAB = CREATIVE_MODE_TABS.register("mob_essence_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.TOTEM_TAB_ICON.get()))
                    .title(Component.translatable("creativetab.mob_essence_tab"))
                    .displayItems((pParameters, pOutput) -> {

                        pOutput.accept(ModItems.TOTEM_OF_ESSENCE.get());

                        List<ItemStack> totemStacks = TotemEssenceRegistry.getAll().stream()
                                .map(data -> {
                                    ItemStack stack = new ItemStack(ModItems.TOTEM_OF_ESSENCE.get());
                                    TotemUtils.setEssence(stack, data.id());
                                    return stack;
                                })
                                .sorted(Comparator.comparing(stack -> getSortKey(TotemUtils.getEssence(stack))))
                                .toList();

                        totemStacks.forEach(pOutput::accept);
                    })
                    .build());

    private static String getSortKey(ResourceLocation essenceId) {
        String path = essenceId.getPath();

        if (path.endsWith("_frog")) return "frog";
        if (path.endsWith("_fox")) return "fox";
        if (path.endsWith("_mooshroom")) return "mooshroom";
        if (path.endsWith("_bunny")) return "rabbit";

        return path;
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
