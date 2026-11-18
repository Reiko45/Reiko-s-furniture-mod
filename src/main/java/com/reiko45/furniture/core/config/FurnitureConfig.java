package com.reiko45.furniture.core.config;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

/**
 * The mod's settings, in three files under {@code config/} (or {@code serverconfig/} in each world):
 * <ul>
 *   <li>{@code reikos_furniture-startup.toml}: read before blocks are registered (restart to change).</li>
 *   <li>{@code reikos_furniture-server.toml}: gameplay values, per world.</li>
 *   <li>{@code reikos_furniture-common.toml}: world generation.</li>
 * </ul>
 * The getters fall back to the defaults while a file isn't loaded yet.
 */
public final class FurnitureConfig {
    public static final Startup STARTUP;
    public static final ModConfigSpec STARTUP_SPEC;
    public static final Server SERVER;
    public static final ModConfigSpec SERVER_SPEC;
    public static final Common COMMON;
    public static final ModConfigSpec COMMON_SPEC;

    static {
        Pair<Startup, ModConfigSpec> startup = new ModConfigSpec.Builder().configure(Startup::new);
        STARTUP = startup.getLeft();
        STARTUP_SPEC = startup.getRight();
        Pair<Server, ModConfigSpec> server = new ModConfigSpec.Builder().configure(Server::new);
        SERVER = server.getLeft();
        SERVER_SPEC = server.getRight();
        Pair<Common, ModConfigSpec> common = new ModConfigSpec.Builder().configure(Common::new);
        COMMON = common.getLeft();
        COMMON_SPEC = common.getRight();
    }

    private FurnitureConfig() {
    }

    /** Translation key for a setting or section, used by the in-game config screen. */
    public static String key(String name) {
        return "config.reikos_furniture." + name;
    }

    /** English names for the config screen (added by the language provider). */
    public static final java.util.Map<String, String> NAMES = java.util.Map.of(
            "modded_woods", "Other Mods' Woods",
            "modded_woods.enabled", "Furniture in Other Mods' Woods",
            "modded_woods.styles", "Styles in Other Mods' Woods",
            "gameplay", "Gameplay",
            "laundry_cycle_seconds", "Laundry Cycle (Seconds)",
            "appliance_speed", "Appliance Speed",
            "safe_owner_only", "Safes Only Open for Their Owner",
            "dolly_carries_full_storage", "Dolly Carries Full Storage",
            "world", "World",
            "village_workshop_weight", "Village Workshop Weight");

    public static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.STARTUP, STARTUP_SPEC);
        container.registerConfig(ModConfig.Type.SERVER, SERVER_SPEC);
        container.registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
    }

    public static final class Startup {
        public final ModConfigSpec.BooleanValue moddedWoods;
        public final ModConfigSpec.BooleanValue moddedWoodStyles;

        Startup(ModConfigSpec.Builder builder) {
            builder.translation(key("modded_woods")).push("modded_woods");
            moddedWoods = builder
                    .comment("Make wooden furniture in the woods of other mods (like Biomes O' Plenty) too.",
                            "Every player and the server need the same setting.")
                    .translation(key("modded_woods.enabled"))
                    .define("enabled", true);
            moddedWoodStyles = builder
                    .comment("Also make the furniture styles (rustic, modern, royal, Japanese) in other mods' woods.",
                            "Turn this off if you have many wood mods and want fewer items.")
                    .translation(key("modded_woods.styles"))
                    .define("styles", true);
            builder.pop();
        }
    }

    public static final class Server {
        public final ModConfigSpec.IntValue laundryCycleSeconds;
        public final ModConfigSpec.DoubleValue applianceSpeed;
        public final ModConfigSpec.BooleanValue safeOwnerOnly;
        public final ModConfigSpec.BooleanValue dollyCarriesFullStorage;

        Server(ModConfigSpec.Builder builder) {
            builder.translation(key("gameplay")).push("gameplay");
            laundryCycleSeconds = builder
                    .comment("How long a washing machine or dryer cycle takes, in seconds.")
                    .translation(key("laundry_cycle_seconds"))
                    .defineInRange("laundry_cycle_seconds", 5, 1, 600);
            applianceSpeed = builder
                    .comment("Speed of the coffee machine, toaster, blender and espresso machine. 2.0 is twice as fast.")
                    .translation(key("appliance_speed"))
                    .defineInRange("appliance_speed", 1.0, 0.1, 10.0);
            safeOwnerOnly = builder
                    .comment("Only the player who placed a safe can open or break it.")
                    .translation(key("safe_owner_only"))
                    .define("safe_owner_only", true);
            dollyCarriesFullStorage = builder
                    .comment("The moving dolly can pick up storage furniture that still has items inside.")
                    .translation(key("dolly_carries_full_storage"))
                    .define("dolly_carries_full_storage", true);
            builder.pop();
        }
    }

    public static final class Common {
        public final ModConfigSpec.IntValue villageWorkshopWeight;

        Common(ModConfigSpec.Builder builder) {
            builder.translation(key("world")).push("world");
            villageWorkshopWeight = builder
                    .comment("How often villages have a furniture maker's workshop (vanilla houses use 1 to 10). 0 turns them off.")
                    .translation(key("village_workshop_weight"))
                    .defineInRange("village_workshop_weight", 2, 0, 50);
            builder.pop();
        }
    }

    // ---- Getters that work before the files are loaded ----------------------------------------

    public static boolean moddedWoods() {
        return STARTUP_SPEC.isLoaded() ? STARTUP.moddedWoods.get() : STARTUP.moddedWoods.getDefault();
    }

    public static boolean moddedWoodStyles() {
        return STARTUP_SPEC.isLoaded() ? STARTUP.moddedWoodStyles.get() : STARTUP.moddedWoodStyles.getDefault();
    }

    public static int laundryCycleTicks() {
        return 20 * (SERVER_SPEC.isLoaded() ? SERVER.laundryCycleSeconds.get() : SERVER.laundryCycleSeconds.getDefault());
    }

    /** An appliance recipe's time, adjusted by {@code appliance_speed}. */
    public static int applianceTicks(int recipeTicks) {
        double speed = SERVER_SPEC.isLoaded() ? SERVER.applianceSpeed.get() : SERVER.applianceSpeed.getDefault();
        return Math.max(1, (int) Math.round(recipeTicks / speed));
    }

    public static boolean safeOwnerOnly() {
        return SERVER_SPEC.isLoaded() ? SERVER.safeOwnerOnly.get() : SERVER.safeOwnerOnly.getDefault();
    }

    public static boolean dollyCarriesFullStorage() {
        return SERVER_SPEC.isLoaded() ? SERVER.dollyCarriesFullStorage.get() : SERVER.dollyCarriesFullStorage.getDefault();
    }

    public static int villageWorkshopWeight() {
        return COMMON_SPEC.isLoaded() ? COMMON.villageWorkshopWeight.get() : COMMON.villageWorkshopWeight.getDefault();
    }
}
