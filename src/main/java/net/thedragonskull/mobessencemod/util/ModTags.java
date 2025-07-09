package net.thedragonskull.mobessencemod.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.thedragonskull.mobessencemod.MobEssenceMod;

public class ModTags {

    public static class MobEssenceBiomeTags {
        public static final TagKey<Biome> IS_SNOWY = TagKey.create(Registries.BIOME,
                ResourceLocation.fromNamespaceAndPath(MobEssenceMod.MOD_ID, "is_snowy"));
    }

}
