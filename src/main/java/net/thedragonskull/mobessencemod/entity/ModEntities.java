package net.thedragonskull.mobessencemod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.thedragonskull.mobessencemod.MobEssenceMod;
import net.thedragonskull.mobessencemod.entity.custom.IllusionDecoyEntity;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MobEssenceMod.MOD_ID);

    public static final RegistryObject<EntityType<IllusionDecoyEntity>> ILLUSION_DECOY =
            ENTITY_TYPES.register("illusion_decoy", () -> EntityType.Builder.of(IllusionDecoyEntity::new, MobCategory.MISC)
                    .sized(0.5f, 1.975f)
                    .clientTrackingRange(8)
                    .updateInterval(1)
                    .build("illusion_decoy"));


    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
