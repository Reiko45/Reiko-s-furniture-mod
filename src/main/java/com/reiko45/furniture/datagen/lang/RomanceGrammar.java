package com.reiko45.furniture.datagen.lang;

import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.Style;
import java.util.Map;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;

/**
 * Spanish, French and Portuguese names: the noun first, then the style and color (agreeing with the
 * noun's gender), then the wood: "Cama rústica de roble", "Canapé royal rouge", "Sofá Moderno Azul".
 */
abstract class RomanceGrammar extends Grammar {
    /** Wood words for masculine and feminine nouns, in vanilla wood order ("de roble", ..., "distorsionado"). */
    protected abstract String[] woodsMasculine();

    protected abstract String[] woodsFeminine();

    /** Color words in dye color order. */
    protected abstract String[] colorsMasculine();

    protected abstract String[] colorsFeminine();

    /** Style words: masculine and feminine form for each style. */
    protected abstract Map<Style, String[]> styles();

    @Override
    public String name(Noun noun, @Nullable FurnitureWood wood, @Nullable DyeColor color, @Nullable Style style) {
        boolean feminine = noun.gender() == Gender.F;
        StringBuilder name = new StringBuilder(noun.text());
        if (style != null) {
            name.append(' ').append(styles().get(style)[feminine ? 1 : 0]);
        }
        if (color != null) {
            name.append(' ').append((feminine ? colorsFeminine() : colorsMasculine())[color.ordinal()]);
        }
        if (wood != null) {
            name.append(' ').append((feminine ? woodsFeminine() : woodsMasculine())[index(wood)]);
        }
        return name.toString();
    }
}
