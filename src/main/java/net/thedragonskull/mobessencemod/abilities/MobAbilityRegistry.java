package net.thedragonskull.mobessencemod.abilities;

import java.util.HashMap;
import java.util.Map;

public class MobAbilityRegistry {

    private static final Map<String, IMobAbility> ABILITIES = new HashMap<>();

    static {
        ABILITIES.put("minecraft:pig", new PigAbility());
        ABILITIES.put("minecraft:bee", new BeeAbility());
    }

    public static IMobAbility getAbility(String essenceId) {
        return ABILITIES.get(essenceId);
    }
}

