package com.reiko45.furniture.material;

import net.minecraft.world.level.block.WeatheringCopper.WeatherState;

/** The four ages of copper, unwaxed and waxed, like vanilla copper blocks. */
public enum CopperStage {
    UNAFFECTED(WeatherState.UNAFFECTED, false),
    EXPOSED(WeatherState.EXPOSED, false),
    WEATHERED(WeatherState.WEATHERED, false),
    OXIDIZED(WeatherState.OXIDIZED, false),
    WAXED(WeatherState.UNAFFECTED, true),
    WAXED_EXPOSED(WeatherState.EXPOSED, true),
    WAXED_WEATHERED(WeatherState.WEATHERED, true),
    WAXED_OXIDIZED(WeatherState.OXIDIZED, true);

    private final WeatherState age;
    private final boolean waxed;

    CopperStage(WeatherState age, boolean waxed) {
        this.age = age;
        this.waxed = waxed;
    }

    public WeatherState age() {
        return age;
    }

    public boolean waxed() {
        return waxed;
    }

    /** {@code waxed_exposed_} and so on, put in front of {@code copper_<piece>}. */
    public String prefix() {
        String age = this.age == WeatherState.UNAFFECTED ? "" : this.age.name().toLowerCase(java.util.Locale.ROOT) + "_";
        return (waxed ? "waxed_" : "") + age;
    }

    /** The vanilla copper texture for this age. */
    public String texture() {
        return switch (age) {
            case UNAFFECTED -> "minecraft:block/copper_block";
            case EXPOSED -> "minecraft:block/exposed_copper";
            case WEATHERED -> "minecraft:block/weathered_copper";
            case OXIDIZED -> "minecraft:block/oxidized_copper";
        };
    }

    /** The same age, unwaxed or waxed. */
    public CopperStage withWax(boolean waxed) {
        return values()[age.ordinal() + (waxed ? 4 : 0)];
    }

    /** The next age (unwaxed only), or null when fully oxidized or waxed. */
    public CopperStage next() {
        return waxed || age == WeatherState.OXIDIZED ? null : values()[ordinal() + 1];
    }
}
