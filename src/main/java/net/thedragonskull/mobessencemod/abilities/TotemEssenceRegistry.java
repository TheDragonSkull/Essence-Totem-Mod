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
                new TotemTooltipData("Cast-Iron Stomach", "The glutton fears no poison. And should the storm choose thee, a new form walks: horned, golden, and wrathful"),
                SoundEvents.PIG_HURT,
                new PigAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:parrot", "essence_parrot",
                new TotemTooltipData("Featherlight", "Woven of whisper and wind, its bearer dances above danger. Traps lie still, and air yields twice to your command"),
                SoundEvents.PARROT_HURT,
                new ParrotAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:cod", "essence_cod",
                new TotemTooltipData("Adaptive Lungs", "Your chest swells not in desperation, but in quiet rhythm. With every heartbeat below the waves, breath becomes patience"),
                SoundEvents.COD_HURT,
                new CodAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:salmon", "essence_salmon",
                new TotemTooltipData("Underwater Momentum", "The current is no foe, but a path. Momentum is your nature, and rivers yield to your rhythm"),
                SoundEvents.SALMON_HURT,
                new SalmonAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:tropical_fish", "essence_tropical_fish",
                new TotemTooltipData("Clear Waters", "The veil lifts beneath the tide. The sea, once secretive, now gleams in full truth beneath your gaze"),
                SoundEvents.TROPICAL_FISH_HURT,
                new TropicalFishAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:chicken", "essence_chicken",
                new TotemTooltipData("Feathered Fiend", "No fall can claim you. Gravity whispers, but your feathers answer with defiance"),
                SoundEvents.CHICKEN_HURT,
                new ChickenAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:villager", "essence_villager",
                new TotemTooltipData("Scent of the Money", "Once you smell it, you’ll never forget. The earth cannot hide its riches from your developed nostrils"),
                SoundEvents.VILLAGER_HURT,
                new VillagerAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:cow", "essence_cow",
                new TotemTooltipData("Cleanse", "The unshaken spirit repels all foulness. Illness falters, and poison may forget its path"),
                SoundEvents.COW_HURT,
                new CowAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:sheep", "essence_sheep",
                new TotemTooltipData("Wooly Shield", "Softness is not weakness. What strikes the wool may not strike the flesh"),
                SoundEvents.SHEEP_HURT,
                new SheepAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:squid", "essence_squid",
                new TotemTooltipData("Ink Escape", "Your fear may stain the world black. Let them grope in blindness while the tide carries you away"),
                SoundEvents.SQUID_HURT,
                new SquidAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:glow_squid", "essence_glow_squid",
                new TotemTooltipData("Hypnotic Glowing", "From the deep, a light that bends wills. The sea obeys, and your enemies may shine before they fall"),
                SoundEvents.GLOW_SQUID_HURT,
                new GlowSquidAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:horse", "essence_horse",
                new TotemTooltipData("Power Leap", "Momentum becomes flight. Let none stand behind you, lest they taste your fury"),
                SoundEvents.HORSE_HURT,
                new HorseAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:donkey", "essence_donkey",
                new TotemTooltipData("Burden Bearer", "The burden remains whole. What is carried shall not scatter, and betrayal meets a hindleg’s truth"),
                SoundEvents.DONKEY_HURT,
                new DonkeyAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:turtle", "essence_turtle",
                new TotemTooltipData("Rear shield", "Turn your back without fear. The world may strike, but never from behind"),
                SoundEvents.TURTLE_HURT,
                new TurtleAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:axolotl", "essence_axolotl",
                new TotemTooltipData("PTSD Therapy", "The wound of the moment fades swiftly. Pain lingers, but so does the will to heal"),
                SoundEvents.AXOLOTL_HURT,
                new AxolotlAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:bat", "essence_bat",
                new TotemTooltipData("Shadow Pulse", "You and your companions see what others miss. They, in turn, see nothing at all"),
                SoundEvents.BAT_HURT,
                new BatAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:strider", "essence_strider",
                new TotemTooltipData("Lava Core", "The flame no longer bites. You move through molten depths as if born from them"),
                SoundEvents.STRIDER_HURT,
                new StriderAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:allay", "essence_allay",
                new TotemTooltipData("Selective Retrieval", "A quiet bond draws the familiar near, as if the world remembers what belongs to you. Let melody guide your feet, and rhythm gather what lies forgotten"),
                SoundEvents.ALLAY_HURT,
                new AllayAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:camel", "essence_camel",
                new TotemTooltipData("Sandskipper", "No green thorn may wound you, and no rise slows your gait. The impulse bursts into motion, swift as a desert gale"),
                SoundEvents.CAMEL_HURT,
                new CamelAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:ocelot", "essence_ocelot",
                new TotemTooltipData("Feline Grace", "You land with grace, while the hissing stalker flee from your gaze"),
                SoundEvents.OCELOT_HURT,
                new OcelotAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:cat", "essence_cat",
                new TotemTooltipData("Feline Blessing", "Grace steadies your fall, the hissing dread keeps its distance, and the dreams whisper their gift"),
                SoundEvents.CAT_HURT,
                new CatAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:fox", "essence_fox",
                new TotemTooltipData("Twilight Scavenger", "Nature’s grasp cannot bind you and when the stars look kindly, prey may fall in generous echo"),
                SoundEvents.FOX_HURT,
                new FoxAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:snow_fox", "essence_snow_fox",
                new TotemTooltipData("Snow Twilight Scavenger", "Nature’s traps loosen their hold and fortune may dance in the night. You trace no weight upon the icy powder of the earth"),
                SoundEvents.FOX_HURT,
                new SnowFoxAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:tadpole", "essence_tadpole",
                new TotemTooltipData("Slimy Snack", "What is bitter to the tongue may yet sweeten your spirit, and the ground or water shall whisper what strength you deserve"),
                SoundEvents.TADPOLE_HURT,
                new TadpoleAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:temperate_frog", "essence_temperate_frog",
                new TotemTooltipData("Amphibian Spirit of Resilience", "Spring-bound legs reward the still, and short descents bring no harm. No venom dares linger beneath your skin"),
                SoundEvents.FROG_HURT,
                new FrogAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:cold_frog", "essence_cold_frog",
                new TotemTooltipData("Amphibian Spirit of the Tundra", "Leaping limbs bless the patient, while gentle falls leaves no scar. Winter’s bite passes you by"),
                SoundEvents.FROG_HURT,
                new FrogAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:warm_frog", "essence_warm_frog",
                new TotemTooltipData("Amphibian Spirit of the Dunes", "Legs coiled in patience grant grace, and soft landings bear no wound. Your pace defies all that would delay it"),
                SoundEvents.FROG_HURT,
                new FrogAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:rabbit", "essence_rabbit",
                new TotemTooltipData("Forager's Spring", "With nimble bounds and forager’s scent, what lies beneath the overgrowth is not always weeds"),
                SoundEvents.RABBIT_HURT,
                new RabbitAbility(),
                TotemMobCategory.PASSIVE
        );

        register("minecraft:sniffer", "essence_sniffer",
                new TotemTooltipData("Scent Tracking", "A subtle scent arising from your grasp draws close the echoes of what you seek. The closer you draw, the louder the earth’s murmur becomes, speeding your quest"),
                SoundEvents.SNIFFER_HURT,
                new SnifferAbility(),
                TotemMobCategory.PASSIVE
        );

        // NEUTRAL
        register("minecraft:bee", "essence_bee",
                new TotemTooltipData("Stinger Reflex", "No sting finds you guilty, yet betrayal draws the poison forth"),
                SoundEvents.BEE_HURT,
                new BeeAbility(),
                TotemMobCategory.NEUTRAL
        );

        register("minecraft:pufferfish", "essence_pufferfish",
                new TotemTooltipData("Toxic Touch", "Your presence invites approach, but your touch may ensure regret"),
                SoundEvents.PUFFER_FISH_HURT,
                new PufferfishAbility(),
                TotemMobCategory.NEUTRAL
        );

        register("minecraft:spider", "essence_spider",
                new TotemTooltipData("Wall-Crawler", "Clutching strands lose their grip, no wall denies your ascent"),
                SoundEvents.SPIDER_HURT,
                new SpiderAbility(),
                TotemMobCategory.NEUTRAL
        );

        register("minecraft:cave_spider", "essence_cave_spider",
                new TotemTooltipData("Poisonous Wall-Crawler", "No thread ensnares you, no surface hinders, and and venom clings to gestures unarmed"),
                SoundEvents.SPIDER_HURT,
                new CaveSpiderAbility(),
                TotemMobCategory.NEUTRAL
        );

        register("minecraft:enderman", "essence_enderman",
                new TotemTooltipData("Blink Instinct", "To be struck from afar is a choice you never make; the world must catch you first"),
                SoundEvents.ENDERMAN_HURT,
                new EndermanAbility(),
                TotemMobCategory.NEUTRAL
        );

        register("minecraft:goat", "essence_goat",
                new TotemTooltipData("Ram Charge", "With each stride, the air grows sharper, and nothing standing still remains whole"),
                SoundEvents.GOAT_HURT,
                new GoatAbility(),
                TotemMobCategory.NEUTRAL
        );

        register("minecraft:llama", "essence_llama",
                new TotemTooltipData("Spit Happens", "Hostility finds itself met with swift disdain, hurled from deep within"),
                SoundEvents.LLAMA_HURT,
                new LlamaAbility(),
                TotemMobCategory.NEUTRAL
        );

        register("minecraft:wolf", "essence_wolf",
                new TotemTooltipData("Pack Instinct", "Pain may awaken the ancient bond, then the fangs rush from the mist to defend their own. The marrow-born know this fear well"),
                SoundEvents.WOLF_HURT,
                new WolfAbility(),
                TotemMobCategory.NEUTRAL
        );

        register("minecraft:dolphin", "essence_dolphin",
                new TotemTooltipData("Wave Rider", "Carried by the ocean’s breath, your rush becomes a spiral of motion none should obstruct"),
                SoundEvents.DOLPHIN_HURT,
                new DolphinAbility(),
                TotemMobCategory.NEUTRAL
        );

        register("minecraft:polar_bear", "essence_polar_bear",
                new TotemTooltipData("Arctic Endurance", "The tundra lends its strength, snow welcomes your tread and your strikes may send tremors through the frost"),
                SoundEvents.POLAR_BEAR_HURT,
                new PolarBearAbility(),
                TotemMobCategory.NEUTRAL
        );

        register("minecraft:panda", "essence_panda",
                new TotemTooltipData("Temperamental Behavior", "Gentle by nature, fierce in defense. Soft stalks soothes the ache, and humble greens fortify the soul"),
                SoundEvents.PANDA_HURT,
                new PandaAbility(),
                TotemMobCategory.NEUTRAL
        );

        register("minecraft:piglin", "essence_piglin",
                new TotemTooltipData("Lust for Gold", "What shines in your satchel stirs both blade and fate; gold answers greed with power"),
                SoundEvents.PIGLIN_HURT,
                new PiglinAbility(),
                TotemMobCategory.NEUTRAL
        );

        register("minecraft:zombified_piglin", "essence_zombified_piglin",  //todo usar "and pain may rouse the wrath of the rotting tusk" para el Zoglin
                new TotemTooltipData("Hell's wrath", "The sky holds no sway over you, and pain may rouse the wrath of the rotting death"),
                SoundEvents.ZOMBIFIED_PIGLIN_HURT,
                new ZombifiedPiglinAbility(),
                TotemMobCategory.NEUTRAL
        );

        // HOSTILE
        register("minecraft:zombie", "essence_zombie",
                new TotemTooltipData("Zombie Recall", "The grave does not yet hold your name; should a blow strike true, the husk stirs once more"),
                SoundEvents.ZOMBIE_HURT,
                new ZombieAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:creeper", "essence_creeper",
                new TotemTooltipData("Explosive Retaliation", "Strike too close, and the air turns hostile. What lies dormant within may burst when stirred"),
                SoundEvents.CREEPER_HURT,
                new CreeperAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:skeleton", "essence_skeleton",
                new TotemTooltipData("Bone Quiver", "The string bends to your will with unnatural haste"),
                SoundEvents.SKELETON_HURT,
                new SkeletonAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:slime", "essence_slime",
                new TotemTooltipData("Elastic Body", "The earth repels you like a heartbeat, while the blow sinks deep and fades into you"),
                SoundEvents.SLIME_HURT,
                new SlimeAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:magma_cube", "essence_magma_cube",
                new TotemTooltipData("Igneous Elastic Body", "The ground beats back against your landing, and the careless find their courage kissed by flame"),
                SoundEvents.MAGMA_CUBE_HURT,
                new MagmaCubeAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:blaze", "essence_blaze",
                new TotemTooltipData("Infernal Drift", "When shadows fall beneath your feet, step softly to ascend the breeze, while flames awaken where your fingers met"),
                SoundEvents.BLAZE_HURT,
                new BlazeAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:phantom", "essence_phantom",
                new TotemTooltipData("Twilight Spiral Drifting", "Cursed wings no longer haunt your nights, and the wind coils ‘round your form in sudden burst"),
                SoundEvents.PHANTOM_HURT,
                new PhantomAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:silverfish", "essence_silverfish",
                new TotemTooltipData("Infested Strike", "The soil remembers your enemies' steps, causing the silver plague to unearth itself"),
                SoundEvents.SILVERFISH_HURT,
                new SilverfishAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:endermite", "essence_endermite",
                new TotemTooltipData("Dimensional Call", "The faintest tear in the fabric of being calls forth the void plague’s embrace"),
                SoundEvents.ENDERMITE_HURT,
                new EndermiteAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:drowned", "essence_drowned",
                new TotemTooltipData("Abyssal Salvation", "When breath fails and stone presses close, the deep grants mercy"),
                SoundEvents.DROWNED_HURT,
                new DrownedAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:husk", "essence_husk",
                new TotemTooltipData("Scorched Salvation", "Fire may take you, but ash will not hold you. Hunger gnaws in vain at what refuses to wither"),
                SoundEvents.HUSK_HURT,
                new HuskAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:guardian", "essence_guardian",
                new TotemTooltipData("Gaze of the Deep", "The stare of the deep draws strength away from the bold; and in the strike’s wake, the sea answers with a borrowed breath"),
                SoundEvents.GUARDIAN_HURT,
                new GuardianAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:stray", "essence_stray",
                new TotemTooltipData("Winter's Draw", "Cold sinews hasten the pull, and frost may ride silently upon the string"),
                SoundEvents.STRAY_HURT,
                new StrayAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:evoker", "essence_evoker",
                new TotemTooltipData("Wicked Covenant", "Let them taste your wrath... Devils and Fangs shall strike those who dare attack you"),
                SoundEvents.EVOKER_HURT,
                new EvokerAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:pillager", "essence_pillager",
                new TotemTooltipData("Beastmaster's Wrath", "The colossal beasts heed your command, and perched atop, the locked tension sings, delivering thunderous strikes with unerring force"),
                SoundEvents.PILLAGER_HURT,
                new PillagerAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:vindicator", "essence_vindicator",
                new TotemTooltipData("Ravaging Axe", "The beast does not question your grip on the reins and the cleaving strike rings truer from its throne. Blood quickens when prey draws near, iron hungers, and your step grows swift"),
                SoundEvents.VINDICATOR_HURT,
                new VindicatorAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:ghast", "essence_ghast",
                new TotemTooltipData("Sorrow Flame", "Pain echoes through the air; the lament may return in flame, and the soul mend quietly in its wake"),
                SoundEvents.GHAST_HURT,
                new GhastAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:shulker", "essence_shulker",
                new TotemTooltipData("Armored Skin", "Shell like stone, castings turned away; stillness becomes bastion, and defiance may answer in drifting wrath"),
                SoundEvents.SHULKER_HURT,
                new ShulkerAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:wither_skeleton", "essence_wither_skeleton",
                new TotemTooltipData("Withering Touch", "Decay may cling to your blows, and searing flames bend before the mark of the withered"),
                SoundEvents.WITHER_SKELETON_HURT,
                new WitherSkeletonAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:piglin_brute", "essence_piglin_brute",
                new TotemTooltipData("Golden Tenacity", "When the cleaver finds its mark, hands grow uncertain, and below half blood, the rhythm of the swing hastens. Gold, obeying only its chosen, strikes with uncanny ease, and when resolve wanes, it answers with cruel brilliance"),
                SoundEvents.PIGLIN_BRUTE_HURT,
                new PiglinBruteAbility(),
                TotemMobCategory.HOSTILE
        );

        register("minecraft:hoglin", "essence_hoglin",
                new TotemTooltipData("Savage Slam", "The wild kin sense no threat in your scent. Your footing holds firm through the storm and when fury surges, foes rise like leaves on the wind"),
                SoundEvents.HOGLIN_HURT,
                new HoglinAbility(),
                TotemMobCategory.HOSTILE
        );

        // SPECIAL
        register("minecraft:zombie_villager", "essence_zombie_villager",
                new TotemTooltipData("Undead Scent of the Money", "The cursed merchant’s shadow hungers still; its crooked scent guides toward buried promise, and death is but a fleeting bargain"),
                SoundEvents.ZOMBIE_VILLAGER_HURT,
                new ZombieVillagerAbility(),
                TotemMobCategory.SPECIAL
        );

        register("minecraft:wandering_trader", "essence_wandering_trader",
                new TotemTooltipData("Llama Whisperer", "Through untraveled roads, the murmurs of the nomad tame both temper and tread"),
                SoundEvents.WANDERING_TRADER_HURT,
                new WanderingTraderAbility(),
                TotemMobCategory.SPECIAL
        );

        register("minecraft:trader_llama", "essence_trader_llama",
                new TotemTooltipData("Caravan Aura", "The caravan’s watch needs no command; in measured breath, it drives away the bold, and gentler feet gather in its wake"),
                SoundEvents.LLAMA_HURT,
                new TraderLlamaAbility(),
                TotemMobCategory.SPECIAL
        );

        register("minecraft:red_mooshroom", "essence_red_mooshroom",
                new TotemTooltipData("Cleansing Feast", "In the hush of the brown grove, misfortune often turns away, and the meekest morsel may swell the belly"),
                SoundEvents.COW_HURT,
                new RedMooshroomAbility(),
                TotemMobCategory.SPECIAL
        );

        register("minecraft:brown_mooshroom", "essence_brown_mooshroom",
                new TotemTooltipData("Enriched Cleanse", "Beneath the earth-toned crown, afflictions may falter at your door, and each mouthful plants endurance in the soul"),
                SoundEvents.COW_HURT,
                new BrownMooshroomAbility(),
                TotemMobCategory.SPECIAL
        );

        register("minecraft:mule", "essence_mule",
                new TotemTooltipData("Leaping Hauler", "The burdened runner gathers strength with every stride, guards its trove as one, and strikes swift at the coward’s reach"),
                SoundEvents.MULE_HURT,
                new MuleAbility(),
                TotemMobCategory.SPECIAL
        );

        register("minecraft:zombie_horse", "essence_zombie_horse",
                new TotemTooltipData("Undead Power Leap", "In the gallop of the grave, sinew winds for the sky, the hind hoof strikes true, and death is but a pause"),
                SoundEvents.ZOMBIE_HORSE_HURT,
                new ZombieHorseAbility(),
                TotemMobCategory.SPECIAL
        );

        register("minecraft:skeleton_horse", "essence_skeleton_horse",
                new TotemTooltipData("Leaping Bone Quiver", "The hollow steed springs skyward with the long run, hurls its heel at the creeping hand, and looses death before the string can sing"),
                SoundEvents.SKELETON_HORSE_HURT,
                new SkeletonHorseAbility(),
                TotemMobCategory.SPECIAL
        );

        register("minecraft:witch", "essence_witch",
                new TotemTooltipData("Coven's Blessing", "The covenant’s shadow softens the claws of the small, turns certain pains to whispers, and from hidden folds, the right draught finds your hand"),
                SoundEvents.WITCH_HURT,
                new WitchAbility(),
                TotemMobCategory.SPECIAL
        );

        register("minecraft:elder_guardian", "essence_elder_guardian",
                new TotemTooltipData("Ominous Look", "The ancient eye weighs heavy on the bold, and the depths offer no chains to your hands"),
                SoundEvents.ELDER_GUARDIAN_HURT,
                new ElderGuardianAbility(),
                TotemMobCategory.SPECIAL
        );

        register("minecraft:vex", "essence_vex",
                new TotemTooltipData("Spectral Stride", "While sprinting, dash through walls that are one block thick while sprinting and chance of dealing a critical blow (1/3)"),
                SoundEvents.VEX_HURT,
                new VexAbility(),
                TotemMobCategory.SPECIAL
        );

        register("minecraft:ravager", "essence_ravager",
                new TotemTooltipData("Sturdy Beast", "Tackle any mob in your way while running + 50% less knockback received + when melee attacked, chance to roar and make any mob in the surroundings take damage and knockback"),
                SoundEvents.RAVAGER_HURT,
                new RavagerAbility(),
                TotemMobCategory.SPECIAL
        );

        register("minecraft:iron_golem", "essence_iron_golem",
                new TotemTooltipData("Steelbound", "When melee attacking, chance to launch enemies into the air (1/4) + consume iron ingots to heal half a heart + 1/4 chance to block melee attacks, but for the next 5 seconds any hit deals double [this persists even after unequipping the totem]"),
                SoundEvents.IRON_GOLEM_HURT,
                new IronGolemAbility(),
                TotemMobCategory.SPECIAL
        );

        register("minecraft:snow_golem", "essence_snow_golem",
                new TotemTooltipData("Snowball Fight", "Immunity to freezing + walk over powder snow blocks + automatically shoot snowballs at hostile mobs nearby, dealing knockback (every 2 seconds)"),
                SoundEvents.SNOW_GOLEM_HURT,
                new SnowGolemAbility(),
                TotemMobCategory.SPECIAL
        );

        register("minecraft:warden", "essence_warden",
                new TotemTooltipData("Rage Pulse", "With each wound, the fury inside grows. Strike harder, endure longer. \nEmbrace darkness to sense nearby souls"),
                SoundEvents.WARDEN_HURT,
                new WardenAbility(),
                TotemMobCategory.SPECIAL
        );

        register("minecraft:zoglin", "essence_zoglin",  //todo usar "and pain may rouse the wrath of the rotting tusk" para el Zoglin
                new TotemTooltipData("Hell's Savage Slam", "Reduced knockback + zoglins ignore the player + chance to launch the target into the air when melee hit (1/3) + when damaged, chance of summoning a zoglin targeting the attacker (1/12)"),
                SoundEvents.ZOGLIN_HURT,
                new ZoglinAbility(),
                TotemMobCategory.SPECIAL
        );

        register("minecraft:killer_bunny", "essence_killer_bunny",
                new TotemTooltipData("Killing Frenzy", "Bound by restless leaps, each fallen shadow may fuel a sudden spark within"),
                SoundEvents.RABBIT_HURT,
                new RabbitAbility(),
                TotemMobCategory.SPECIAL
        );

        register("minecraft:illusioner", "essence_illusioner",
                new TotemTooltipData("???", "???"),
                SoundEvents.ILLUSIONER_HURT,
                new IllusionerAbility(),
                TotemMobCategory.SPECIAL
        );

        // BOSS
        register("minecraft:wither", "essence_wither",
                new TotemTooltipData("Withering Destruction", "Hurl death from its hollow grin. Death protects, and arrows turn"),
                SoundEvents.WITHER_HURT,
                new WitherAbility(),
                TotemMobCategory.BOSS
        );

        register("minecraft:ender_dragon", "essence_ender_dragon",
                new TotemTooltipData("Breath of the Void", "The rods of the void awaken near the dragon, pulsing with ancient power and restoring the flesh. Rise with wings unseen, then fall—carving the ground with dragon’s breath, untouched by its fire"),
                SoundEvents.ENDER_DRAGON_HURT,
                new EnderDragonAbility(),
                TotemMobCategory.BOSS
        );

        // NON MOB
        register("minecraft:armor_stand", "essence_armor_stand",
                new TotemTooltipData("Soulless Entity", "Become as still as stone. In absolute immobility, even danger forgets you exist"),
                SoundEvents.ARMOR_STAND_HIT,
                new ArmorStandAbility(),
                TotemMobCategory.NON_MOB
        );

        register("minecraft:player", "essence_player",
                new TotemTooltipData("/cmd", "You feel like you’re cheating... but are you?"),
                SoundEvents.PLAYER_HURT,
                new PlayerAbility(),
                TotemMobCategory.NON_MOB
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
