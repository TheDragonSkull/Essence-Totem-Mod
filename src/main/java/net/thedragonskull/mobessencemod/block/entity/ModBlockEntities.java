package net.thedragonskull.mobessencemod.block.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.thedragonskull.mobessencemod.MobEssenceMod;
import net.thedragonskull.mobessencemod.block.ModBlocks;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MobEssenceMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<HealingEndRodBE>> HEALING_END_ROD_BE =
            BLOCK_ENTITIES.register("healing_end_rod_be", () ->
                    BlockEntityType.Builder.of(HealingEndRodBE::new,
                            ModBlocks.HEALING_END_ROD.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
