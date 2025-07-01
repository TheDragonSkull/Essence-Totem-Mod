package net.thedragonskull.mobessencemod.util;

import net.minecraft.network.chat.Style;


public enum TotemMobCategory {
    PASSIVE(0x88cb2c),      // lime
    NEUTRAL(0xe7ca3a),      // yellow
    HOSTILE(0xaf3b35),      // dark red
    SPECIAL(0x55c0d9),      // cyan
    NON_MOB(0xFFFFFF),      // white
    BOSS(0x101010);         // near black

    private final int color;

    TotemMobCategory(int hexColor) {
        this.color = hexColor;
    }

    public Style asStyle() {
        return Style.EMPTY.withColor(color);
    }
}

