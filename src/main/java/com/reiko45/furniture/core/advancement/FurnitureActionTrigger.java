package com.reiko45.furniture.core.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.reiko45.furniture.ReikosFurniture;
import java.util.Optional;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Advancement trigger {@code reikos_furniture:furniture_action}: fired from code when a player does
 * something with furniture (plays the piano, turns a piece with the hammer, ...). The criterion's
 * {@code action} picks which one; without it, any action counts.
 */
public class FurnitureActionTrigger extends SimpleCriterionTrigger<FurnitureActionTrigger.TriggerInstance> {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS =
            DeferredRegister.create(Registries.TRIGGER_TYPE, ReikosFurniture.MODID);
    public static final DeferredHolder<CriterionTrigger<?>, FurnitureActionTrigger> FURNITURE_ACTION =
            TRIGGERS.register("furniture_action", FurnitureActionTrigger::new);

    // The actions fired from code.
    public static final String HAMMER = "hammer";
    public static final String DOLLY = "dolly";
    public static final String PIANO = "piano";
    public static final String BULLSEYE = "bullseye";
    public static final String ARCADE_HIGH_SCORE = "arcade_high_score";
    public static final String WASH = "wash";
    public static final String PET_FED = "pet_fed";
    public static final String REPAINT = "repaint";
    public static final String LIGHT_SWITCH = "light_switch";
    public static final String MAIL = "mail";
    public static final String GREEN_THUMB = "green_thumb";
    public static final String MOVIE_NIGHT = "movie_night";
    public static final String THE_ARTIST = "the_artist";

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, String action) {
        trigger(player, instance -> instance.action().map(action::equals).orElse(true));
    }

    /** Fires {@code action} for a player on the server; does nothing on the client or for no player. */
    public static void award(Player player, String action) {
        if (player instanceof ServerPlayer serverPlayer) {
            FURNITURE_ACTION.get().trigger(serverPlayer, action);
        }
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<String> action)
            implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                Codec.STRING.optionalFieldOf("action").forGetter(TriggerInstance::action)
        ).apply(instance, TriggerInstance::new));

        public static Criterion<TriggerInstance> action(String action) {
            return FURNITURE_ACTION.get().createCriterion(new TriggerInstance(Optional.empty(), Optional.of(action)));
        }
    }
}
