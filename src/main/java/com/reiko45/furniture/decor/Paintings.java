package com.reiko45.furniture.decor;

import com.reiko45.furniture.ReikosFurniture;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.decoration.PaintingVariant;

/**
 * The mod's paintings. They are painting variants (data, not blocks), so the vanilla painting item
 * places them too. Their pictures are drawn by {@code tools/textures} into {@code textures/painting/}.
 */
public final class Paintings {
    /** A painting: its name, size in blocks and English title. */
    public record Art(String name, int width, int height, String title) {
        public ResourceKey<PaintingVariant> key() {
            return ResourceKey.create(Registries.PAINTING_VARIANT, ReikosFurniture.id(name));
        }
    }

    public static final List<Art> ALL = List.of(
            new Art("sunset", 2, 1, "Sunset"),
            new Art("mountains", 2, 1, "Mountains"),
            new Art("meadow", 1, 1, "Meadow"),
            new Art("still_life", 1, 1, "Still Life"),
            new Art("shapes", 1, 1, "Shapes"),
            new Art("cat", 1, 1, "Cat"),
            new Art("city_lights", 4, 2, "City Lights"),
            new Art("lighthouse", 1, 2, "Lighthouse"),
            new Art("big_wave", 2, 2, "Big Wave"),
            new Art("night_hills", 2, 2, "Night Hills"),
            new Art("fruit_bowl", 2, 1, "Fruit Bowl"),
            new Art("sailboat", 1, 1, "Sailboat"));

    private Paintings() {
    }

    /** Writes the painting variants (used by data generation). */
    public static void bootstrap(BootstrapContext<PaintingVariant> context) {
        for (Art art : ALL) {
            context.register(art.key(), new PaintingVariant(art.width(), art.height(), ReikosFurniture.id(art.name())));
        }
    }
}
