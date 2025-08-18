package net.thedragonskull.mobessencemod.util;

public class ChestDropControl {
    public static final ThreadLocal<Boolean> SUPPRESS_DROPS = ThreadLocal.withInitial(() -> false);
}
