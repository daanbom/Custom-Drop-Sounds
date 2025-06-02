package org.CustomSounds;

import java.io.File;
import java.util.function.Supplier;

public class WeaponSound {
    public final File soundFile;
    public final Supplier<Boolean> configCheck;
    public final int delay;
    public final boolean isAnimationBased;

    public WeaponSound(File soundFile, Supplier<Boolean> configCheck, int delay, boolean isAnimationBased) {
        this.soundFile = soundFile;
        this.configCheck = configCheck;
        this.delay = delay;
        this.isAnimationBased = isAnimationBased;
    }
}