package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.thedragonskull.mobessencemod.util.TotemMobCategory;
import net.thedragonskull.mobessencemod.util.TotemTooltipData;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class TotemEssenceRegistry {

    public record EssenceData(
            ResourceLocation id,
            String modelPredicateName,
            TotemTooltipData tooltip,
            SoundEvent sound,
            IMobAbility ability,
            TotemMobCategory category
    ) {}

    private static final Map<String, EssenceData> ESSENCE_DATA = new LinkedHashMap<>();

    static {

        // PASSIVE
        register("minecraft:pig", "essence_pig",
                new TotemTooltipData("Cast-Iron Stomach", "Immune to negative food effects"),
                SoundEvents.PIG_HURT,
                new PigAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:parrot", "essence_parrot",
                new TotemTooltipData("Featherlight", "Double jump + don't trigger pressure plates or tripwires"),
                SoundEvents.PARROT_HURT,
                new ParrotAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:cod", "essence_cod",
                new TotemTooltipData("Adaptive Lungs", "Increases underwater breathing time"),
                SoundEvents.COD_HURT,
                new CodAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:salmon", "essence_salmon",
                new TotemTooltipData("Underwater Momentum", "Increased swimming speed"),
                SoundEvents.SALMON_HURT,
                new SalmonAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:tropical_fish", "essence_tropical_fish",
                new TotemTooltipData("Clear Waters", "Enhanced underwater visibility"),
                SoundEvents.TROPICAL_FISH_HURT,
                new TropicalFishAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:chicken", "essence_chicken",
                new TotemTooltipData("Feathered Fiend", "Basically permanent slow falling"),
                SoundEvents.CHICKEN_HURT,
                new ChickenAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:villager", "essence_villager",
                new TotemTooltipData("Scent of the Money", "Grow a big nose to smell nearby emerald ores (~20 blocks)"),
                SoundEvents.VILLAGER_HURT,
                new VillagerAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:cow", "essence_cow",
                new TotemTooltipData("Cleanse", "Chance of cleansing a harmful effect (1/3) when applied while the totem is equipped"),
                SoundEvents.COW_HURT,
                new CowAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:sheep", "essence_sheep",
                new TotemTooltipData("Wooly Shield", "Chance of halving melee and projectile damage (1/3)"),
                SoundEvents.SHEEP_HURT,
                new SheepAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:squid", "essence_squid",
                new TotemTooltipData("Ink Escape", "When melee attacked, chance to release ink and slow the attacker (1/3). If underwater, it also pushes the player away"),
                SoundEvents.SQUID_HURT,
                new SquidAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:glow_squid", "essence_glow_squid",
                new TotemTooltipData("Hypnotic Glowing", "Hypnotize aquatic mobs into following the player (10 blocks radius) + when melee attacked, chance to release glowing ink and put glowing effect to the attacker (1/3). If underwater, it also pushes the player away"),
                SoundEvents.GLOW_SQUID_HURT,
                new GlowSquidAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:horse", "essence_horse",
                new TotemTooltipData("Power Leap", "Upgraded jump when sprinting for more than 3 seconds without stopping + chance to horse kick if melee attacked from behind (1/3)"),
                SoundEvents.HORSE_HURT,
                new HorseAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:donkey", "essence_donkey",
                new TotemTooltipData("Burden Bearer", "Chests drop themselves with the items inside instead of dropping the contents + chance to donkey kick if melee attacked from behind (1/3)"),
                SoundEvents.DONKEY_HURT,
                new DonkeyAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:turtle", "essence_turtle",
                new TotemTooltipData("Rear shield", "Block all incoming damage from behind"),
                SoundEvents.TURTLE_HURT,
                new TurtleAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:axolotl", "essence_axolotl",
                new TotemTooltipData("PTSD Therapy", "After killing a mob that's trying to fight, gain regeneration I for 5 seconds (regen II if underwater)"),
                SoundEvents.AXOLOTL_HURT,
                new AxolotlAbility(),
                TotemMobCategory.PASSIVE
        );

        // NEUTRAL
        register("minecraft:bee", "essence_bee",
                new TotemTooltipData("Stinger Reflex", "Stings and poisons enemies when hit from behind + bees will no longer target the player"),
                SoundEvents.BEE_HURT,
                new BeeAbility(),
                TotemMobCategory.NEUTRAL
        );

        register("minecraft:pufferfish", "essence_pufferfish",
                new TotemTooltipData("Toxic Touch", "Chance to inflict knockback + poison on contact (1/3)"),
                SoundEvents.PUFFER_FISH_HURT,
                new PufferfishAbility(),
                TotemMobCategory.NEUTRAL
        );

        register("minecraft:spider", "essence_spider",
                new TotemTooltipData("Wall-Crawler", "Slowly climb vertical surfaces"),
                SoundEvents.SPIDER_HURT,
                new SpiderAbility(),
                TotemMobCategory.NEUTRAL
        );

        register("minecraft:cave_spider", "essence_cave_spider",
                new TotemTooltipData("Poisonous Wall-Crawler", "Slowly climb walls + poison enemies when unarmed (20%)"),
                SoundEvents.SPIDER_HURT,
                new CaveSpiderAbility(),
                TotemMobCategory.NEUTRAL
        );

        register("minecraft:enderman", "essence_enderman",
                new TotemTooltipData("Blink Instinct", "Teleport away to evade ranged damage"),
                SoundEvents.ENDERMAN_HURT,
                new EndermanAbility(),
                TotemMobCategory.NEUTRAL
        );

        register("minecraft:goat", "essence_goat",
                new TotemTooltipData("Ram Charge", "Tackle any mob in your way while running; the faster the player, the more the damage"),
                SoundEvents.GOAT_HURT,
                new GoatAbility(),
                TotemMobCategory.NEUTRAL
        );

        register("minecraft:llama", "essence_llama",
                new TotemTooltipData("Spit Happens", "Automatically spits at hostile mobs nearby dealing damage and knockback (every 5 seconds)"),
                SoundEvents.LLAMA_HURT,
                new LlamaAbility(),
                TotemMobCategory.NEUTRAL
        );

        register("minecraft:wolf", "essence_wolf",
                new TotemTooltipData("Pack Instinct", "When injured, there's a chance two wolves will come to your aid and attack your assailant"),
                SoundEvents.WOLF_HURT,
                new WolfAbility(),
                TotemMobCategory.NEUTRAL
        );

        register("minecraft:dolphin", "essence_dolphin",
                new TotemTooltipData("Wave Rider", "Increased swimming speed + spin boost when swimming near an entity (if the entity is hit, damage is applied but it cancels the boost)"),
                SoundEvents.DOLPHIN_HURT,
                new DolphinAbility(),
                TotemMobCategory.NEUTRAL
        );

        // HOSTILE
        register("minecraft:zombie", "essence_zombie",
                new TotemTooltipData("Zombie Recall", "If killed by an entity, instantly revive with half a heart (one time use)"),
                SoundEvents.ZOMBIE_HURT,
                new ZombieAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:creeper", "essence_creeper",
                new TotemTooltipData("Explosive Retaliation", "When taking melee damage, there's a chance to trigger a small explosion that doesn't affect blocks"),
                SoundEvents.CREEPER_HURT,
                new CreeperAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:skeleton", "essence_skeleton",
                new TotemTooltipData("Bone Quiver", "Arrows fired have a chance to not be consumed: 50% for regular arrows, 25% for spectral or tipped arrows"),
                SoundEvents.SKELETON_HURT,
                new SkeletonAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:slime", "essence_slime",
                new TotemTooltipData("Elastic Body", "Bounce on the ground when falling + chance to absorbe melee damage"),
                SoundEvents.SLIME_HURT,
                new SlimeAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:magma_cube", "essence_magma_cube",
                new TotemTooltipData("Igneous Elastic Body", "Bounce on the ground when falling + chance to set any mob on fire on contact (1/3)"),
                SoundEvents.MAGMA_CUBE_HURT,
                new MagmaCubeAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:blaze", "essence_blaze",
                new TotemTooltipData("Infernal Drift", "Shift while on air to levitate + chance to set any mob on fire (1/4) on hit (melee)"),
                SoundEvents.BLAZE_HURT,
                new BlazeAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:phantom", "essence_phantom",
                new TotemTooltipData("Night Glider", "Phantoms will no longer bother the player + gliding (only at night)"),
                SoundEvents.PHANTOM_HURT,
                new PhantomAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:silverfish", "essence_silverfish",
                new TotemTooltipData("Infested Strike", "Silverfish will ignore the player + chance for two silverfish to emerge from the target's feet and attack it (1/8)"),
                SoundEvents.SILVERFISH_HURT,
                new SilverfishAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:endermite", "essence_endermite",
                new TotemTooltipData("Dimensional Call", "Endermite will ignore the player + chance for two endermite to teleport straight to the target and attack it (1/12)"),
                SoundEvents.ENDERMITE_HURT,
                new EndermiteAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:drowned", "essence_drowned",
                new TotemTooltipData("Abyssal Salvation", "Prevent death from drowning or suffocating"),
                SoundEvents.DROWNED_HURT,
                new DrownedAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:husk", "essence_husk",
                new TotemTooltipData("Scorched Salvation", "If killed by starving or something fire-related, revive with half a heart and get 20s of fire resistance + prevent starvation"),
                SoundEvents.HUSK_HURT,
                new HuskAbility(),
                TotemMobCategory.HOSTILE
        );


        // SPECIAL
        register("minecraft:zombie_villager", "essence_zombie_villager",
                new TotemTooltipData("Undead Scent of the Money", "Grow a big nose to smell nearby emerald ores (~20 blocks) + if killed by an entity, instantly revive with half a heart (one time use)"),
                SoundEvents.ZOMBIE_VILLAGER_HURT,
                new ZombieVillagerAbility(),
                TotemMobCategory.SPECIAL
        );

        register("minecraft:wandering_trader", "essence_wandering_trader",
                new TotemTooltipData("Llama Whisperer", "Llamas won't spit at the player + control llamas while riding"),
                SoundEvents.WANDERING_TRADER_HURT,
                new WanderingTraderAbility(),
                TotemMobCategory.SPECIAL
        );

        register("minecraft:trader_llama", "essence_trader_llama",
                new TotemTooltipData("Caravan Aura", "Automatically spits at hostile mobs nearby dealing damage and knockback (every 5 seconds) + nearby creatures follow the player (5 blocks radius)"),
                SoundEvents.LLAMA_HURT,
                new TraderLlamaAbility(),
                TotemMobCategory.SPECIAL
        );

        register("minecraft:red_mooshroom", "essence_red_mooshroom",
                new TotemTooltipData("Cleansing Feast", "Chance of cleansing a harmful effect (1/3) when applied while the totem is equipped + chance of filling +1 hunger when eating non harmful food (1/3)"),
                SoundEvents.COW_HURT,
                new RedMooshroomAbility(),
                TotemMobCategory.SPECIAL
        );

        register("minecraft:brown_mooshroom", "essence_brown_mooshroom",
                new TotemTooltipData("Enriched Cleanse", "Chance of cleansing a harmful effect (1/3) when applied while the totem is equipped + chance of adding +1 saturation after eating non harmful food (1/3)"),
                SoundEvents.COW_HURT,
                new BrownMooshroomAbility(),
                TotemMobCategory.SPECIAL
        );

        register("minecraft:mule", "essence_mule",
                new TotemTooltipData("Leaping Hauler", "Upgraded jump when sprinting for more than 3 seconds without stopping + chests drop themselves with the items inside instead of dropping the contents + chance to donkey kick if melee attacked from behind (1/3)"),
                SoundEvents.MULE_HURT,
                new MuleAbility(),
                TotemMobCategory.SPECIAL
        );

        register("minecraft:zombie_horse", "essence_zombie_horse",
                new TotemTooltipData("Undead Power Leap", "Upgraded jump when sprinting for more than 3 seconds without stopping + chance to horse kick if melee attacked from behind (1/3) + if killed by an entity, instantly revive with half a heart (one time use)"),
                SoundEvents.ZOMBIE_HORSE_HURT,
                new ZombieHorseAbility(),
                TotemMobCategory.SPECIAL
        );

        register("minecraft:skeleton_horse", "essence_skeleton_horse",
                new TotemTooltipData("Leaping Bone Quiver", "Upgraded jump when sprinting for more than 3 seconds without stopping + chance to horse kick if melee attacked from behind (1/3) + arrows fired have a chance to not be consumed: 50% for regular arrows, 25% for spectral or tipped arrows"),
                SoundEvents.SKELETON_HORSE_HURT,
                new SkeletonHorseAbility(),
                TotemMobCategory.SPECIAL
        );

        register("minecraft:witch", "essence_witch",
                new TotemTooltipData("Coven's Blessing", "Cats no longer run away from the player + some damage types are reduced by 25% + chance to drink a potion depending on the situation"),
                SoundEvents.WITCH_HURT,
                new WitchAbility(),
                TotemMobCategory.SPECIAL
        );
    }


    private static void register(String id, String predicate, TotemTooltipData tooltip, SoundEvent sound, IMobAbility ability, TotemMobCategory category) {
        ESSENCE_DATA.put(id, new EssenceData(
                ResourceLocation.parse(id),
                predicate,
                tooltip,
                sound,
                ability,
                category
        ));
    }

    public static boolean isRegistered(ResourceLocation id) {
        return ESSENCE_DATA.containsKey(id.toString());
    }

    public static Set<String> getRegisteredEssenceIds() {
        return ESSENCE_DATA.keySet();
    }

    public static EssenceData get(ResourceLocation id) {
        return ESSENCE_DATA.get(id.toString());
    }

    public static Collection<EssenceData> getAll() {
        return ESSENCE_DATA.values();
    }

}
